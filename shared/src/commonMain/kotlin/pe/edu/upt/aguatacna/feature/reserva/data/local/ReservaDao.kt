package pe.edu.upt.aguatacna.feature.reserva.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservaDao {

    @Query("SELECT * FROM perfil_hogar WHERE usuarioId = :usuarioId")
    fun observarPerfil(usuarioId: String): Flow<PerfilHogarEntity?>

    @Upsert
    suspend fun guardarPerfil(perfil: PerfilHogarEntity)

    @Query("SELECT * FROM evento_llenado WHERE usuarioId = :usuarioId ORDER BY momento")
    fun observarLlenados(usuarioId: String): Flow<List<EventoLlenadoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarLlenado(llenado: EventoLlenadoEntity)

    @Query("DELETE FROM evento_llenado WHERE usuarioId = :usuarioId")
    suspend fun borrarLlenados(usuarioId: String)
}
