package pe.edu.upt.aguatacna.feature.reserva.domain.usecase

// Valores iniciales; se ajustan con los hogares del piloto (spec, decisiones abiertas).
internal object ParametrosConsumo {
    const val MAX_INTERVALOS = 5
    const val FACTOR_INTERVALO_LARGO = 2.0
    const val HORAS_ESTIMACION_INICIAL = 48.0
}

internal fun List<Double>.mediana(): Double {
    require(isNotEmpty()) { "No hay valores para calcular la mediana" }
    val orden = sorted()
    val medio = orden.size / 2
    return if (orden.size % 2 == 1) orden[medio] else (orden[medio - 1] + orden[medio]) / 2
}
