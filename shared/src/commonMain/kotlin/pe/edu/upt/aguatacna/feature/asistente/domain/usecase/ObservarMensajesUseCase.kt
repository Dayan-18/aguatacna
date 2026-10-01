package pe.edu.upt.aguatacna.feature.asistente.domain.usecase

import kotlinx.coroutines.flow.Flow
import pe.edu.upt.aguatacna.feature.asistente.domain.model.MensajeAsistente
import pe.edu.upt.aguatacna.feature.asistente.domain.repository.AsistenteRepository

/**
 * Caso de uso para observar el flujo de mensajes del chat en tiempo real.
 */
class ObservarMensajesUseCase(
    private val repository: AsistenteRepository
) {
    operator fun invoke(): Flow<List<MensajeAsistente>> =
        repository.observarMensajes()
}
