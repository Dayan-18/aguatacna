package pe.edu.upt.aguatacna.feature.retos.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reporte")
data class ReporteEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "usuario_id") val usuarioId: String,
    val tipo: String,
    val descripcion: String,
    val latitud: Double?,
    val longitud: Double?,
    @ColumnInfo(name = "foto_bytes") val fotoBytes: ByteArray?,
    @ColumnInfo(name = "pendiente_sincronizacion") val pendienteSincronizacion: Boolean
)
