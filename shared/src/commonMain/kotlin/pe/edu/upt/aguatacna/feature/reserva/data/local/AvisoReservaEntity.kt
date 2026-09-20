package pe.edu.upt.aguatacna.feature.reserva.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// La clave identifica el aviso (por ejemplo el agotamiento previsto) y es única por usuario:
// así el mismo aviso no se guarda dos veces. `momento` y `fechaDato` van en formato ISO.
@Entity(tableName = "aviso_reserva", indices = [Index(value = ["usuarioId", "clave"], unique = true)])
data class AvisoReservaEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val clave: String,
    val tipo: String,
    val momento: String,
    val fechaDato: String,
    val horasDato: Double?,
    val leido: Boolean
) {
    companion object {
        const val AGOTAMIENTO = "AGOTAMIENTO"
        const val CONFIRMAR_LLENADO = "CONFIRMAR_LLENADO"
    }
}
