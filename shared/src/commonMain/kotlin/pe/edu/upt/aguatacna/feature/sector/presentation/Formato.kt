package pe.edu.upt.aguatacna.feature.sector.presentation

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.daysUntil
import kotlinx.datetime.number
import kotlin.math.roundToInt

fun LocalTime.aTexto(): String {
    val hora12 = if (hour % 12 == 0) 12 else hour % 12
    val sufijo = if (hour < 12) "a.m." else "p.m."
    return "$hora12:${minute.toString().padStart(2, '0')} $sufijo"
}

fun LocalDate.relativoA(hoy: LocalDate): String = when (hoy.daysUntil(this)) {
    0 -> "hoy"
    1 -> "mañana"
    else -> "el $day/${month.number}"
}

fun minutosEntre(desde: LocalDateTime, hasta: LocalDateTime): Int {
    val dias = desde.date.daysUntil(hasta.date)
    val segundos = hasta.time.toSecondOfDay() - desde.time.toSecondOfDay()
    return dias * 24 * 60 + segundos / 60
}

fun duracionATexto(minutos: Int): String {
    val horas = minutos / 60
    val resto = minutos % 60
    return when {
        horas == 0 -> "$resto min"
        resto == 0 -> "$horas h"
        else -> "$horas h $resto min"
    }
}

fun kmATexto(km: Double): String {
    val decimas = (km * 10).roundToInt()
    return "${decimas / 10},${decimas % 10} km"
}
