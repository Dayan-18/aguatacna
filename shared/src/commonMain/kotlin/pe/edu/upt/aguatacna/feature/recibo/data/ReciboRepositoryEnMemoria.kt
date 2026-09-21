package pe.edu.upt.aguatacna.feature.recibo.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository

/**
 * Implementación del repositorio en memoria (Fase 2, supuesto S5).
 *
 * Los datos se pierden al cerrar la app. Se reemplazará por SQLDelight/Room
 * en Fase 11 (T-11.1).
 *
 * Upsert por período de consumo (supuesto S2).
 */
class ReciboRepositoryEnMemoria : ReciboRepository {

    private val _recibos = MutableStateFlow<List<Recibo>>(emptyList())

    override fun observarRecibos(): Flow<List<Recibo>> =
        _recibos.map { lista ->
            lista.sortedByDescending { it.periodoConsumo }
        }

    override suspend fun guardar(recibo: Recibo) {
        _recibos.update { lista ->
            // Upsert: reemplazar si existe el mismo período (S2)
            val sinDuplicado = lista.filter { it.periodoConsumo != recibo.periodoConsumo }
            sinDuplicado + recibo
        }
    }

    override suspend fun obtenerPorPeriodo(periodo: PeriodoConsumo): Recibo? =
        _recibos.value.find { it.periodoConsumo == periodo }

    override suspend fun eliminar(id: String) {
        _recibos.update { lista -> lista.filter { it.id != id } }
    }

    /**
     * Carga una lista de recibos en bloque (para la semilla de depuración).
     */
    suspend fun cargarTodos(recibos: List<Recibo>) {
        _recibos.value = recibos
    }
}
