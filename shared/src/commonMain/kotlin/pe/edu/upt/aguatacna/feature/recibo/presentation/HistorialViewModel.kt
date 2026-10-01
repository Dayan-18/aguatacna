package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
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
        val masReciente: BarraHistorialSlot
    ) : HistorialUiState
}

// Pantalla de Historial: muestra los 6 meses y prepara el borrador del mes que se quiera modificar.
class HistorialViewModel(
    observarHistorial: ObservarHistorialUseCase,
    private val repository: ReciboRepository,
    private val borradorStore: BorradorReciboStore
) : ViewModel() {

    val uiState: StateFlow<HistorialUiState> = observarHistorial()
        .map { historial ->
            if (historial == null) HistorialUiState.SinHistorial
            else HistorialUiState.ConDatos(historial.ventana6Meses, historial.masReciente)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistorialUiState.Cargando)

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
