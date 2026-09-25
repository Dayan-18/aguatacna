package pe.edu.upt.aguatacna.feature.recibo.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upt.aguatacna.feature.recibo.data.local.ReciboDao
import pe.edu.upt.aguatacna.feature.recibo.data.local.toDomain
import pe.edu.upt.aguatacna.feature.recibo.data.local.toEntity
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository

// Implementación de [ReciboRepository] sobre la tabla `recibo` de la base de datos general (Room).
class ReciboRepositoryRoom(
    private val dao: ReciboDao
) : ReciboRepository {

    override fun observarRecibos(): Flow<List<Recibo>> =
        dao.observarTodos().map { lista -> lista.map { it.toDomain() } }

    override suspend fun guardar(recibo: Recibo) {
        // Upsert por período: si ya existe un recibo del mismo período, reutiliza su ID
        val existente = dao.buscarPorPeriodo(recibo.periodoConsumo.anio, recibo.periodoConsumo.mes)
        val aGuardar = if (existente != null) recibo.copy(id = existente.id) else recibo
        dao.guardar(aGuardar.toEntity())
    }

    override suspend fun obtenerPorPeriodo(periodo: PeriodoConsumo): Recibo? =
        dao.buscarPorPeriodo(periodo.anio, periodo.mes)?.toDomain()

    override suspend fun eliminar(id: String) {
        dao.borrarPorId(id)
    }
}
