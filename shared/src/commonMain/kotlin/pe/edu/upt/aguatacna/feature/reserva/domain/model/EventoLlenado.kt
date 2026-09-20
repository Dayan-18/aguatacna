package pe.edu.upt.aguatacna.feature.reserva.domain.model

import kotlinx.datetime.LocalDateTime

data class EventoLlenado(
    val momento: LocalDateTime,
    val tipo: TipoLlenado,
    val origen: OrigenLlenado = OrigenLlenado.REAL
)
