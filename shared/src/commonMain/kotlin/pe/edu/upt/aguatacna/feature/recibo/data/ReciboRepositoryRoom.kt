package pe.edu.upt.aguatacna.feature.recibo.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upt.aguatacna.feature.recibo.data.local.ReciboDao
import pe.edu.upt.aguatacna.feature.recibo.data.local.toDomain
import pe.edu.upt.aguatacna.feature.recibo.data.local.toEntity
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository

/**
 * Implementación de [ReciboRepository] respaldada por SQLite/Room KMP (T-11.1).
 * Mantiene la persistencia de los datos al cerrar y abrir la aplicación.
 */
class ReciboRepositoryRoom(
    private val dao: ReciboDao
) : ReciboRepository {

    override fun observarRecibos(): Flow<List<Recibo>> =
        dao.observarTodos().map { lista ->
            lista.map { it.toDomain() }
        }

    override suspend fun guardar(recibo: Recibo) {
        // Upsert por período (supuesto S2): si ya existe un recibo del mismo período, reutiliza su ID
        val existente = dao.buscarPorPeriodo(recibo.periodoConsumo.anio, recibo.periodoConsumo.mes)
        if (existente != null) {
            dao.guardar(recibo.copy(id = existente.id).toEntity())
        } else {
            dao.guardar(recibo.toEntity())
        }
    }

    override suspend fun obtenerPorPeriodo(periodo: PeriodoConsumo): Recibo? =
        dao.buscarPorPeriodo(periodo.anio, periodo.mes)?.toDomain()

    override suspend fun eliminar(id: String) {
        dao.borrarPorId(id)
    }

    suspend fun guardarTodos(recibos: List<Recibo>) {
        dao.guardarTodos(recibos.map { it.toEntity() })
    }

    suspend fun borrarTodos() {
        dao.borrarTodos()
    }
}
