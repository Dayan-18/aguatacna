package pe.edu.upt.aguatacna.feature.reserva.domain.model

import kotlinx.datetime.LocalDateTime

/** Qué cambiaría si el usuario declarara que se quedó sin agua en `momento`, sin guardar nada todavía. */
data class PrevisualizacionSinAgua(
    val agotamientoProyectado: LocalDateTime,
    val momento: LocalDateTime,
    val consumoActual: ConsumoHorario,
    val consumoNuevo: ConsumoHorario
) {
    /** Horas que el agua duró menos de lo previsto; negativo si duró más. */
    val horasAntesDeLoPrevisto: Double get() = horasEntre(momento, agotamientoProyectado)
}
