package pe.edu.upt.aguatacna.feature.reserva.presentation

import pe.edu.upt.aguatacna.feature.reserva.domain.model.Recomendacion

data class QueRecortarUiState(
    val cargando: Boolean = true,
    /** `null` cuando no hay déficit: no hay nada que recortar. */
    val vista: QueRecortarVista? = null
)

data class QueRecortarVista(
    val textoDeficit: String,
    val textoAgotamiento: String,
    val textoVuelveElAgua: String,
    val opciones: List<OpcionDeRecorte>,
    val ahorroLitros: Int,
    val textoGanas: String,
    val textoFaltan: String,
    val cubreElDeficit: Boolean
)

data class OpcionDeRecorte(
    val recomendacion: Recomendacion,
    val litros: Int,
    val elegida: Boolean
)

sealed interface QueRecortarEvent {
    data class Alternar(val recomendacion: Recomendacion) : QueRecortarEvent
}
