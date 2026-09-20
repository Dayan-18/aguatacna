package pe.edu.upt.aguatacna.feature.reserva.presentation

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.LitrosPorHabitanteDia
import pe.edu.upt.aguatacna.feature.reserva.domain.model.OrigenLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoReservorio
import pe.edu.upt.aguatacna.feature.reserva.domain.model.masHoras
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.CalcularDeficit
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.EvaluarProyeccion
import kotlin.math.roundToInt

/** Lo que la cabecera dice del hogar: dónde vive y con qué tipo de reservorio cuenta. */
data class ContextoDelHogar(val tipo: TipoReservorio, val habitantes: Int, val nombreDelSector: String?)

/** Convierte la reserva del dominio en lo que muestra la pantalla; el ViewModel solo lo invoca. */
class ConstruirVistaReserva(
    private val calcularDeficit: CalcularDeficit = CalcularDeficit(),
    private val evaluarProyeccion: EvaluarProyeccion = EvaluarProyeccion()
) {
    operator fun invoke(
        reserva: Reserva,
        hogar: ContextoDelHogar,
        proximoAbastecimiento: LocalDateTime?,
        litrosPorHabitanteDia: LitrosPorHabitanteDia?,
        ahora: LocalDateTime
    ): ReservaVista {
        val nivel = reserva.nivelEn(ahora)
        val deficit = calcularDeficit(reserva, proximoAbastecimiento)
        return ReservaVista(
            saludo = saludoPara(ahora.hour),
            subtituloHogar = subtituloDe(hogar),
            nivelLitros = nivel.litros.valor.roundToInt(),
            capacidadLitros = reserva.capacidad.litros.valor.roundToInt(),
            porcentaje = nivel.porcentaje.roundToInt(),
            estado = evaluarProyeccion(reserva, proximoAbastecimiento),
            confirmacion = reserva.confirmacion,
            textoLlenado = describirLlenado(reserva, hogar.tipo, ahora),
            horaLlenadoAsumido = horaAsumida(reserva),
            textoAgotamiento = describirMomento(reserva.agotamientoProyectado(), ahora),
            textoVuelveElAgua = proximoAbastecimiento?.let { describirMomento(it, ahora) },
            textoDeficit = deficit?.let { formatearDuracion(it.horas) },
            textoAlcanzaHastaSiLlena = alcanzaHastaSiLlena(reserva, ahora),
            consumoLitrosPorHora = reserva.consumo.litrosPorHora.roundToInt(),
            litrosPorHabitanteDia = litrosPorHabitanteDia?.valor?.roundToInt()
        )
    }

    private fun alcanzaHastaSiLlena(reserva: Reserva, ahora: LocalDateTime): String {
        val horasQueDuraria = reserva.capacidad.litros.valor / reserva.consumo.litrosPorHora
        return describirMomento(ahora.masHoras(horasQueDuraria), ahora)
    }

    private fun subtituloDe(hogar: ContextoDelHogar): String {
        val personas = if (hogar.habitantes == 1) "1 persona" else "${hogar.habitantes} personas"
        return hogar.nombreDelSector?.let { "$it · $personas" } ?: personas
    }

    private fun describirLlenado(reserva: Reserva, tipo: TipoReservorio, ahora: LocalDateTime): String {
        val cuando = describirMomento(reserva.llenado.momento, ahora)
        val llenado = if (reserva.llenado.origen == OrigenLlenado.REAL) "último llenado" else "llenado asumido"
        return "${nombreDelTipo(tipo)} · $llenado $cuando"
    }

    private fun horaAsumida(reserva: Reserva): String? =
        if (reserva.llenado.origen == OrigenLlenado.ASUMIDO) formatearHora(reserva.llenado.momento.time) else null
}

fun saludoPara(hora: Int): String = when {
    hora < 12 -> "Buenos días"
    hora < 19 -> "Buenas tardes"
    else -> "Buenas noches"
}

fun nombreDelTipo(tipo: TipoReservorio): String = when (tipo) {
    TipoReservorio.TANQUE_ELEVADO -> "Tanque elevado"
    TipoReservorio.CISTERNA -> "Cisterna"
    TipoReservorio.BIDONES -> "Bidones"
}
