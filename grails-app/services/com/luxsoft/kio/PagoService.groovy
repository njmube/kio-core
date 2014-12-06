package com.luxsoft.kio

import grails.transaction.Transactional
import org.apache.commons.lang.time.DateUtils

@Transactional
class PagoService {

    def save(Pago pago){
        pago.save failOnError:true
        actualizarDisponible pago
        actualizarSaldos pago
        return pago
    }

    def registrarPago(Cobro cobro) {
    	def pago=new Pago(cliente:cobro.cliente
    		,fecha:cobro.fecha
    		,formaDePago:cobro.formaDePago
    		,referenciaBancaria:cobro.referencia
    		,importe:cobro.importe
    		,aplicado:0.0)
    	pago.addToAplicaciones(fecha:new Date(),importe:cobro.importe,venta:cobro.venta)
    	pago.save failOnError:true
    	actualizarDisponible(pago)
    	actualizarSaldos(pago)
    	log.info 'Pago generado: '+pago
    	return pago

    }

    def actualizarDisponible(Pago pago){
        if(pago.aplicaciones)
    	   pago.aplicado=pago.aplicaciones.sum 0.0,{it.importe}
        else{
            pago.aplicado=0.0 
        }
    }
    def actualizarSaldos(Pago pago){
        if(pago.aplicaciones){
            def ventas=pago.aplicaciones.collect{it.venta}
            ventas.each{ venta->
                venta.pagos=Pago.executeQuery("select sum(importe) from AplicacionDePago a where a.venta=?",[venta])[0]
                venta.status='PAGADA'
            }
        }
    }

    def agregarAplicacion(Pago pago,Venta venta,Date fecha,BigDecimal importe,String comentario){
        pago.addToAplicaciones(venta:venta,importe:importe,comentario:comentario,fecha:fecha)
        pago.save failOnError:true
        actualizarSaldos(pago)
        actualizarDisponible(pago)
        //registrarPagoDeMembresia(venta,fecha)
        log.info 'Aplicacion de pago registrada '
        return pago
    }


    def eliminarAplicacion(AplicacionDePago aplicacion){
        def pago=aplicacion.pago
        def ventas=pago.aplicaciones.collect{it.venta}
        pago.removeFromAplicaciones(aplicacion)
        pago.save flush:true
        ventas.each{ venta->
            def aplicaciones=AplicacionDePago.executeQuery("select sum(importe) from AplicacionDePago a where a.venta=?",[venta])[0]
            venta.pagos=aplicaciones?:0.0
            if(venta.pagos==0.0){
                venta.status='VENTA'
            }
            venta.save()
            log.info "Saldo actuaizado ${venta.saldo}  (Pagos: ${venta.pagos}) "
        }
        actualizarDisponible(pago)
        log.info 'Aplicacion eliminada '+aplicacion.id
        return pago
    }

    def delete(Pago pago){
    	def ventas=pago.aplicaciones.collect{it.venta}
       
    	pago.delete flush:true

    	ventas.each{ venta->
    		def aplicaciones=AplicacionDePago.executeQuery("select sum(importe) from AplicacionDePago a where a.venta=?",[venta])[0]
    		venta.pagos=aplicaciones?:0.0
            if(venta.pagos==0.0){
                venta.status='VENTA'
            }
    		venta.save()
    		log.info "Saldo actuaizado ${venta.saldo}  (Pagos: ${venta.pagos}) "
    	}
    }

    def actualizarMembresias(Long pagoId){
        def pago=Pago.get(pagoId)
        pago.aplicaciones.each{a->
            actualizarMembresia(a)
        }
    }

