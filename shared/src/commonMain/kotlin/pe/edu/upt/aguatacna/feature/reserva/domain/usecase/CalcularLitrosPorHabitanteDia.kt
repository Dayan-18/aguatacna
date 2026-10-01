package pe.edu.upt.aguatacna.feature.reserva.domain.usecase

import pe.edu.upt.aguatacna.feature.reserva.domain.model.Habitantes
import pe.edu.upt.aguatacna.feature.reserva.domain.model.IntervaloConsumo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.LitrosPorHabitanteDia

class CalcularLitrosPorHabitanteDia(
    private val seleccionar: SeleccionarIntervalos = SeleccionarIntervalos()
) {
    /** `null` mientras no haya los mismos datos que exige el consumo estimado: así los dos indicadores no se contradicen. */
    operator fun invoke(intervalos: List<IntervaloConsumo>, habitantes: Habitantes): LitrosPorHabitanteDia? {
        val validos = seleccionar(intervalos)
        if (!seleccionar.alcanzanParaEstimar(validos)) return null
        val consumoDiario = validos.map { it.litrosPorDia }.mediana()
        return LitrosPorHabitanteDia(consumoDiario / habitantes.cantidad)
    }
}
