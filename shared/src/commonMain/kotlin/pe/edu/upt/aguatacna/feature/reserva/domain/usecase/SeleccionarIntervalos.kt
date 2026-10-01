package pe.edu.upt.aguatacna.feature.reserva.domain.usecase

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ClaseIntervalo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.IntervaloConsumo

/** Elige los intervalos que sirven para estimar el consumo del hogar. */
class SeleccionarIntervalos {

    operator fun invoke(intervalos: List<IntervaloConsumo>): List<IntervaloConsumo> {
        val recientes = intervalos.filterNot(::esRelleno).sortedBy { it.fin }.takeLast(ParametrosConsumo.MAX_INTERVALOS)
        val sinOlvidos = descartarOlvidos(recientes)
        val observados = sinOlvidos.filter { it.clase == ClaseIntervalo.OBSERVADO }
        return observados.ifEmpty { sinOlvidos }
    }

    /** Un intervalo observado es dato exacto y alcanza solo; los inferidos necesitan dos (CA-28, CA-36). */
    fun alcanzanParaEstimar(validos: List<IntervaloConsumo>): Boolean =
        validos.any { it.clase == ClaseIntervalo.OBSERVADO } || validos.size >= ParametrosConsumo.MINIMO_INFERIDOS

    // Entre dos llenados muy seguidos no se gastó el tanque: contarlo dispararía el consumo (CA-35).
    // Los observados no se tocan: ahí el usuario declaró que el agua se acabó de verdad.
    private fun esRelleno(intervalo: IntervaloConsumo): Boolean =
        intervalo.clase == ClaseIntervalo.POR_LLENADO && intervalo.horas < ParametrosConsumo.HORAS_MINIMAS_ENTRE_LLENADOS

    // Un intervalo mucho más largo que lo normal es un llenado que no se registró.
    private fun descartarOlvidos(intervalos: List<IntervaloConsumo>): List<IntervaloConsumo> {
        if (intervalos.isEmpty()) return intervalos
        val limite = intervalos.map { it.horas }.mediana() * ParametrosConsumo.FACTOR_INTERVALO_LARGO
        return intervalos.filter { it.horas <= limite }
    }
}
