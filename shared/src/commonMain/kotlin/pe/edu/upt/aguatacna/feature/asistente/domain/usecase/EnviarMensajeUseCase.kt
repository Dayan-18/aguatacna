package pe.edu.upt.aguatacna.feature.asistente.domain.usecase

import pe.edu.upt.aguatacna.feature.asistente.domain.model.MensajeAsistente
import pe.edu.upt.aguatacna.feature.asistente.domain.repository.AsistenteRepository

/**
 * Caso de uso para validar y enviar un mensaje al asistente (flujo n8n).
 */
class EnviarMensajeUseCase(
    private val repository: AsistenteRepository
) {
    suspend operator fun invoke(texto: String): Result<MensajeAsistente> {
        val textoLimpio = texto.trim()
        if (textoLimpio.isBlank()) {
            return Result.failure(IllegalArgumentException("El mensaje no puede estar vacío."))
        }
        return repository.enviarMensaje(textoLimpio)
    }
}
