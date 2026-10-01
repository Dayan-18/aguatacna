package pe.edu.upt.aguatacna.feature.asistente.domain.model

/**
 * Entidad de dominio que representa un mensaje en la conversación con el asistente hídrico.
 */
data class MensajeAsistente(
    val id: String,
    val texto: String,
    val esUsuario: Boolean,
    val timestamp: Long = 0L,
    val enviando: Boolean = false,
    val esError: Boolean = false
)
