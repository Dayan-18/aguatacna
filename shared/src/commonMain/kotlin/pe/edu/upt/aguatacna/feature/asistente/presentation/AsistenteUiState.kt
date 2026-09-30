package pe.edu.upt.aguatacna.feature.asistente.presentation

import pe.edu.upt.aguatacna.feature.asistente.domain.model.MensajeAsistente

/**
 * Estado inmutable de la interfaz del Asistente Hídrico.
 */
data class AsistenteUiState(
    val mensajes: List<MensajeAsistente> = emptyList(),
    val estaEscribiendo: Boolean = false,
    val textoEntrada: String = "",
    val error: String? = null
)
