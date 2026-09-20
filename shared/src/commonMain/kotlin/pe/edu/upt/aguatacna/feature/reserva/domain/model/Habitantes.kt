package pe.edu.upt.aguatacna.feature.reserva.domain.model

import kotlin.jvm.JvmInline

@JvmInline
value class Habitantes(val cantidad: Int) {
    init {
        require(cantidad > 0) { "El hogar debe tener al menos 1 habitante: $cantidad" }
    }
}
