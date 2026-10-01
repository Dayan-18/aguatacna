package pe.edu.upt.aguatacna.feature.retos.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "reto_usuario", primaryKeys = ["usuario_id", "reto_id", "fecha"])
data class RetoUsuarioEntity(
    @ColumnInfo(name = "usuario_id") val usuarioId: String,
    @ColumnInfo(name = "reto_id") val retoId: String,
    val fecha: String,
    val cumplido: Boolean
)
