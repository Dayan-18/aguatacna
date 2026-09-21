package pe.edu.upt.aguatacna.feature.recibo.domain.usecase

import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository

/**
 * Confirma un recibo: convierte el borrador en entidad y la guarda.
 * Si ya existe un recibo del mismo período, lo reemplaza (upsert, S2).
 */
class ConfirmarReciboUseCase(
    private val repository: ReciboRepository
) {
    /**
     * @param recibo El recibo confirmado a guardar.
     * @return true si ya existía un recibo para ese período (fue reemplazado).
     */
    suspend operator fun invoke(recibo: Recibo): Boolean {
        val existente = repository.obtenerPorPeriodo(recibo.periodoConsumo)
        repository.guardar(recibo)
        return existente != null
    }
}
