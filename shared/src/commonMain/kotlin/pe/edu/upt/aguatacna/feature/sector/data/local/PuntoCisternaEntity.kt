package pe.edu.upt.aguatacna.feature.sector.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "punto_cisterna", indices = [Index(value = ["sectorId"])])
data class PuntoCisternaEntity(
    @PrimaryKey val id: String,
    val sectorId: String,
    val nombre: String,
    val latitud: Double,
    val longitud: Double,
    val horarioInicio: String,
    val horarioFin: String,
    val estado: String
)
