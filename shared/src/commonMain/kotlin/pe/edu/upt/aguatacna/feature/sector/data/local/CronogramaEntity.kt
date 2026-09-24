package pe.edu.upt.aguatacna.feature.sector.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// fecha en ISO (yyyy-MM-dd); horaInicio/horaFin en ISO (HH:mm), como las tablas de reserva.
@Entity(tableName = "cronograma", indices = [Index(value = ["sectorId", "fecha"])])
data class CronogramaEntity(
    @PrimaryKey val id: String,
    val sectorId: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val tipo: String,
    val fuente: String
)
