package pe.edu.upt.aguatacna.feature.retos.domain.model

@JvmInline
value class LitrosPorHabitanteDia(val valor: Double) {
    init {
        require(valor >= 0.0)
    }
}

data class Racha(val dias: Int) {
    init {
        require(dias >= 0)
    }
}

data class PosicionSector(
    val consumoHogar: LitrosPorHabitanteDia,
    val promedioSector: LitrosPorHabitanteDia
) {
    val diferencia: Double get() = consumoHogar.valor - promedioSector.valor
    val ahorraMasQueElPromedio: Boolean get() = diferencia < 0
}
