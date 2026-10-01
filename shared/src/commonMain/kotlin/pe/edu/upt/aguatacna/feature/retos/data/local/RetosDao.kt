package pe.edu.upt.aguatacna.feature.retos.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface RetosDao {
    @Query("SELECT * FROM reto WHERE activo = 1")
    suspend fun obtenerRetos(): List<RetoEntity>

    @Upsert
    suspend fun guardarRetos(retos: List<RetoEntity>)

    @Query("SELECT * FROM reto_usuario WHERE usuario_id = :usuarioId")
    suspend fun obtenerCumplimientos(usuarioId: String): List<RetoUsuarioEntity>

    @Upsert
    suspend fun guardarCumplimiento(cumplimiento: RetoUsuarioEntity)

    @Query("SELECT * FROM reporte")
    suspend fun obtenerReportes(): List<ReporteEntity>

    @Upsert
    suspend fun guardarReporte(reporte: ReporteEntity)
}
