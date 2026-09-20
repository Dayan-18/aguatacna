package pe.edu.upt.aguatacna.feature.reserva.domain.model

import kotlinx.datetime.LocalDateTime

private const val SEGUNDOS_POR_DIA = 86_400L
private const val SEGUNDOS_POR_HORA = 3_600.0

internal fun horasEntre(desde: LocalDateTime, hasta: LocalDateTime): Double =
    (segundosDesdeEpoca(hasta) - segundosDesdeEpoca(desde)) / SEGUNDOS_POR_HORA

private fun segundosDesdeEpoca(momento: LocalDateTime): Long =
    momento.date.toEpochDays().toLong() * SEGUNDOS_POR_DIA + momento.time.toSecondOfDay()
