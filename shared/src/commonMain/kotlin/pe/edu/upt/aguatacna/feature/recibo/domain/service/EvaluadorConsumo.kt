package pe.edu.upt.aguatacna.feature.recibo.domain.service

import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TipoConsumo
import kotlin.math.roundToInt

// Evalúa el consumo según Sunass (Reglamento de Calidad, art. 88): atípico si excede
// en más del 100 % el promedio de los meses previos.
object EvaluadorConsumo {

    const val MESES_VENTANA_PROMEDIO = 6
    const val MESES_MINIMOS_HISTORIAL = 3
    const val UMBRAL_ATIPICO_PORCENTAJE = 100

    data class Metricas(
        val promedioHistorico: Double,
        val excesoPorcentaje: Int,
        val umbralAtipicoM3: Double
    )

    // mesesPreviosM3: consumos de meses anteriores, más reciente primero (se toman hasta MESES_VENTANA_PROMEDIO)
    fun evaluar(
        consumoM3: Int,
        mesesPreviosM3: List<Int>,
        tipoConsumo: TipoConsumo,
        mesDisplay: String = ""
    ): EstadoConsumo {
        // Recibos facturados por promedio → siempre estado neutro
        if (tipoConsumo == TipoConsumo.PROMEDIO) {
            return EstadoConsumo.FacturadoPorPromedio
        }

        // Ventana: hasta 6 meses previos
        val ventana = mesesPreviosM3.take(MESES_VENTANA_PROMEDIO)

        // Sin historial suficiente
        if (ventana.size < MESES_MINIMOS_HISTORIAL) {
            return EstadoConsumo.SinHistorial(mesesDisponibles = ventana.size)
        }

        val metricas = calcularMetricas(consumoM3, ventana)

        return if (metricas.excesoPorcentaje > UMBRAL_ATIPICO_PORCENTAJE) {
            EstadoConsumo.Atipico(
                excesoPorcentaje = metricas.excesoPorcentaje,
                promedioHistorico = metricas.promedioHistorico.roundToInt(),
                mes = mesDisplay
            )
        } else {
            EstadoConsumo.Normal(
                excesoPorcentaje = metricas.excesoPorcentaje,
                promedioHistorico = metricas.promedioHistorico.roundToInt()
            )
        }
    }

    fun calcularMetricas(consumoM3: Int, mesesPreviosM3: List<Int>): Metricas {
        val ventana = mesesPreviosM3.take(MESES_VENTANA_PROMEDIO)
        val promedio = if (ventana.isEmpty()) 0.0 else ventana.average()
        val exceso = if (promedio > 0) {
            ((consumoM3 - promedio) / promedio * 100).roundToInt()
        } else {
            0
        }
        val umbral = promedio * 2

        return Metricas(
            promedioHistorico = promedio,
            excesoPorcentaje = exceso,
            umbralAtipicoM3 = umbral
        )
    }
}
