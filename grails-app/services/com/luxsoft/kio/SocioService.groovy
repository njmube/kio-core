package com.luxsoft.kio

import grails.transaction.Transactional

import org.springframework.beans.BeanUtils

import com.luxsoft.cfdi.CfdiFolio

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.math.NumberUtils

@Transactional
class SocioService {

    def salvarSocio(Socio socio) {
        if(socio.id){
			throw new SocioError(message:'Socio ya registrado')
		}
    	socio.validate()
    	if(socio.hasErrors()){
    		throw new SocioError(message:'Errores de validacion en socio',socio:socio)
    	}
        
        //log.debug 'Direccion de socio: '+socio.direccion
        def cliente=socio.cliente
        
        if(!cliente.id){
            cliente.nombre=socio.nombre
            cliente.tipo=TipoDeCliente.first()
            if(!cliente.validate(['rfc'])){
                cliente.rfc='XAXX010101000'
            }
            cliente.save flush:true
            //socio.cliente=cliente
        }
        //socio.membresia.validate()
        
        if(socio.id==null){
            def folio=CfdiFolio.findBySerie('SOCIOS')
            if(folio==null){
                folio=new CfdiFolio(serie:'SOCIOS',folio:60000)
                folio.save flush:true
            }
            socio.numeroDeSocio=StringUtils.leftPad(folio.next().toString(),6,'0')
        }
        
    	socio=socio.save failOnError:true
        
    	return socio
    }
	
	def actualizarSocio(Socio socio) {
		
		def proximoPago=socio.membresia.proximoPago
        if(proximoPago ){
            def now=new Date()
            def suspender=proximoPago+socio.membresia.toleranciaEnDias
            socio.membresia.suspender=suspender
            if(now>=suspender){
                log.info 'Suspendiendo socio'
                socio.activo=false
            }
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
	
    def Socio activar(Socio socio,boolean valor){
		socio.activo=valor
		socio.save()
		logAccess(socio)
        return socio
		
	}

    

    def AccessLog logAccess(Socio socio){
        AccessLog log=new AccessLog()
        log.nombre=socio.nombre
        log.numero=NumberUtils.toLong(socio.numeroDeSocio)
        log.tarjeta=socio.tarjeta
        log.activo=socio.activo
		if(log.validate()){
			log.save failOnError:true
			return log
		}
		return null
        
    }
}


class SocioError extends RuntimeException{
	
	String message
	Socio socio


}



