package pe.edu.upt.aguatacna.feature.recibo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.domain.service.EvaluadorConsumo

/**
 * Datos de una barra del gráfico de historial.
 */
data class BarraHistorial(
    val periodo: PeriodoConsumo,
    val consumoM3: Int,
    val estado: EstadoConsumo,
    val importeTotal: Dinero,
    val promedioPrevio: Int = 0
)

/**
 * Slot en la ventana de 6 meses del gráfico.
 */
data class BarraHistorialSlot(
    val periodo: PeriodoConsumo,
    val mesCorto: String,
    val consumoM3: Int?,
    val estado: EstadoConsumo?,
    val importeTotal: Dinero?,
    val promedioPrevio: Int,
    val esAtipico: Boolean
)

/**
 * Estado completo para la pantalla de Historial.
 */
data class HistorialCompleto(
    val barras: List<BarraHistorial>,
    val ventana6Meses: List<BarraHistorialSlot>,
    val promedioHistorico: Int,
    val umbralAtipicoM3: Double,
    val mesSeleccionado: PeriodoConsumo?,
    val estadoMesSeleccionado: EstadoConsumo?
)

/**
 * Calcula los últimos 6 meses de forma simple y directa con umbral fijo en 100 m³.
 */
class ObservarHistorialUseCase(
    private val repository: ReciboRepository
) {
    operator fun invoke(): Flow<HistorialCompleto?> =
        repository.observarRecibos().map { recibos ->
            if (recibos.isEmpty()) return@map null

            val ordenados = recibos.sortedBy { it.periodoConsumo }
            val masReciente = ordenados.last()
            val masAntiguo = ordenados.first().periodoConsumo

            // Calcular inicio de la ventana de 6 meses de izquierda a derecha
            var span = 1
            var temp = masAntiguo
            while (temp < masReciente.periodoConsumo && span <= 6) {
                temp = temp.siguiente()
                span++
            }

            val inicio = if (span <= 6) {
                masAntiguo
            } else {
                var p = masReciente.periodoConsumo
                repeat(5) { p = p.anterior() }
                p
            }

            val periodosVentana = mutableListOf<PeriodoConsumo>()
            var p = inicio
            repeat(6) {
                periodosVentana.add(p)
                p = p.siguiente()
            }

            // Meses previos al más reciente para calcular el promedio histórico y umbral
            val mesesPreviosAlMasReciente = ordenados
                .filter { it.periodoConsumo < masReciente.periodoConsumo }
                .map { it.consumoM3 }
                .reversed()

            val metricas = EvaluadorConsumo.calcularMetricas(masReciente.consumoM3, mesesPreviosAlMasReciente)
            val promedioGeneral = if (mesesPreviosAlMasReciente.isNotEmpty()) metricas.promedioHistorico.toInt() else masReciente.consumoM3
            val umbral = metricas.umbralAtipicoM3

            val ventana6Meses = periodosVentana.map { per ->
                val recibo = ordenados.find { it.periodoConsumo == per }
                val consumo = recibo?.consumoM3
                val mesesPrevios = ordenados
                    .filter { it.periodoConsumo < per }
                    .map { it.consumoM3 }
                    .reversed()

                val estado = if (consumo != null) {
                    if (consumo > 100) {
                        EstadoConsumo.Atipico(
                            excesoPorcentaje = if (promedioGeneral > 0) ((consumo - promedioGeneral) * 100) / promedioGeneral else 100,
                            promedioHistorico = promedioGeneral,
                            mes = per.mesLargo
                        )
                    } else {
                        EvaluadorConsumo.evaluar(
                            consumoM3 = consumo,
                            mesesPreviosM3 = mesesPrevios,
                            tipoConsumo = recibo.tipoConsumo,
                            mesDisplay = per.mesLargo
                        )
                    }
                } else null

                val esAtipico = estado is EstadoConsumo.Atipico || (consumo != null && consumo > 100)

                BarraHistorialSlot(
                    periodo = per,
                    mesCorto = per.mesCorto,
                    consumoM3 = consumo,
                    estado = estado,
                    importeTotal = recibo?.importeTotal,
                    promedioPrevio = if (mesesPrevios.isNotEmpty()) mesesPrevios.average().toInt() else promedioGeneral,
                    esAtipico = esAtipico
                )
            }

            val barrasValidas = ventana6Meses.filter { it.consumoM3 != null }.map {
                BarraHistorial(
                    periodo = it.periodo,
                    consumoM3 = it.consumoM3!!,
                    estado = it.estado!!,
                    importeTotal = it.importeTotal!!,
                    promedioPrevio = it.promedioPrevio
                )
            }

            HistorialCompleto(
                barras = barrasValidas,
                ventana6Meses = ventana6Meses,
                promedioHistorico = promedioGeneral,
                umbralAtipicoM3 = umbral,
                mesSeleccionado = masReciente.periodoConsumo,
                estadoMesSeleccionado = ventana6Meses.find { it.periodo == masReciente.periodoConsumo }?.estado
            )
        }
}
