package pe.edu.upt.aguatacna.feature.retos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reto")
data class RetoEntity(
    @PrimaryKey val id: String
)
