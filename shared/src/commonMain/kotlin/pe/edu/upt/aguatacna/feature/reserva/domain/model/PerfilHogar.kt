package pe.edu.upt.aguatacna.feature.reserva.domain.model

enum class TipoReservorio { TANQUE_ELEVADO, CISTERNA, BIDONES }

data class HabitosDelHogar(
    val duchasPorDia: Int,
    val usaLavadora: Boolean,
    val riegaJardin: Boolean
) {
    init {
        require(duchasPorDia >= 0) { "Las duchas por día no pueden ser negativas: $duchasPorDia" }
    }
}

/** Lo que el usuario declara de su hogar en la configuración inicial. */
data class PerfilHogar(
    val usuarioId: String,
    val tipoReservorio: TipoReservorio,
    val capacidad: CapacidadLitros,
    val habitantes: Habitantes,
    val habitos: HabitosDelHogar,
    val consumoPorHabitos: ConsumoHorario? = null,
    val consumoVigente: ConsumoHorario? = null
) {
    val configuracion: ConfiguracionHogar
        get() = ConfiguracionHogar(tipoReservorio, capacidad, habitantes, habitos)
}
