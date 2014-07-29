package com.luxsoft.cfdi

import com.luxsoft.kio.Venta
import grails.transaction.Transactional

import mx.gob.sat.cfd.x3.ComprobanteDocument
import mx.gob.sat.cfd.x3.ComprobanteDocument.Comprobante;
import mx.gob.sat.cfd.x3.ComprobanteDocument.Comprobante.Conceptos;
import mx.gob.sat.cfd.x3.ComprobanteDocument.Comprobante.Conceptos.Concepto;
import mx.gob.sat.cfd.x3.ComprobanteDocument.Comprobante.Conceptos.Concepto.CuentaPredial;
import mx.gob.sat.cfd.x3.ComprobanteDocument.Comprobante.Emisor;
import mx.gob.sat.cfd.x3.ComprobanteDocument.Comprobante.Impuestos;
import mx.gob.sat.cfd.x3.ComprobanteDocument.Comprobante.Impuestos.Traslados;
import mx.gob.sat.cfd.x3.ComprobanteDocument.Comprobante.Impuestos.Traslados.Traslado;
import mx.gob.sat.cfd.x3.ComprobanteDocument.Comprobante.Receptor;
import mx.gob.sat.cfd.x3.ComprobanteDocument.Comprobante.TipoDeComprobante;


import org.apache.xmlbeans.XmlObject
import org.apache.xmlbeans.XmlOptions
import org.apache.xmlbeans.XmlValidationError

import org.apache.commons.lang.StringUtils
import org.bouncycastle.util.encoders.Base64

@Transactional
class CfdiService {
	
	def grailsApplication
	
	def cfdiSellador
	def cfdiTimbrador
	
	def empresa

