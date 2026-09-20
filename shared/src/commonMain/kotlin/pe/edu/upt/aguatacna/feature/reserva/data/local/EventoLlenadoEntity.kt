package pe.edu.upt.aguatacna.feature.reserva.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Solo se guardan los llenados reales; los asumidos se derivan del cronograma del sector.
// `momento` va en formato ISO (2026-09-19T05:15), que se ordena igual que el tiempo.
@Entity(tableName = "evento_llenado")
data class EventoLlenadoEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val momento: String,
    val tipo: String
)
