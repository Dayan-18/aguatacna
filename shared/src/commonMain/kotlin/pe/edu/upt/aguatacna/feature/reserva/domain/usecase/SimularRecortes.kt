package pe.edu.upt.aguatacna.feature.reserva.domain.usecase

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Deficit
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Litros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Recomendacion

data class ResultadoRecortes(val ahorro: Litros, val horasGanadas: Double, val horasQueFaltan: Double) {
    val cubreElDeficit: Boolean get() = horasQueFaltan <= 0.0
}

class SimularRecortes {

    /** Cuántas horas de agua compra lo que el usuario marcó y cuántas siguen faltando. */
    operator fun invoke(deficit: Deficit, consumo: ConsumoHorario, elegidas: Set<Recomendacion>): ResultadoRecortes {
        val ahorro = elegidas.fold(Litros.CERO) { total, recomendacion -> total + recomendacion.litrosQueAhorra }
        val horasGanadas = ahorro.valor / consumo.litrosPorHora
        return ResultadoRecortes(ahorro, horasGanadas, (deficit.horas - horasGanadas).coerceAtLeast(0.0))
    }
}
