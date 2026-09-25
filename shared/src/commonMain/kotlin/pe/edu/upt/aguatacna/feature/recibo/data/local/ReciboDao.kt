package pe.edu.upt.aguatacna.feature.recibo.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

// Acceso a la tabla `recibo` de la base de datos general (AguaTacnaDatabase).
@Dao
interface ReciboDao {

    @Query("SELECT * FROM recibo ORDER BY anio DESC, mes DESC")
    fun observarTodos(): Flow<List<ReciboEntity>>

    @Query("SELECT * FROM recibo WHERE anio = :anio AND mes = :mes LIMIT 1")
    suspend fun buscarPorPeriodo(anio: Int, mes: Int): ReciboEntity?

    @Upsert
    suspend fun guardar(recibo: ReciboEntity)

    @Query("DELETE FROM recibo WHERE id = :id")
    suspend fun borrarPorId(id: String)
}
