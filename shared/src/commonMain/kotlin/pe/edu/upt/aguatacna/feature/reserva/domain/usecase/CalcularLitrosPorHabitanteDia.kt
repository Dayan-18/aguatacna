package pe.edu.upt.aguatacna.feature.reserva.domain.usecase

import pe.edu.upt.aguatacna.feature.reserva.domain.model.Habitantes
import pe.edu.upt.aguatacna.feature.reserva.domain.model.IntervaloConsumo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.LitrosPorHabitanteDia

class CalcularLitrosPorHabitanteDia(
    private val seleccionar: SeleccionarIntervalos = SeleccionarIntervalos()
) {
    /** `null` si el hogar aún no tiene ningún intervalo válido. */
    operator fun invoke(intervalos: List<IntervaloConsumo>, habitantes: Habitantes): LitrosPorHabitanteDia? {
        val validos = seleccionar(intervalos)
        if (validos.isEmpty()) return null
        val consumoDiario = validos.map { it.litrosPorDia }.mediana()
        return LitrosPorHabitanteDia(consumoDiario / habitantes.cantidad)
    }
}
