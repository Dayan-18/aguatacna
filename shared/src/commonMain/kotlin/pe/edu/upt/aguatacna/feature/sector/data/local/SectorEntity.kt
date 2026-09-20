package pe.edu.upt.aguatacna.feature.sector.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sector")
data class SectorEntity(
    @PrimaryKey val id: String
)
