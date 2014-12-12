package com.luxsoft.kio

import org.springframework.security.access.annotation.Secured
import grails.validation.Validateable
import org.grails.databinding.BindingFormat

@Secured(["hasAnyRole('ADMINISTRACION','CAJERO')"])
class CobroController {

    def cobroService
    def cfdiService
    
    def index(Long max){
        params.max = Math.min(max ?: 50, 100)
        params.sort=params.sort?:'dateCreated'
        params.order='desc'
        [cobroInstanceList:Cobro.list(params),cobroInstanceListTotal:Cobro.count()]
    }

    def pendientes(Long max){
        params.max = Math.min(max ?: 500, 500)
        params.sort=params.sort?:'dateCreated'
        params.order='desc'
        def query=Venta.where{saldo>0.0 && status=='VENTA'}
        [ventaInstanceList:query.list(params),ventaInstanceListTotal:query.count(params)]
    }

    def registrar(Venta venta){
        if(!venta){
            response.sendError(404)

        }else{
            def cobroInstance=new Cobro(fecha:new Date(),cliente:venta.cliente,importe:venta.saldo,formaDePago:'EFECTIVO')
            [ventaInstance:venta,cobroInstance:cobroInstance]
        }
    }

    def show(Cobro cobroInstance){
        [cobroInstance:cobroInstance]
    }
    def showVenta(Venta ventaInstance){
        def cobro=Cobro.findByVenta(ventaInstance)
        if(cobro){
            render view:'show',model:[cobroInstance:cobro]
            return
        }
        [ventaInstance:ventaInstance]
    }

    def cobrar(Cobro cobroInstance){
        if(!cobroInstance){
            response.sendError 404
        }else{
            try {
				def venta=Venta.get(params.ventaId)
				cobroInstance.venta=venta
                cobroInstance.fecha=new Date()
				cobroInstance.cliente=venta.cliente
				cobroInstance.recibe=cobroInstance.importe
				cobroInstance.cambio=0.0
                cobroInstance=cobroService.save(cobroInstance)
				log.info 'Cobro registrado: '+cobroInstance
                //render view:'show',model:[ventaInstance:cobroInstance.venta,cobroInstance:cobroInstance]
				redirect action:'show',params:[id:cobroInstance.id]
				
            }
            catch(CobroException ex) {
				log.error ex.message
				flash.message=ex.message
                def cobro=ex.cobro
                def venta=Venta.get(params.ventaId)
                render view:'registrar',model:[ventaInstance:venta,cobroInstance:cobro]
                  
            }
            
        }
        
    }

    @Secured(["hasAnyRole('ADMINISTRACION')"])
    def delete(Cobro cobroInstance){
        cobroService.delete(cobroInstance)
		flash.message="Cobro $cobroInstance.id eliminado"
		redirect action:'index'
    }

    def ventas(){ 
        params.max = 100
        params.sort=params.sort?:'dateCreated'
        params.order='desc'
        def now=new Date()
        def list=Venta.executeQuery("from Venta v where date(v.fecha) between ? and ? and v.status in ('VENTA','PAGADA','FACTURADA','CANCELADA')"
            ,[now-2,now])
        log.info 'Ventas registradas: '+list.size()
        [ventaInstanceList:list,ventaInstanceListTotal:list.size()]
    }

    def facturar(Venta venta){
        
        if(venta.cfdi){
            flash.message="Venta ya facturada"
            redirect acion:'show',params:[id:cobro.id]
            return
        }
        def cfdi=cfdiService.generar(venta)
        redirect controller:'cfdi',action:'show',params:[id:cfdi.id]
        //render cfdi.getComprobante()
    }

    

    def searchVentas(SearchVentaCommand command){
        command.nombre=command.nombre?:'%'
        command.fechaInicial=command.fechaInicial?:new Date()-30
        command.fechaFinal=command.fechaFinal?:new Date()
        params.max = 50
        params.sort=params.sort?:'dateCreated'
        params.order='desc'
        println 'Nomibr: '+command.nombre
        
        def hql="from Venta v where lower(v.cliente.nombre) like ?  and date(v.fecha) between ? and ? "
        
        def list=Venta.findAll(hql,[command.nombre.toLowerCase(),command.fechaInicial,command.fechaFinal],params)
        render view:'ventas',model:[ventaInstanceList:list,ventaInstanceCount:list.size()]
    }

    
}

@Validateable
class SearchVentaCommand{
    
    String nombre

    @BindingFormat('dd/MM/yyyy')
    Date fechaInicial=new Date()-30
    
    @BindingFormat('dd/MM/yyyy')
    Date fechaFinal=new Date()
    

    static constraints={
        fechaInicial nullable:true
        fechaFinal nullable:true
        nombre nullable:true
    }
}
