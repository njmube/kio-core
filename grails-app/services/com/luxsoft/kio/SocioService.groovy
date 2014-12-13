package com.luxsoft.kio

import grails.transaction.Transactional
import grails.transaction.NotTransactional

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
		
		// def proximoPago=socio.membresia.proximoPago
  //       if(proximoPago ){
  //           def now=new Date()
  //           def suspender=proximoPago+socio.membresia.toleranciaEnDias
  //           socio.membresia.suspender=suspender
  //           if(now>=suspender){
  //               log.info 'Suspendiendo socio'
  //               socio.activo=false
  //           }else{
  //               log.info 'Activando socio'
  //               socio.activo=true
  //           }
  //       }	
        def proximoPago=socio.membresia.proximoPago
        if(proximoPago ){
            def now=new Date()
            def suspender=proximoPago+socio.membresia.toleranciaEnDias
            //socio.membresia.suspender=suspender
            if(now>=suspender){
                if(socio.activo==true){
                    log.debug "Suspendiendo $socio.nombre por atraso de $socio.membresia.atraso  Tarjeta: $socio.tarjeta"
                    socio.activo=false
                    suspendidos++
                }
            }else{
                if(socio.activo==false){
                    log.debug "Activando $socio.nombre"
                    socio.activo=true
                    activados++
                }
            }
        } else{
            if(socio.activo){
                log.debug "Suspendiendo $socio.nombre por no tener proximo pago definido"
                socio.activo=false
                suspendidos++
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


    /**
    * Genera una bitacora de acceso para todos los socios activos.
    *
    **/
    @NotTransactional
    def exportarALectora(){
        def socios=Socio.findAll("from Socio s where s.activo=true")
        def exportados=[]
        socios.each{ s->
            if(s.tarjeta){
                try {
                    def alog=logAccess(s)
                    exportados.add(alog)
                    s.sleep(1000)
                }
                catch(Exception e) {
                }
            }

        }
        return exportados
    }

    /**
    *
    * Limpia los registros AccessLog anteriores a la fecha indicada y si estan replicados
    *
    **/
    @NotTransactional
    def limpiarBitacora(Date fecha,boolean replicados){
        if(replicados){
            def res=AccessLog.executeUpdate("delete from AccessLog a where date(a.dateCreated)<? and replicado is not null"
                ,[fecha])
            return res
        }else{
            def res=AccessLog.executeUpdate("delete from AccessLog a where date(a.dateCreated)<? "
                ,[fecha])
            return res
        }
        
    }

    /**
    *   Actualizar suspencion de socios por atraso en pago
    *
    **/
    @NotTransactional
    def suspender(){
        def socios=Socio.findAll("from Socio s where tarjeta is not null")
        def res=[:]
        def suspendidos=0
        def activados=0
        socios.each{socio->
            
            def proximoPago=socio.membresia.proximoPago
            if(proximoPago ){
                def now=new Date()
                def suspender=proximoPago+socio.membresia.toleranciaEnDias
                //socio.membresia.suspender=suspender
                if(now>=suspender){
                    if(socio.activo==true){
                        log.debug "Suspendiendo $socio.nombre por atraso de $socio.membresia.atraso  Tarjeta: $socio.tarjeta"
                        socio.activo=false
                        socio.save flush:true
                        suspendidos++
                    }
                }else{
                    if(socio.activo==false){
                        log.debug "Activando $socio.nombre"
                        socio.activo=true
                        socio.save flush:true
                        activados++
                    }
                }
            } else{
                if(socio.activo){
                    log.debug "Suspendiendo $socio.nombre por no tener proximo pago definido"
                    socio.activo=false
                    socio.save flush:true
                    suspendidos++
                }
            } 
            //socio.sleep(1000)
        }
        res.suspendidos=suspendidos
        res.activados=activados
        return res
    }
}


class SocioError extends RuntimeException{
	
	String message
	Socio socio


}



