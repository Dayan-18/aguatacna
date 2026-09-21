package pe.edu.upt.aguatacna.feature.recibo.presentation

import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.BarraHistorialSlot

/**
 * UI State para la pantalla de Historial de Consumo (T-10.1).
 */
sealed interface HistorialUiState {
    data object Cargando : HistorialUiState
    data object SinHistorial : HistorialUiState

    data class ConDatos(
        val barras: List<BarraHistorialSlot>,
        val promedioHistorico: Int,
        val umbralAtipicoM3: Double,
        val mesSeleccionado: PeriodoConsumo,
        val consumoSeleccionado: Int?,
        val estadoSeleccionado: EstadoConsumo?,
        val importeSeleccionado: Dinero?,
        val promedioHistoricoSeleccionado: Int,
        val excesoPorcentajeSeleccionado: Int?,
        val esAtipico: Boolean,
        val mostrarDialogoReclamo: Boolean = false
    ) : HistorialUiState
}
