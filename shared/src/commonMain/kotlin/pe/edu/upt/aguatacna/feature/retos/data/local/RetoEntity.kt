package pe.edu.upt.aguatacna.feature.retos.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reto")
data class RetoEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(defaultValue = "''") val titulo: String = "",
    @ColumnInfo(defaultValue = "''") val descripcion: String = "",
    @ColumnInfo(name = "litros_meta", defaultValue = "0") val litrosMeta: Int = 0,
    @ColumnInfo(defaultValue = "1") val activo: Boolean = true
)
