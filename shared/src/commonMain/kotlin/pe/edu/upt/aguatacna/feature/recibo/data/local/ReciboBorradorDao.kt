package pe.edu.upt.aguatacna.feature.recibo.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

// Acceso a la única fila de `recibo_borrador`.
@Dao
interface ReciboBorradorDao {

    @Query("SELECT * FROM recibo_borrador WHERE id = 1")
    suspend fun obtener(): ReciboBorradorEntity?

    @Upsert
    suspend fun guardar(borrador: ReciboBorradorEntity)

    @Query("DELETE FROM recibo_borrador")
    suspend fun borrar()
}
