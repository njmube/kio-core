package com.luxsoft.kio
import org.springframework.security.access.annotation.Secured

@Secured(["hasAnyRole('ADMINISTRACION')"])
class TipoDeCorporativoController {
    static scaffold = true
}
