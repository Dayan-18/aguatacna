package pe.edu.upt.aguatacna.feature.reserva.domain.model

import kotlinx.datetime.LocalDateTime

// OBSERVADO: terminó con "me quedé sin agua", el tanque se vació de verdad.
// POR_LLENADO: terminó en otro llenado; solo es una cota del consumo real.
enum class ClaseIntervalo { OBSERVADO, POR_LLENADO }

data class IntervaloConsumo(
    val inicio: LocalDateTime,
    val fin: LocalDateTime,
    val litrosConsumidos: Litros,
    val clase: ClaseIntervalo
) {
    init {
        require(fin > inicio) { "El intervalo debe terminar después de empezar" }
        require(litrosConsumidos > Litros.CERO) { "El intervalo debe haber consumido agua" }
    }

    val horas: Double get() = horasEntre(inicio, fin)

    val consumoHorario: ConsumoHorario get() = ConsumoHorario(litrosConsumidos.valor / horas)

    val litrosPorDia: Double get() = litrosConsumidos.valor / (horas / HORAS_POR_DIA)

    private companion object {
        const val HORAS_POR_DIA = 24.0
    }
}
