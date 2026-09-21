package pe.edu.upt.aguatacna.feature.recibo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la tabla `recibo` en Room (T-11.1).
 */
@Dao
interface ReciboDao {

    @Query("SELECT * FROM recibo ORDER BY anio DESC, mes DESC")
    fun observarTodos(): Flow<List<ReciboEntity>>

    @Query("SELECT * FROM recibo WHERE anio = :anio AND mes = :mes LIMIT 1")
    suspend fun buscarPorPeriodo(anio: Int, mes: Int): ReciboEntity?

    @Upsert
    suspend fun guardar(recibo: ReciboEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTodos(recibos: List<ReciboEntity>)

    @Query("DELETE FROM recibo WHERE id = :id")
    suspend fun borrarPorId(id: String)

    @Query("DELETE FROM recibo WHERE anio = :anio AND mes = :mes")
    suspend fun borrarPorPeriodo(anio: Int, mes: Int)

    @Query("DELETE FROM recibo")
    suspend fun borrarTodos()
}
