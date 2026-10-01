package pe.edu.upt.aguatacna.feature.reserva.domain.usecase

import pe.edu.upt.aguatacna.feature.reserva.domain.model.CapacidadLitros
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
            seleccionar.alcanzanParaEstimar(validos) -> EstimacionConsumo(medianaDe(validos), OrigenEstimacion.HISTORICO)
            porHabitos != null -> EstimacionConsumo(porHabitos, OrigenEstimacion.HABITOS)
            else -> EstimacionConsumo(consumoInicial(capacidad), OrigenEstimacion.ESTIMACION_INICIAL)
        }
    }

    private fun medianaDe(validos: List<IntervaloConsumo>): ConsumoHorario =
        ConsumoHorario(validos.map { it.consumoHorario.litrosPorHora }.mediana())

    private fun consumoInicial(capacidad: CapacidadLitros): ConsumoHorario =
        ConsumoHorario(capacidad.litros.valor / ParametrosConsumo.HORAS_ESTIMACION_INICIAL)
}
