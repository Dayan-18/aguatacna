package pe.edu.upt.aguatacna.feature.reserva.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * El agua de un hogar desde su último llenado vigente, que puede ser real
 * (el usuario lo registró) o asumido (el sector abasteció y no lo confirmó).
 * Si el usuario declaró que se quedó sin agua, `agotadaEn` manda sobre la proyección.
 */
data class Reserva(
    val capacidad: CapacidadLitros,
    val consumo: ConsumoHorario,
    val llenado: EventoLlenado,
    val agotadaEn: LocalDateTime? = null
) {
    val confirmacion: ConfirmacionEstimacion
        get() = if (llenado.origen == OrigenLlenado.REAL) {
            ConfirmacionEstimacion.CONFIRMADA
        } else {
            ConfirmacionEstimacion.NO_CONFIRMADA
        }

    val nivelTrasLlenado: NivelReserva
        get() = NivelReserva.trasLlenado(capacidad, llenado.tipo)

    /** El nivel baja de forma lineal y nunca es menor que cero. */
    fun nivelEn(momento: LocalDateTime): NivelReserva {
        if (agotadaEn != null && momento >= agotadaEn) return NivelReserva(Litros.CERO, capacidad)
        val horas = horasEntre(llenado.momento, momento).coerceAtLeast(0.0)
        val consumido = Litros(consumo.litrosPorHora * horas)
        return NivelReserva(nivelTrasLlenado.litros - consumido, capacidad)
    }

    fun agotamientoProyectado(): LocalDateTime {
        agotadaEn?.let { return it }
        val horasQueDura = nivelTrasLlenado.litros.valor / consumo.litrosPorHora
        return llenado.momento.masHoras(horasQueDura)
    }
}
