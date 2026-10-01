package pe.edu.upt.aguatacna.feature.recibo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.domain.service.EvaluadorConsumo

// Resumen del recibo más reciente para la pantalla General.
data class ResumenRecibo(
    val recibo: Recibo,
    val fechaVencimiento: String,
    val estadoConsumo: EstadoConsumo,
    val promedioHistorico: Int?
)

// Observa el recibo más reciente y evalúa su estado; emite null si no hay recibos.
class ObservarResumenUseCase(
    private val repository: ReciboRepository
) {
    operator fun invoke(): Flow<ResumenRecibo?> =
        repository.observarRecibos().map { recibos ->
            val ordenados = recibos.sortedByDescending { it.periodoConsumo }
            val actual = ordenados.firstOrNull() ?: return@map null
            val vencimiento = actual.periodoConsumo.vencimiento()
            ResumenRecibo(
                recibo = actual,
                fechaVencimiento = "${vencimiento.day} ${PeriodoConsumo.de(vencimiento).mesCorto} ${vencimiento.year}",
                estadoConsumo = EvaluadorConsumo.evaluar(actual.consumoM3),
                promedioHistorico = EvaluadorConsumo.promedio(ordenados.drop(1).map { it.consumoM3 })
            )
        }
}
