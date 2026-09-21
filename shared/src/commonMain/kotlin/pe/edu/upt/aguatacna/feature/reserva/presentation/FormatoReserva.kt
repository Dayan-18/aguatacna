package pe.edu.upt.aguatacna.feature.reserva.presentation

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import pe.edu.upt.aguatacna.feature.reserva.domain.model.horasEntre
import pe.edu.upt.aguatacna.feature.reserva.domain.model.masHoras
import kotlin.math.roundToInt

private const val SEGUNDOS_DE_REDONDEO = 30.0

fun formatearHora(hora: LocalTime): String {
    val hora12 = if (hora.hour % 12 == 0) 12 else hora.hour % 12
    val sufijo = if (hora.hour < 12) "a.m." else "p.m."
    return "$hora12:${hora.minute.toString().padStart(2, '0')} $sufijo"
}

fun formatearDuracion(horas: Double): String {
    val minutos = (horas * 60).roundToInt()
    val enteras = minutos / 60
    val resto = minutos % 60
    return when {
        enteras == 0 -> "$resto min"
        resto == 0 -> "$enteras h"
        else -> "$enteras h $resto min"
    }
}

/** "hoy 6:40 p.m.", "mañana 5:00 a.m." o "el 23/9 5:00 a.m.", redondeado al minuto. */
fun describirMomento(momento: LocalDateTime, ahora: LocalDateTime): String {
    val redondeado = momento.masHoras(SEGUNDOS_DE_REDONDEO / 3600)
    val fecha = redondeado.date
    val dia = when (fecha) {
        ahora.date -> "hoy"
        ahora.date.plus(1, DateTimeUnit.DAY) -> "mañana"
        else -> "el ${fecha.day}/${fecha.month.number}"
    }
    return "$dia ${formatearHora(redondeado.time)}"
}

/** 1100 se muestra "1 100", como el Figma. */
fun formatearMiles(valor: Int): String =
    valor.toString().reversed().chunked(3).joinToString(" ").reversed()

/** Solo la hora ("3:20 p.m."), redondeada al minuto; si cae en otro día, dice cuál respecto de `referencia`. */
fun describirHora(momento: LocalDateTime, referencia: LocalDateTime): String {
    val redondeado = momento.masHoras(SEGUNDOS_DE_REDONDEO / 3600)
    return if (redondeado.date == referencia.date) formatearHora(redondeado.time) else describirMomento(momento, referencia)
}

/** Lo que dice la insignia de la campana: nada si no hay avisos sin leer y "9+" desde diez. */
fun textoDeInsignia(sinLeer: Int): String? = when {
    sinLeer <= 0 -> null
    sinLeer > 9 -> "9+"
    else -> sinLeer.toString()
}

private fun hora24(hora: LocalTime) = "${hora.hour}:${hora.minute.toString().padStart(2, '0')}"

/** Cuándo pasó algo, como en la lista de avisos: "hace 12 min", "hoy 14:05", "ayer 19:40". */
fun describirCuando(momento: LocalDateTime, ahora: LocalDateTime): String {
    val minutos = (horasEntre(momento, ahora) * 60).toInt()
    if (minutos < 1) return "ahora"
    if (minutos < 60) return "hace $minutos min"
    val dia = when (momento.date) {
        ahora.date -> "hoy"
        ahora.date.minus(1, DateTimeUnit.DAY) -> "ayer"
        else -> "${momento.date.day}/${momento.date.month.number}"
    }
    return "$dia ${hora24(momento.time)}"
}
