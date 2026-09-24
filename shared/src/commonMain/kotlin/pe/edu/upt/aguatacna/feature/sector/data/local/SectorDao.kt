package pe.edu.upt.aguatacna.feature.sector.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SectorDao {

    @Query("SELECT * FROM sector")
    suspend fun sectores(): List<SectorEntity>

    @Query("SELECT * FROM cronograma WHERE sectorId = :sectorId")
    suspend fun cronogramas(sectorId: String): List<CronogramaEntity>

    @Query("SELECT * FROM punto_cisterna WHERE sectorId = :sectorId")
    suspend fun puntosCisterna(sectorId: String): List<PuntoCisternaEntity>

    @Query("SELECT * FROM confirmacion_horario WHERE sectorId = :sectorId")
    suspend fun confirmaciones(sectorId: String): List<ConfirmacionHorarioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarSectores(sectores: List<SectorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarCronogramas(cronogramas: List<CronogramaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarPuntos(puntos: List<PuntoCisternaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarConfirmacion(confirmacion: ConfirmacionHorarioEntity)
}
