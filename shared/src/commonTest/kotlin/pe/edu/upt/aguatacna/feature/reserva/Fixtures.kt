package pe.edu.upt.aguatacna.feature.reserva

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.CapacidadLitros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ClaseIntervalo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.IntervaloConsumo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Litros

val CAPACIDAD_1000 = CapacidadLitros.deLitros(1000.0)

/** Ejecuta un bloque suspendible que no llega a suspenderse, como los del repositorio en memoria. */
fun <T> ejecutar(bloque: suspend () -> T): T {
    var resultado: Result<T>? = null
    bloque.startCoroutine(Continuation(EmptyCoroutineContext) { resultado = it })
    return checkNotNull(resultado) { "El bloque se suspendió" }.getOrThrow()
}

/** Hora absoluta contada desde el 1 de setiembre de 2026 a las 00:00. */
fun enHora(hora: Int): LocalDateTime = LocalDateTime(2026, 9, 1 + hora / 24, hora % 24, 0)

fun intervalo(
    desdeHora: Int,
    hastaHora: Int,
    litros: Double = 1000.0,
    clase: ClaseIntervalo = ClaseIntervalo.POR_LLENADO
) = IntervaloConsumo(enHora(desdeHora), enHora(hastaHora), Litros(litros), clase)

/** Intervalos consecutivos con las duraciones dadas, en horas, desde la hora `inicio`. */
fun intervalosDesde(inicio: Int, vararg duraciones: Int): List<IntervaloConsumo> {
    var actual = inicio
    return duraciones.map { horas -> intervalo(actual, actual + horas).also { actual += horas } }
}

fun intervalosDe(vararg duraciones: Int): List<IntervaloConsumo> = intervalosDesde(0, *duraciones)
