package pe.edu.upt.aguatacna.feature.sector.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Las columnas nuevas llevan defaultValue para que el AutoMigration 3->4 las agregue solo
// (Room no puede añadir columnas NOT NULL sin default). El sync las llena con los datos reales.
@Entity(tableName = "sector")
data class SectorEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(defaultValue = "") val nombre: String,
    @ColumnInfo(defaultValue = "") val distrito: String,
    @ColumnInfo(defaultValue = "0") val latitud: Double,
    @ColumnInfo(defaultValue = "0") val longitud: Double
)
