package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.feature.recibo.data.FakeReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ObservarResumenUseCase

// Estado de la pantalla General del Recibo.
sealed interface ReciboUiState {

    data object Cargando : ReciboUiState

    // Sin recibos: mostrar solo "Escanea tu recibo"
    data object SinRecibos : ReciboUiState

    // Con datos: tarjeta activa, métricas y estado de consumo
    data class ConDatos(
        val mes: String,                        // "Agosto 2026"
        val periodoConsumo: PeriodoConsumo,
        val importeTotal: Dinero,
        val importeDisplay: String,             // "74,20"
        val fechaVencimiento: String,           // "11 Set 2026"
        val consumoM3: Int,
        val estadoConsumo: EstadoConsumo,
        val variacionTexto: String,             // "+106 %" o "—"
        val promedioHistorico: Int,
        val esAtipico: Boolean,
        val reciboOriginal: Recibo? = null
    ) : ReciboUiState
}

// ViewModel de la pantalla General: observa el resumen del recibo más reciente.
class ReciboViewModel(
    private val observarResumen: ObservarResumenUseCase
) : ViewModel() {

    val uiState: StateFlow<ReciboUiState> = observarResumen()
        .map { resumen ->
            if (resumen == null) {
                ReciboUiState.SinRecibos
            } else {
                val esAtipico = resumen.estadoConsumo is EstadoConsumo.Atipico

                val variacionTexto = resumen.estadoConsumo.variacionTexto

                ReciboUiState.ConDatos(
                    mes = resumen.mes,
                    periodoConsumo = resumen.periodoConsumo,
                    importeTotal = resumen.importeTotal,
                    importeDisplay = resumen.importeTotal.formatearSoloNumero(),
                    fechaVencimiento = resumen.fechaVencimiento,
                    consumoM3 = resumen.consumoM3,
                    estadoConsumo = resumen.estadoConsumo,
                    variacionTexto = variacionTexto,
                    promedioHistorico = resumen.promedioHistorico,
                    esAtipico = esAtipico,
                    reciboOriginal = resumen.reciboOriginal
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReciboUiState.Cargando)

    companion object {
        // Con la inyección iniciada usa Koin; sin ella recurre a datos de prueba en memoria.
        fun desdeInyeccion(): ReciboViewModel {
            val koin = KoinPlatform.getKoinOrNull() ?: return conDatosDePrueba()
            return ReciboViewModel(koin.get())
        }

        private fun conDatosDePrueba(): ReciboViewModel =
            ReciboViewModel(ObservarResumenUseCase(FakeReciboRepository()))
    }
}
