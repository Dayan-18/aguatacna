package pe.edu.upt.aguatacna.feature.reserva.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AvisoReservaDao {

    @Query("SELECT COUNT(*) FROM aviso_reserva WHERE usuarioId = :usuarioId AND clave = :clave")
    suspend fun contar(usuarioId: String, clave: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun guardar(aviso: AvisoReservaEntity)

    @Query("SELECT * FROM aviso_reserva WHERE usuarioId = :usuarioId ORDER BY momento DESC LIMIT 50")
    fun observar(usuarioId: String): Flow<List<AvisoReservaEntity>>

    @Query("UPDATE aviso_reserva SET leido = 1 WHERE usuarioId = :usuarioId")
    suspend fun marcarTodosLeidos(usuarioId: String)
}
