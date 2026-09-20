package pe.edu.upt.aguatacna.feature.asistente.domain.model

data class MensajeAsistente(
    val id: String,
    val texto: String,
    val esUsuario: Boolean,
    val timestamp: Long = 0L
)
