package pe.edu.upt.aguatacna.feature.reserva.domain.usecase

import pe.edu.upt.aguatacna.feature.reserva.domain.model.CapacidadLitros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ClaseIntervalo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.IntervaloConsumo

enum class OrigenEstimacion { HISTORICO, HABITOS, ESTIMACION_INICIAL }

data class EstimacionConsumo(val consumo: ConsumoHorario, val origen: OrigenEstimacion)

class EstimarConsumo(private val seleccionar: SeleccionarIntervalos = SeleccionarIntervalos()) {

    operator fun invoke(
        intervalos: List<IntervaloConsumo>,
        capacidad: CapacidadLitros,
        porHabitos: ConsumoHorario? = null
    ): EstimacionConsumo {
        val validos = seleccionar(intervalos)
        return when {
            alcanzanParaEstimar(validos) -> EstimacionConsumo(medianaDe(validos), OrigenEstimacion.HISTORICO)
            porHabitos != null -> EstimacionConsumo(porHabitos, OrigenEstimacion.HABITOS)
            else -> EstimacionConsumo(consumoInicial(capacidad), OrigenEstimacion.ESTIMACION_INICIAL)
        }
    }

    // Un intervalo observado es dato exacto y alcanza solo; los inferidos necesitan dos.
    private fun alcanzanParaEstimar(validos: List<IntervaloConsumo>): Boolean =
        validos.any { it.clase == ClaseIntervalo.OBSERVADO } || validos.size >= MINIMO_INFERIDOS

    private fun medianaDe(validos: List<IntervaloConsumo>): ConsumoHorario =
        ConsumoHorario(validos.map { it.consumoHorario.litrosPorHora }.mediana())

    private fun consumoInicial(capacidad: CapacidadLitros): ConsumoHorario =
        ConsumoHorario(capacidad.litros.valor / ParametrosConsumo.HORAS_ESTIMACION_INICIAL)

    private companion object {
        const val MINIMO_INFERIDOS = 2
    }
}
