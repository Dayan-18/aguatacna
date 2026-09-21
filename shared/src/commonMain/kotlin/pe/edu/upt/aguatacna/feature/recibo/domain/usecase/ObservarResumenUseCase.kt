package pe.edu.upt.aguatacna.feature.recibo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.domain.service.EvaluadorConsumo

/**
 * Resumen para la pantalla General (T-3.1).
 */
data class ResumenRecibo(
    val mes: String,
    val periodoConsumo: PeriodoConsumo,
    val importeTotal: Dinero,
    val fechaVencimiento: String,
    val consumoM3: Int,
    val estadoConsumo: EstadoConsumo,
    val promedioHistorico: Int,
    val tieneRecibo: Boolean,
    val reciboOriginal: Recibo? = null
)

/**
 * Observa el recibo más reciente y calcula su estado para la pantalla General.
 * Emite null si no hay recibos.
 */
class ObservarResumenUseCase(
    private val repository: ReciboRepository
) {
    operator fun invoke(): Flow<ResumenRecibo?> =
        repository.observarRecibos().map { recibos ->
            if (recibos.isEmpty()) return@map null

            // Último recibo registrado por fecha
            val actual = recibos.maxByOrNull { it.periodoConsumo } ?: recibos.first()
            val anteriores = recibos.filter { it.id != actual.id }
            val promedio = if (anteriores.isNotEmpty()) {
                anteriores.map { it.consumoM3 }.average().toInt()
            } else {
                actual.consumoM3
            }

            val variacion = if (promedio > 0) {
                ((actual.consumoM3 - promedio) * 100) / promedio
            } else 0

            // Regla simple: más de 100 m³ es Alto Consumo
            val estado = if (actual.consumoM3 > 100) {
                EstadoConsumo.Atipico(variacion, promedio, actual.periodoConsumo.mesLargo)
            } else if (anteriores.isEmpty()) {
                EstadoConsumo.SinHistorial(0)
            } else {
                EstadoConsumo.Normal(variacion, promedio)
            }

            @Suppress("DEPRECATION")
            val vencimientoTexto = actual.fechaVencimiento?.let { fecha ->
                "${fecha.dayOfMonth} ${actual.periodoConsumo.mesCorto} ${fecha.year}"
            } ?: "—"

            ResumenRecibo(
                mes = actual.periodoConsumo.displayCompleto,
                periodoConsumo = actual.periodoConsumo,
                importeTotal = actual.importeTotal,
                fechaVencimiento = vencimientoTexto,
                consumoM3 = actual.consumoM3,
                estadoConsumo = estado,
                promedioHistorico = promedio,
                tieneRecibo = true,
                reciboOriginal = actual
            )
        }
}
