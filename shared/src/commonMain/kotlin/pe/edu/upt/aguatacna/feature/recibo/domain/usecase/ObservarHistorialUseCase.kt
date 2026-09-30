package pe.edu.upt.aguatacna.feature.recibo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.domain.service.EvaluadorConsumo

// Slot en la ventana de 6 meses del gráfico (puede quedar vacío si no hay recibo ese mes).
data class BarraHistorialSlot(
    val periodo: PeriodoConsumo,
    val mesCorto: String,
    val consumoM3: Int?,
    val estado: EstadoConsumo?,
    val importeTotal: Dinero?,
    val promedioPrevio: Int?,
    val esAtipico: Boolean
)

// Estado completo para la pantalla de Historial.
data class HistorialCompleto(
    val ventana6Meses: List<BarraHistorialSlot>,
    val promedioHistorico: Int?,
    val mesSeleccionado: PeriodoConsumo
)

// Arma los 6 meses que terminan en el recibo más reciente; cada mes se evalúa con EvaluadorConsumo.
class ObservarHistorialUseCase(
    private val repository: ReciboRepository
) {
    operator fun invoke(): Flow<HistorialCompleto?> =
        repository.observarRecibos().map { recibos ->
            val ordenados = recibos.sortedBy { it.periodoConsumo }
            val masReciente = ordenados.lastOrNull()?.periodoConsumo ?: return@map null
            val periodos = generateSequence(masReciente) { it.anterior() }
                .take(MESES_GRAFICO)
                .toList()
                .reversed()

            HistorialCompleto(
                ventana6Meses = periodos.map { slot(it, ordenados) },
                promedioHistorico = EvaluadorConsumo.promedio(consumosPrevios(masReciente, ordenados)),
                mesSeleccionado = masReciente
            )
        }

    private fun slot(periodo: PeriodoConsumo, ordenados: List<Recibo>): BarraHistorialSlot {
        val recibo = ordenados.find { it.periodoConsumo == periodo }
        val previos = consumosPrevios(periodo, ordenados)
        val estado = recibo?.let {
            EvaluadorConsumo.evaluar(it.consumoM3, previos, it.tipoConsumo, periodo.mesLargo)
        }
        return BarraHistorialSlot(
            periodo = periodo,
            mesCorto = periodo.mesCorto,
            consumoM3 = recibo?.consumoM3,
            estado = estado,
            importeTotal = recibo?.importeTotal,
            promedioPrevio = EvaluadorConsumo.promedio(previos),
            esAtipico = estado is EstadoConsumo.Atipico
        )
    }

    // Consumos anteriores a [periodo], del más reciente al más antiguo.
    private fun consumosPrevios(periodo: PeriodoConsumo, ordenados: List<Recibo>): List<Int> =
        ordenados.filter { it.periodoConsumo < periodo }.map { it.consumoM3 }.reversed()

    private companion object {
        const val MESES_GRAFICO = 6
    }
}
