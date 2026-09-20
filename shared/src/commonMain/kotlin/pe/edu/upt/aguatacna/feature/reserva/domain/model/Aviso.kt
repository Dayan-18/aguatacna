package pe.edu.upt.aguatacna.feature.reserva.domain.model

import kotlinx.datetime.LocalDateTime

/** Algo que la aplicación le dice al usuario aunque no la tenga abierta. `clave` evita repetirlo cada hora. */
sealed interface Aviso {
    val clave: String

    /** La reserva se proyecta agotar antes de que vuelva el agua. */
    data class AgotamientoAntesDelAbastecimiento(val agotamiento: LocalDateTime, val deficit: Deficit) : Aviso {
        override val clave: String = "agotamiento-$agotamiento"
    }

    /** El sector abasteció y el usuario no dijo si llenó: se le pide confirmar con una sola acción. */
    data class ConfirmarLlenado(val inicioDeLaVentana: LocalDateTime) : Aviso {
        override val clave: String = "confirmar-$inicioDeLaVentana"
    }
}
