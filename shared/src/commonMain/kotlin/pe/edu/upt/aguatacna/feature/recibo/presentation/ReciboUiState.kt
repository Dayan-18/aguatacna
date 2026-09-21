package pe.edu.upt.aguatacna.feature.recibo.presentation

import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo

/**
 * Estado de la pantalla General del Recibo (T-3.1).
 */
sealed interface ReciboUiState {

    /** Cargando datos iniciales. */
    data object Cargando : ReciboUiState

    /** Sin recibos: mostrar solo "Escanea tu recibo" (T-3.2). */
    data object SinRecibos : ReciboUiState

    /** Con datos: tarjeta activa, métricas, estado (T-3.3). */
    data class ConDatos(
        val mes: String,                        // "Agosto 2026"
        val periodoConsumo: PeriodoConsumo,
        val importeTotal: Dinero,
        val importeDisplay: String,             // "74,20"
        val fechaVencimiento: String,           // "28 Ago 2026"
        val consumoM3: Int,
        val estadoConsumo: EstadoConsumo,
        val variacionTexto: String,             // "+106 %" o "—"
        val promedioHistorico: Int,
        val esAtipico: Boolean,
        val reciboOriginal: pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo? = null
    ) : ReciboUiState
}
