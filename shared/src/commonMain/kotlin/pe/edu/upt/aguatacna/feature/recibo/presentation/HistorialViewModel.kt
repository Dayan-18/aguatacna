package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ObservarHistorialUseCase

/**
 * ViewModel para la pantalla de Historial (T-10.1).
 * Gestiona la selección de mes (T-10.7) y la sincronización reactiva de datos.
 */
class HistorialViewModel(
    private val observarHistorial: ObservarHistorialUseCase
) : ViewModel() {

    private val _mesSeleccionadoOverride = MutableStateFlow<PeriodoConsumo?>(null)
    private val _mostrarDialogoReclamo = MutableStateFlow(false)

    val uiState: StateFlow<HistorialUiState> = combine(
        observarHistorial(),
        _mesSeleccionadoOverride,
        _mostrarDialogoReclamo
    ) { historial, mesOverride, mostrarReclamo ->
        if (historial == null || historial.ventana6Meses.isEmpty()) {
            return@combine HistorialUiState.SinHistorial
        }

        // Determinar qué mes está seleccionado
        val periodoSeleccionado = mesOverride
            ?: historial.mesSeleccionado
            ?: historial.ventana6Meses.lastOrNull { it.consumoM3 != null }?.periodo
            ?: historial.ventana6Meses.last().periodo

        // Buscar el slot correspondiente en la ventana de 6 meses
        val slotSeleccionado = historial.ventana6Meses.find { it.periodo == periodoSeleccionado }

        val consumo = slotSeleccionado?.consumoM3
        val estado = slotSeleccionado?.estado
        val importe = slotSeleccionado?.importeTotal
        val promPrevio = slotSeleccionado?.promedioPrevio ?: historial.promedioHistorico

        val excesoPct = if (consumo != null && promPrevio > 0) {
            val dif = consumo - promPrevio
            ((dif.toDouble() / promPrevio) * 100).toInt()
        } else null

        val esAtipico = estado is EstadoConsumo.Atipico

        HistorialUiState.ConDatos(
            barras = historial.ventana6Meses,
            promedioHistorico = historial.promedioHistorico,
            umbralAtipicoM3 = historial.umbralAtipicoM3,
            mesSeleccionado = periodoSeleccionado,
            consumoSeleccionado = consumo,
            estadoSeleccionado = estado,
            importeSeleccionado = importe,
            promedioHistoricoSeleccionado = promPrevio,
            excesoPorcentajeSeleccionado = excesoPct,
            esAtipico = esAtipico,
            mostrarDialogoReclamo = mostrarReclamo
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistorialUiState.Cargando)

    fun seleccionarMes(periodo: PeriodoConsumo) {
        _mesSeleccionadoOverride.value = periodo
    }

    fun mostrarDialogoReclamo(mostrar: Boolean) {
        _mostrarDialogoReclamo.value = mostrar
    }

    companion object {
        fun desdeInyeccion(): HistorialViewModel {
            val koin = KoinPlatform.getKoin()
            return HistorialViewModel(koin.get())
        }
    }
}
