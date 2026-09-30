package pe.edu.upt.aguatacna.feature.recibo.domain.usecase

import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface ResultadoConfirmacion {
    data class Guardado(val recibo: Recibo, val reemplazo: Boolean) : ResultadoConfirmacion
    data class Duplicado(val periodo: PeriodoConsumo) : ResultadoConfirmacion
    data object Incompleto : ResultadoConfirmacion
}

// Guarda el borrador como recibo. Un período solo admite un recibo: si ya existe otro distinto del que
// se está editando, se rechaza como duplicado en vez de sobrescribirlo.
class ConfirmarReciboUseCase(
    private val repository: ReciboRepository
) {
    suspend operator fun invoke(borrador: ReciboBorrador): ResultadoConfirmacion {
        val periodo = borrador.periodoConsumo.valor
        if (!borrador.esConfirmable || periodo == null) return ResultadoConfirmacion.Incompleto

        val existente = repository.obtenerPorPeriodo(periodo)
        if (existente != null && existente.id != borrador.idRecibo) {
            return ResultadoConfirmacion.Duplicado(periodo)
        }

        val recibo = borrador.confirmar(borrador.idRecibo ?: nuevoId())
        repository.guardar(recibo)
        return ResultadoConfirmacion.Guardado(recibo, reemplazo = existente != null)
    }

    // No se deriva del período: un recibo editado puede moverse de mes y conservar su id, y un id
    // "recibo-año-mes" chocaría con el de un recibo nuevo de ese mes.
    @OptIn(ExperimentalUuidApi::class)
    private fun nuevoId(): String = "recibo-${Uuid.random()}"
}
