package com.luxsoft.kio

import grails.transaction.Transactional
import org.springframework.beans.BeanUtils
import com.luxsoft.cfdi.CfdiFolio
import org.apache.commons.lang.StringUtils

@Transactional
class SocioService {

    def salvarSocio(Socio socio) {
        
    	socio.validate()
    	if(socio.hasErrors()){
    		throw new SocioError(message:'Errores de validacion en socio',socio:socio)
    	}
        
        //log.debug 'Direccion de socio: '+socio.direccion
        def cliente=socio.cliente
        
        if(!cliente.id){
            cliente.nombre="$socio.nombres $socio.apellidoPaterno $socio.apellidoMaterno"
            cliente.tipo=TipoDeCliente.first()
            if(!cliente.validate(['rfc'])){
                cliente.rfc='XAXX010101000'
            }
            cliente.validate()
            if(cliente.hasErrors()){
                log.error("Validation errors: "+cliente.errors)
                throw new ClienteException(message:'Errores de validacion en alta de cliente',cliente:cliente)
            }
            cliente.save flush:true
            socio.cliente=cliente
        }
        socio.membresia.validate()
        if(socio.membresia.hasErrors()){
            println 'Errores en validacion de membresia: '+socio.membresia.errors
        }
        if(socio.id==null){
            def folio=CfdiFolio.findBySerie('SOCIOS')
            if(folio==null){
                folio=new CfdiFolio(serie:'SOCIOS',folio:0)
                folio.save flush:true
            }
            socio.numeroDeSocio=StringUtils.leftPad(folio.next().toString(),5,'0')
        }
        
    	socio=socio.save failOnError:true
        
    	return socio
    }

    def actualizarFoto(Socio socio){
        socio.save()
        return socio
    }

    def delete(Socio socio){
        socio.delete flush:true
    }
    /*
    Cliente createCliente(Socio socio){
        if(cliente==null){
            def target=new Direccion()
            BeanUtils.copyProperties(socio.direccion,target)
            cliente=new Cliente(
                nombre:"$apellidoPaterno $apellidoMaterno $nombres ",
                rfc:"'XAXX010101000'",
                direccion:target,
                tipo:TipoDeCliente.findByClave('MOSTRADOR')

            )
        }
    }
    */
}


class SocioError extends RuntimeException{
	
	String message
	Socio socio


}



