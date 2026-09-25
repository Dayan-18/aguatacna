package pe.edu.upt.aguatacna.feature.recibo.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository

// Repositorio en memoria usado en pruebas y vistas previas (sin Koin); upsert por período de consumo.
class FakeReciboRepository : ReciboRepository {

    private val _recibos = MutableStateFlow<List<Recibo>>(emptyList())

    override fun observarRecibos(): Flow<List<Recibo>> =
        _recibos.map { lista -> lista.sortedByDescending { it.periodoConsumo } }

    override suspend fun guardar(recibo: Recibo) {
        _recibos.update { lista -> lista.filter { it.periodoConsumo != recibo.periodoConsumo } + recibo }
    }

    override suspend fun obtenerPorPeriodo(periodo: PeriodoConsumo): Recibo? =
        _recibos.value.find { it.periodoConsumo == periodo }

    override suspend fun eliminar(id: String) {
        _recibos.update { lista -> lista.filter { it.id != id } }
    }

    // Carga en bloque, usada por la semilla de datos de ejemplo en pruebas.
    suspend fun cargarTodos(recibos: List<Recibo>) {
        _recibos.value = recibos
    }
}
