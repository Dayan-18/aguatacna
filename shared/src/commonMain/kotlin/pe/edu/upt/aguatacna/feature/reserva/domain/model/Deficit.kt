package pe.edu.upt.aguatacna.feature.reserva.domain.model

/** Horas que el hogar pasaría sin agua antes de que vuelva el suministro. */
data class Deficit(val horas: Double) {
    init {
        require(horas.isFinite() && horas >= 0.0) { "El déficit no puede ser negativo: $horas" }
    }

    val hayDeficit: Boolean get() = horas > 0.0
}
