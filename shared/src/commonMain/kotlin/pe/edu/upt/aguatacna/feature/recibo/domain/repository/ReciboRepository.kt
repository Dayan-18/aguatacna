package pe.edu.upt.aguatacna.feature.recibo.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo

/**
 * Interfaz del repositorio de recibos.
 *
 * Expone un [Flow] reactivo para que todas las pantallas se recompongan
 * automáticamente al cambiar los datos (plan sección 4).
 */
interface ReciboRepository {

    /** Observa la lista completa de recibos, ordenados por período descendente. */
    fun observarRecibos(): Flow<List<Recibo>>

    /** Guarda un recibo. Si ya existe uno con el mismo período, lo reemplaza (upsert, S2). */
    suspend fun guardar(recibo: Recibo)

    /** Obtiene un recibo por período. Null si no existe. */
    suspend fun obtenerPorPeriodo(periodo: PeriodoConsumo): Recibo?

    /** Elimina un recibo por su id. */
    suspend fun eliminar(id: String)
}
