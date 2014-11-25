package com.luxsoft.kio

import grails.events.Listener
import grails.transaction.Transactional

@Transactional
class AccesoService {

    @Listener(namespace='gorm')
	def afterDelete(AplicacionDePago aplicacionDePago){
		log.debug "Detectando eliminacion de aplicacion de pago ${aplicacionDePago.id}"
		// if(neDet.concepto.clave==CONCEPTO){
		// 	PrestamoAbono.withNewSession{
		// 		log.debug 'Buscando prestamoAbono para NominaPorEmpleadoDet:'+neDet.id
		// 		def abono=PrestamoAbono.findByNominaPorEmpleadoDet(neDet)
		// 		if(abono){
					
		// 			def prestamo=abono.prestamo
		// 			prestamo.removeFromAbonos(abono)
		// 			prestamo.actualizarSaldo()
		// 			prestamo.save flush:true
		// 			log.debug "PrestamoAbono ${abono.id} eliminado al eliminar detalle de nomina por empleado ${neDet.id}"
		// 		}
		// 	}
		// }
	}

	@Listener(namespace='gorm')
	def afterInsert(AplicacionDePago aplicacionDePago){
		log.info "Detectando una nueva aplicacion ${aplicacionDePago.id}"
	}

	@Listener(namespace='gorm')
	def afterUpdate(AplicacionDePago aplicacionDePago){
		log.info("Detectando una modificacion de aplicacion de pago: ${aplicacionDePago.id}")
	}
}
