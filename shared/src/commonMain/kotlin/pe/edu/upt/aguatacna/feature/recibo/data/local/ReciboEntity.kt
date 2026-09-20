package pe.edu.upt.aguatacna.feature.recibo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recibo")
data class ReciboEntity(
    @PrimaryKey val id: String
)
