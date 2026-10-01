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
    val consumoM3: Int?,
    val estado: EstadoConsumo?,
    val importeTotal: Dinero?,
    val promedioPrevio: Int?,
    val variacionPorcentaje: Int?
) {
    val esAltoConsumo: Boolean get() = estado == EstadoConsumo.ALTO_CONSUMO
}

// Los 6 meses del gráfico; el último siempre es el recibo más reciente.
data class HistorialCompleto(val ventana6Meses: List<BarraHistorialSlot>) {
    val masReciente: BarraHistorialSlot get() = ventana6Meses.last()
}

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
            HistorialCompleto(periodos.map { slot(it, ordenados) })
        }

    private fun slot(periodo: PeriodoConsumo, ordenados: List<Recibo>): BarraHistorialSlot {
        val recibo = ordenados.find { it.periodoConsumo == periodo }
        val promedio = EvaluadorConsumo.promedio(
            ordenados.filter { it.periodoConsumo < periodo }.map { it.consumoM3 }.reversed()
        )
        return BarraHistorialSlot(
            periodo = periodo,
            consumoM3 = recibo?.consumoM3,
            estado = recibo?.let { EvaluadorConsumo.evaluar(it.consumoM3) },
            importeTotal = recibo?.importeTotal,
            promedioPrevio = promedio,
            variacionPorcentaje = recibo?.let { EvaluadorConsumo.variacionPorcentaje(it.consumoM3, promedio) }
        )
    }

    private companion object {
        const val MESES_GRAFICO = 6
    }
}
