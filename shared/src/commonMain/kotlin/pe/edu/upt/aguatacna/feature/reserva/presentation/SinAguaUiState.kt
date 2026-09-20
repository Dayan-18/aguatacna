package pe.edu.upt.aguatacna.feature.reserva.presentation

import kotlinx.datetime.LocalTime

data class SinAguaUiState(
    val cargando: Boolean = true,
    val vista: SinAguaVista? = null,
    val error: String? = null,
    /** `true` cuando ya se registró y la pantalla puede cerrarse. */
    val listo: Boolean = false
)

/** Lo que compara la pantalla "Te quedaste sin agua": lo proyectado frente a lo que pasó. */
data class SinAguaVista(
    val textoProyectado: String,
    val textoSeAcabo: String,
    val textoDiferencia: String,
    val seAcaboAntes: Boolean,
    val textoConsumo: String
)

sealed interface SinAguaEvent {
    data object SeAcaboAhora : SinAguaEvent
    data class SeAcaboAntes(val hora: LocalTime) : SinAguaEvent
    data object DescartarError : SinAguaEvent
}
