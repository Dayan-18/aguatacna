package pe.edu.upt.aguatacna.feature.reserva.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Un perfil por usuario: la llave es el UUID local (constitución, artículo III).
@Entity(tableName = "perfil_hogar")
data class PerfilHogarEntity(
    @PrimaryKey val usuarioId: String,
    val tipoReservorio: String,
    val capacidadLitros: Double,
    val habitantes: Int,
    val duchasPorDia: Int,
    val usaLavadora: Boolean,
    val riegaJardin: Boolean,
    val consumoPorHabitosLitrosHora: Double?,
    val consumoVigenteLitrosHora: Double?
)
