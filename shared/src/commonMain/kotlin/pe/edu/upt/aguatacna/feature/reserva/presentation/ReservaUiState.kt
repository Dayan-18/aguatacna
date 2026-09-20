package pe.edu.upt.aguatacna.feature.reserva.presentation

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfirmacionEstimacion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EstadoProyeccion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado

data class ReservaUiState(
    val cargando: Boolean = true,
    val vista: ReservaVista? = null,
    val error: String? = null
)

/** Todo lo que la pantalla "Mi reserva" muestra, ya listo para pintar. */
data class ReservaVista(
    val nivelLitros: Int,
    val capacidadLitros: Int,
    val porcentaje: Int,
    val estado: EstadoProyeccion?,
    val confirmacion: ConfirmacionEstimacion,
    val textoUltimoLlenado: String,
    val textoAgotamiento: String,
    val textoVuelveElAgua: String?,
    val textoDeficit: String?,
    val consumoLitrosPorHora: Int,
    val litrosPorHabitanteDia: Int?
)

sealed interface ReservaEvent {
    data class RegistrarLlenado(val tipo: TipoLlenado) : ReservaEvent
    data object AguaNoLlego : ReservaEvent
    data object MeQuedeSinAgua : ReservaEvent
    data object DescartarError : ReservaEvent
}