    def actualizarMembresia(AplicacionDePago a){
        def venta=a.venta
        venta.partidas.each{det ->
            if(det.producto.tipo.clave=='MEMBRESIA' && (det.socio)){

                def socio=det.socio
                if(socio.membresia.diaDeCorte==0){
                    socio.membresia.diaDeCorte=a.fecha.getAt(Calendar.DATE)
                }

                def pagoLog=new PagoDeMembresiaLog(
                    aplicacion:a
                    ,membresia:socio.membresia
                    ,servicio:det.producto
                    ,diaDeCorte:socio.membresia.diaDeCorte
                    ,ultimoPago:socio.membresia.ultimoPago
                    ,proximoPago:socio.membresia.proximoPago
                    ,activo:socio.activo
                )

                
                
                def diaDeCorte=socio.membresia.diaDeCorte
                def fechaOriginalDePago=socio.membresia.proximoPago?:a.fecha
                def duracion=det.producto.duracion?:1
                def proximoPago=MembresiaUtils.calcularProximoPago(fechaOriginalDePago,diaDeCorte,duracion)
                log.info "Actualizando socio: $socio.numeroDeSocio Servicio:$det.producto.clave Duracion:$duracion mes"
                log.info "Corte:$diaDeCorte Fecha de pago:${fechaOriginalDePago.format('dd/MM/yyyy')} Proximo:${proximoPago.format('dd/MM/yyyy')}"
                
                socio.membresia.ultimoPago=a.fecha
                socio.membresia.proximoPago=proximoPago
                if(!socio.activo)
                    socio.activo=true
                pagoLog.save failOnError:true
                //socio.save failOnError:true

            }
        }
    }

    def cancelarPagoDeMembresias(Long pagoId){
        def pago=Pago.get(pagoId)
        pago.aplicaciones.each{a->
            cancelarPagoDeMembresias(a)
        }
    }

    def cancelarPagoDeMembresias(AplicacionDePago a){
        def venta=a.venta
        venta.partidas.each{det ->
            if(det.producto.tipo.clave=='MEMBRESIA' && (det.socio)){

                def socio=det.socio
                def log=buscarUltimoPago(socio.membresia)
                if(log){
                    socio.membresia.ultimoPago=log.ultimoPago
                    socio.membresia.proximoPago=log.proximoPago
                    socio.activo=log.activo
                }
                
            }
        }
    }


    def PagoDeMembresiaLog buscarUltimoPago(SocioMembresia m){
        return PagoDeMembresiaLog.find("from PagoDeMembresiaLog p where p.membresia=? order by p.id desc",[m])
    }

    def registrarPagoDeMembresia(Venta venta,Date fecha){
        
        def found=venta.partidas.find{it.producto.tipo.clave=='MEMBRESIA'}
        if(found){
            //Actualizando la membresia
            
            if(servicio.duracion){
                def servicio=found.producto
                Integer duracion=servicio.duracion?:1

                def socio=it.socio
                def m=socio.membresia
                def fpago=m.proximoPago?:new Date()
                def proximo=DateUtils.addMonths(fpago,duracion)
                m.proximoPago=proximo
                m.ultimoPago=fecha
                m.save()
                if(socio.activo==false){
                    socio.activo=true
                    socio.save()
                } 
            }
            
        }
    }

    def cancelarAplicacionAMembresiaDeSocio(Venta venta,Date fecha){
       
        def found=venta.partidas.find{it.producto.tipo.clave=='MEMBRESIA'}
        if(found){
            //Actualizando la membresia
            println 'Actualizando membresia a partir de la aplicacion de un pago......'+found.producto
            if(servicio.duracion){
                def servicio=found.producto
                def duracion=Math.round(servicio.duracion/30.4)
                def socio=it.socio
                def m=socio.membresia
                def fpago=m.proximoPago
                if(fpago){
                    def ul=DateUtils.addMonths(fpago,-duracion)
                    m.proximoPago=ul
                    m.ultimoPago=null
                    m.save()
                    if(m.getAtraso()>=m.getTolerancia()){
                        socio.activo=false
                        socio.save()
                    }
                }
            }
            
            
        }
    }
}
