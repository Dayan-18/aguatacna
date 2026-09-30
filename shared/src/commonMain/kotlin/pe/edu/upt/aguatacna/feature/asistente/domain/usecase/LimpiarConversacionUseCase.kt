package pe.edu.upt.aguatacna.feature.asistente.domain.usecase

import pe.edu.upt.aguatacna.feature.asistente.domain.repository.AsistenteRepository

/**
 * Caso de uso para vaciar la conversación.
 */
class LimpiarConversacionUseCase(
    private val repository: AsistenteRepository
) {
    suspend operator fun invoke() {
        repository.limpiarConversacion()
    }
}
