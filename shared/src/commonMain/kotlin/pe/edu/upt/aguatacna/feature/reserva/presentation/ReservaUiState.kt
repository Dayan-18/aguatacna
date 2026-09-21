package pe.edu.upt.aguatacna.feature.reserva.presentation

import kotlinx.datetime.LocalTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfirmacionEstimacion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EstadoProyeccion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado

data class ReservaUiState(
    val cargando: Boolean = true,
    val hogarConfigurado: Boolean = false,
    val avisosSinLeer: Int = 0,
    val vista: ReservaVista? = null,
    val error: String? = null
)

/** Todo lo que la pantalla "Mi reserva" muestra, ya listo para pintar. */
data class ReservaVista(
    val saludo: String,
    val subtituloHogar: String,
    val nivelLitros: Int,
    val capacidadLitros: Int,
    val porcentaje: Int,
    val estado: EstadoProyeccion?,
    val confirmacion: ConfirmacionEstimacion,
    val textoLlenado: String,
    /** La hora del llenado asumido, para pedir que se confirme; `null` si el llenado es real. */
    val horaLlenadoAsumido: String?,
    val textoAgotamiento: String,
    val textoVuelveElAgua: String?,
    val textoDeficit: String?,
    /** Hasta cuándo alcanzaría el agua si se registrara un llenado completo ahora. */
    val textoAlcanzaHastaSiLlena: String,
    val consumoLitrosPorHora: Int,
    val litrosPorHabitanteDia: Int?
)

sealed interface ReservaEvent {
    data class RegistrarLlenado(val tipo: TipoLlenado) : ReservaEvent
    data object ConfirmarLlenadoAsumido : ReservaEvent
    data class CorregirHoraDelLlenado(val hora: LocalTime) : ReservaEvent
    data object AguaNoLlego : ReservaEvent
    data object MeQuedeSinAgua : ReservaEvent
    data object DescartarError : ReservaEvent
}
