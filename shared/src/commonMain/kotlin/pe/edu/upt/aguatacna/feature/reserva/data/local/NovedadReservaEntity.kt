package pe.edu.upt.aguatacna.feature.reserva.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// SIN_LLEGADA: el agua no llegó al sector. SIN_AGUA: el usuario se quedó sin agua.
// En SIN_AGUA, `inicioObservado` y `litrosObservados` guardan el intervalo que se aprendió
// (nulos si el llenado vigente era asumido y no se aprende de él).
@Entity(tableName = "novedad_reserva")
data class NovedadReservaEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val momento: String,
    val tipo: String,
    val inicioObservado: String? = null,
    val litrosObservados: Double? = null
) {
    companion object {
        const val SIN_LLEGADA = "SIN_LLEGADA"
        const val SIN_AGUA = "SIN_AGUA"
    }
}
