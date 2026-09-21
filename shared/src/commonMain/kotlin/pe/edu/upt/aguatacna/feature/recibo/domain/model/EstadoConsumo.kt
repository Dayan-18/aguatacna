package pe.edu.upt.aguatacna.feature.recibo.domain.model

/**
 * Estado de consumo evaluado según la regla de Sunass (sección 3.3 del plan).
 *
 * Cada estado lleva el texto y la etiqueta para la UI.
 */
sealed class EstadoConsumo {
    abstract val chipTexto: String
    abstract val mensaje: String
    abstract val variacionTexto: String

    /**
     * Alto consumo: supera los 100 m³.
     * Color: naranja (Ocre del tema).
     */
    data class Atipico(
        val excesoPorcentaje: Int,
        val promedioHistorico: Int,
        val mes: String
    ) : EstadoConsumo() {
        override val chipTexto: String = "Alto consumo"
        override val mensaje: String =
            "$mes supera los 100 m³. Se recomienda verificar posibles fugas o solicitar una revisión de medidor."
        override val variacionTexto: String = "+$excesoPorcentaje %"
    }

    /**
     * Consumo normal: dentro del límite de 100 m³.
     * Color: teal/celeste base (AguaMedia del tema).
     */
    data class Normal(
        val excesoPorcentaje: Int,
        val promedioHistorico: Int
    ) : EstadoConsumo() {
        override val chipTexto: String = "Normal"
        override val mensaje: String =
            "Consumo dentro de los límites normales (≤ 100 m³). Todo en orden."
        override val variacionTexto: String = if (excesoPorcentaje >= 0) "+$excesoPorcentaje %" else "$excesoPorcentaje %"
    }

    /**
     * Registro de recibo.
     * Color: azul/teal base.
     */
    data class SinHistorial(
        val mesesDisponibles: Int = 0
    ) : EstadoConsumo() {
        override val chipTexto: String = "Registro"
        override val mensaje: String =
            "Registro de consumo guardado correctamente."
        override val variacionTexto: String = "—"
    }

    /**
     * Facturado por promedio: no es lectura real.
     */
    data object FacturadoPorPromedio : EstadoConsumo() {
        override val chipTexto: String = "Por promedio"
        override val mensaje: String =
            "EPS facturó este mes por promedio; no es una lectura real."
        override val variacionTexto: String = "—"
    }
}
