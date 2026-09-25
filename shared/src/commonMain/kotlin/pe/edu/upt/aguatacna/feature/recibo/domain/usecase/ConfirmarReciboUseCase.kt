package pe.edu.upt.aguatacna.feature.recibo.domain.usecase

import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository

// Guarda el recibo confirmado (upsert por período) y avisa si reemplazó uno existente.
class ConfirmarReciboUseCase(
    private val repository: ReciboRepository
) {
    suspend operator fun invoke(recibo: Recibo): Boolean {
        val existente = repository.obtenerPorPeriodo(recibo.periodoConsumo)
        repository.guardar(recibo)
        return existente != null
    }
}
