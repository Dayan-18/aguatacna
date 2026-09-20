package pe.edu.upt.aguatacna.feature.reserva.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evento_llenado")
data class EventoLlenadoEntity(
    @PrimaryKey val id: String
)
