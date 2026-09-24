package pe.edu.upt.aguatacna.feature.sector.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// momento en ISO (yyyy-MM-ddTHH:mm). Las del vecindario llegan sin identidad (usuarioId = "anon").
@Entity(tableName = "confirmacion_horario", indices = [Index(value = ["sectorId", "momento"])])
data class ConfirmacionHorarioEntity(
    @PrimaryKey val id: String,
    val sectorId: String,
    val usuarioId: String,
    val momento: String,
    val tipo: String
)
