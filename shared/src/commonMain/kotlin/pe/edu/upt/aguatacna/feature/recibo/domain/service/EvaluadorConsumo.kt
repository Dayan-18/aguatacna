package pe.edu.upt.aguatacna.feature.recibo.domain.service

import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TipoConsumo
import kotlin.math.roundToInt

/**
 * Evalúa el consumo según la regla de Sunass (plan sección 3.2/3.3).
 *
 * Fuente: Reglamento de Calidad de la Prestación de los Servicios de Saneamiento,
 * art. 88 — Sunass. Se considera atípico el consumo que excede el 100 % del
 * promedio histórico.
 *
 * Constantes configurables en un solo lugar para facilitar cambios normativos.
 */
object EvaluadorConsumo {

    /** Meses previos usados para calcular el promedio (fuente: Sunass, art. 88). */
    const val MESES_VENTANA_PROMEDIO = 6

    /** Mínimo de meses previos para evaluar (debajo de este umbral → SinHistorial). */
    const val MESES_MINIMOS_HISTORIAL = 3

    /** Exceso mayor a 100 % sobre el promedio → atípico (Sunass). */
    const val UMBRAL_ATIPICO_PORCENTAJE = 100

    /**
     * Resultado intermedio con las métricas calculadas.
     */
    data class Metricas(
        val promedioHistorico: Double,
        val excesoPorcentaje: Int,
        val umbralAtipicoM3: Double
    )

    /**
     * Evalúa el estado de consumo para un recibo dado.
     *
     * @param consumoM3       Consumo facturado del mes a evaluar.
     * @param mesesPreviosM3  Lista de consumos de los meses anteriores (más reciente primero).
     *                        Se toman hasta [MESES_VENTANA_PROMEDIO] valores.
     * @param tipoConsumo     Tipo de consumo del recibo.
     * @param mesDisplay      Nombre del mes para el mensaje (ej: "Agosto").
     */
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

    /**
     * Calcula las métricas de consumo.
     */
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
