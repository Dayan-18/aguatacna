package pe.edu.upt.aguatacna.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuario LIMIT 1")
    suspend fun obtener(): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(usuario: UsuarioEntity)

    @Query("SELECT modoAcceso FROM usuario LIMIT 1")
    fun observarModoDeAcceso(): Flow<String?>

    @Query("UPDATE usuario SET modoAcceso = :modo")
    suspend fun guardarModoDeAcceso(modo: String)
}
