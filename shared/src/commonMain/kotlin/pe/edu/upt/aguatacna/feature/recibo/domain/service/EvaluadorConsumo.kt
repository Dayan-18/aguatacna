package pe.edu.upt.aguatacna.feature.recibo.domain.service

import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TipoConsumo
import kotlin.math.roundToInt

// Única regla de "Alto consumo" del módulo: un mes que supera LIMITE_M3. El promedio y la variación
// solo se muestran como referencia; no cambian el estado.
object EvaluadorConsumo {

    const val LIMITE_M3 = 100
    const val MESES_VENTANA_PROMEDIO = 6

    /** Promedio redondeado de hasta 6 meses previos (más reciente primero); null si no hay ninguno. */
    fun promedio(mesesPrevios: List<Int>): Int? =
        mesesPrevios.take(MESES_VENTANA_PROMEDIO).takeIf { it.isNotEmpty() }?.average()?.roundToInt()

    /** Variación porcentual del consumo frente al promedio; null si no hay promedio con el cual comparar. */
    fun variacionPorcentaje(consumo: Int, promedio: Int?): Int? =
        promedio?.takeIf { it > 0 }?.let { ((consumo - it) * 100.0 / it).roundToInt() }

    // mesesPreviosM3: consumos de meses anteriores, más reciente primero.
    fun evaluar(
        consumoM3: Int,
        mesesPreviosM3: List<Int>,
        tipoConsumo: TipoConsumo,
        mesDisplay: String = ""
    ): EstadoConsumo {
        val promedio = promedio(mesesPreviosM3)
        val variacion = variacionPorcentaje(consumoM3, promedio)
        return when {
            consumoM3 > LIMITE_M3 -> EstadoConsumo.Atipico(variacion, promedio, mesDisplay)
            tipoConsumo == TipoConsumo.PROMEDIO -> EstadoConsumo.FacturadoPorPromedio
            promedio == null -> EstadoConsumo.SinHistorial
            else -> EstadoConsumo.Normal(variacion, promedio)
        }
    }
}