    def Cfdi generar(Venta venta) {
		assert venta.status=='VENTA',"La venta debe tener status VENTA status actual: $venta.status"
		
		if(empresa==null){
			empresa=Empresa.first();
			assert empresa,"La empresa debe estar definida"
		}
		def serie=grailsApplication.config.luxsoft.cfdi.serie.venta
		assert serie,"Debe registrar la serie para cfdi de ventas 'luxsoft.cfdi.serie.venta'"
		def cfdiFolio=CfdiFolio.findBySerie(serie)
		assert cfdiFolio,"Debe exister folios para la serie: "+serie
		              
		def folio=cfdiFolio.next()
		
		log.info "Generando CFDI folio:$folio  Serie:$serie y rfc:$empresa.rfc  Para venta $venta.id"
		
		ComprobanteDocument document=ComprobanteDocument.Factory.newInstance()
		Comprobante comprobante=document.addNewComprobante()
		comprobante.serie=serie
		comprobante.folio=folio
		CfdiUtils.depurar(document)
		def fecha=new Date()
		
		comprobante.setVersion("3.2")
		comprobante.setFecha(CfdiUtils.toXmlDate(fecha).getCalendarValue())
		comprobante.setFormaDePago("PAGO EN UNA SOLA EXHIBICION")
		comprobante.setMetodoDePago(venta.formaDePago)
		comprobante.setMoneda(venta.moneda.getCurrencyCode())
		comprobante.setTipoCambio("1.0")
		
		comprobante.setTipoDeComprobante(TipoDeComprobante.INGRESO)
		comprobante.setLugarExpedicion(empresa.direccion.municipio)
		comprobante.setNoCertificado(empresa.numeroDeCertificado)
		
		Emisor emisor=CfdiUtils.registrarEmisor(comprobante, empresa)
		Receptor receptor=CfdiUtils.registrarReceptor(comprobante, venta.cliente)
		
		comprobante.setSubTotal(venta.importeNeto)
		comprobante.setDescuento(venta.descuento)
		comprobante.setTotal(venta.total)
		
		Impuestos impuestos=comprobante.addNewImpuestos()
		String rfc=comprobante.getReceptor().getRfc()
		
		//Facturacion a clientes extranjero
		if(rfc=="XEXX010101000"){
			comprobante.setSubTotal(source.importe)
			comprobante.setTotal(source.subtotal)
		}else if(rfc=="XAXX010101000"){
			comprobante.setSubTotal(venta.importeBruto*(1+MonedaUtils.IVA))
			comprobante.setDescuento(venta.descuento*(1+MonedaUtils.IVA))
			comprobante.setTotal(venta.total)
		}else{
			impuestos.setTotalImpuestosTrasladados(venta.impuesto)
			Traslados traslados=impuestos.addNewTraslados()
			Traslado traslado=traslados.addNewTraslado()
			traslado.setImpuesto(Traslado.Impuesto.IVA)
			traslado.setImporte(source.impuestos)
			traslado.setTasa(MonedaUtils.IVA*100)
		}
		
		Conceptos conceptos=comprobante.addNewConceptos()
		
		venta.partidas.each {det->
			
			Concepto c=conceptos.addNewConcepto()
			c.setCantidad(det.cantidad)
			c.setUnidad(det.producto.unidad)
			c.setNoIdentificacion(det.producto.clave)
			String desc = det.producto.descripcion
			if(det.servicioPorSocio){
				//desc = (new StringBuilder(String.valueOf(desc))).append(StringUtils.stripToEmpty(det.comentario)).toString()
				desc+=" Socio:$det.servicioPorSocio.socio.id"
			}
			desc = StringUtils.abbreviate(desc, 250)
			c.setDescripcion(desc)
			c.setValorUnitario(det.precioUnitario)
			c.setImporte(det.importeNeto)
			
		}
		
		byte[] encodedCert=Base64.encode(empresa.getCertificado().getEncoded())
		comprobante.setCertificado(new String(encodedCert))
		comprobante.setSello(cfdiSellador.sellar(empresa.privateKey,document))
		
		XmlOptions options = new XmlOptions()
		options.setCharacterEncoding("UTF-8")
		options.put( XmlOptions.SAVE_INNER )
		options.put( XmlOptions.SAVE_PRETTY_PRINT )
		options.put( XmlOptions.SAVE_AGGRESSIVE_NAMESPACES )
		options.put( XmlOptions.SAVE_USE_DEFAULT_NAMESPACE )
		options.put(XmlOptions.SAVE_NAMESPACES_FIRST)
		ByteArrayOutputStream os=new ByteArrayOutputStream()
		document.save(os, options)
		
		
		validarDocumento(document)
		log.debug 'ComprobanteDocument generado y validado: '+document
		
		def cfdi=new Cfdi(comprobante)
		cfdi.tipo="FACTURA"
		cfdi.origen=venta.id.toString()
		cfdi.xml=os.toByteArray()
		cfdi.setXmlName("$cfdi.receptorRfc-$cfdi.serie-$cfdi.folio"+".xml")
		//cfdi.cadenaOriginal
		
		log.debug 'ComprobanteDocument generado y validado: '+document
		cfdi.save(failOnError:true)
		cfdi=cfdiTimbrador.timbrar(cfdi,empresa)
		venta.status='FACTURADA'
		venta.cfdi=cfdi
		log.debug 'Documento timbrado: '+cfdi
		return cfdi
		
    }
	
	void validarDocumento(ComprobanteDocument document) {
		List<XmlValidationError> errores=findErrors(document);
		if(errores.size()>0){
			StringBuffer buff=new StringBuffer();
			for(XmlValidationError e:errores){
				buff.append(e.getMessage()+"\n");
			}
			throw new RuntimeException(message:"Datos para generar el comprobante fiscal (CFDI) incorrectos "+buff.toString());
		}
	}
	
	List findErrors(final XmlObject node){
		final XmlOptions options=new XmlOptions();
		final List errors=new ArrayList();
		options.setErrorListener(errors);
		node.validate(options);
		return errors;
		
	}
	
	
	
}
