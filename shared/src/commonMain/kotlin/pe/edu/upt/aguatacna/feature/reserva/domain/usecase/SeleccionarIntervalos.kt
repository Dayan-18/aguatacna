package pe.edu.upt.aguatacna.feature.reserva.domain.usecase

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ClaseIntervalo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.IntervaloConsumo

/** Elige los intervalos que sirven para estimar el consumo del hogar. */
class SeleccionarIntervalos {

    operator fun invoke(intervalos: List<IntervaloConsumo>): List<IntervaloConsumo> {
        val recientes = intervalos.sortedBy { it.fin }.takeLast(ParametrosConsumo.MAX_INTERVALOS)
        val sinOlvidos = descartarOlvidos(recientes)
        val observados = sinOlvidos.filter { it.clase == ClaseIntervalo.OBSERVADO }
        return observados.ifEmpty { sinOlvidos }
    }

    // Un intervalo mucho más largo que lo normal es un llenado que no se registró.
    private fun descartarOlvidos(intervalos: List<IntervaloConsumo>): List<IntervaloConsumo> {
        if (intervalos.isEmpty()) return intervalos
        val limite = intervalos.map { it.horas }.mediana() * ParametrosConsumo.FACTOR_INTERVALO_LARGO
        return intervalos.filter { it.horas <= limite }
    }
}
