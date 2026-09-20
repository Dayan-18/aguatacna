package pe.edu.upt.aguatacna.feature.reserva.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.math.roundToLong

private const val SEGUNDOS_POR_DIA = 86_400L
private const val SEGUNDOS_POR_HORA = 3_600.0

internal fun horasEntre(desde: LocalDateTime, hasta: LocalDateTime): Double =
    (segundosDesdeEpoca(hasta) - segundosDesdeEpoca(desde)) / SEGUNDOS_POR_HORA

internal fun LocalDateTime.masHoras(horas: Double): LocalDateTime {
    val total = segundosDesdeEpoca(this) + (horas * SEGUNDOS_POR_HORA).roundToLong()
    val fecha = LocalDate.fromEpochDays(total.floorDiv(SEGUNDOS_POR_DIA).toInt())
    return LocalDateTime(fecha, LocalTime.fromSecondOfDay(total.mod(SEGUNDOS_POR_DIA).toInt()))
}

private fun segundosDesdeEpoca(momento: LocalDateTime): Long =
    momento.date.toEpochDays().toLong() * SEGUNDOS_POR_DIA + momento.time.toSecondOfDay()
