package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.feature.recibo.data.BorradorReciboStore
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.model.aBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.BarraHistorialSlot
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ObservarHistorialUseCase

// Estado de UI de la pantalla de Historial de Consumo.
sealed interface HistorialUiState {
    data object Cargando : HistorialUiState
    data object SinHistorial : HistorialUiState

    data class ConDatos(
        val barras: List<BarraHistorialSlot>,
        val promedioHistorico: Int?,
        val seleccionado: BarraHistorialSlot
    ) : HistorialUiState
}

// Pantalla de Historial: elige el mes seleccionado y prepara el borrador para modificar un recibo.
class HistorialViewModel(
    observarHistorial: ObservarHistorialUseCase,
    private val repository: ReciboRepository,
    private val borradorStore: BorradorReciboStore
) : ViewModel() {

    private val mesElegido = MutableStateFlow<PeriodoConsumo?>(null)

    val uiState: StateFlow<HistorialUiState> = combine(observarHistorial(), mesElegido) { historial, elegido ->
        if (historial == null) return@combine HistorialUiState.SinHistorial
        val seleccionado = historial.ventana6Meses.find { it.periodo == elegido }
            ?: historial.ventana6Meses.first { it.periodo == historial.mesSeleccionado }
        HistorialUiState.ConDatos(historial.ventana6Meses, historial.promedioHistorico, seleccionado)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistorialUiState.Cargando)

    fun seleccionarMes(periodo: PeriodoConsumo) {
        mesElegido.value = periodo
    }

    fun prepararEdicion(periodo: PeriodoConsumo, onListo: () -> Unit) {
        viewModelScope.launch {
            val recibo = repository.obtenerPorPeriodo(periodo)
            borradorStore.guardar(recibo?.aBorrador() ?: ReciboBorrador.vacio(periodo))
            onListo()
        }
    }

    companion object {
        fun desdeInyeccion(): HistorialViewModel {
            val koin = KoinPlatform.getKoin()
            return HistorialViewModel(koin.get(), koin.get(), koin.get())
        }
    }
}
