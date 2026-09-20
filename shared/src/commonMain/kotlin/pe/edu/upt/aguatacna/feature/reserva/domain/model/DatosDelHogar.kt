package pe.edu.upt.aguatacna.feature.reserva.domain.model

/** Lo que la reserva necesita saber del hogar; se llena desde el perfil del hogar. */
data class DatosDelHogar(
    val capacidad: CapacidadLitros,
    val habitantes: Habitantes,
    val consumoPorHabitos: ConsumoHorario? = null
)
