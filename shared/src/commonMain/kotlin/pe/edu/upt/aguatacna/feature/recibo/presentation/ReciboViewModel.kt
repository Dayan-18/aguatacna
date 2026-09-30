package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.core.util.Reloj
import pe.edu.upt.aguatacna.core.util.RelojDelSistema
import pe.edu.upt.aguatacna.feature.recibo.data.BorradorReciboStore
import pe.edu.upt.aguatacna.feature.recibo.data.FakeReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.data.sync.SincronizadorRecibo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.model.aBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ObservarResumenUseCase

// Estado de la pantalla General del Recibo.
sealed interface ReciboUiState {

    data object Cargando : ReciboUiState

    // Sin recibos: mostrar solo "Escanea tu recibo"
    data object SinRecibos : ReciboUiState

    // Con datos: tarjeta activa, métricas y estado de consumo
    data class ConDatos(
        val recibo: Recibo,
        val importeDisplay: String,             // "74,20"
        val fechaVencimiento: String,           // "11 Set 2026"
        val estadoConsumo: EstadoConsumo,
        val promedioHistorico: Int?
    ) : ReciboUiState {
        val mes: String get() = recibo.periodoConsumo.displayCompleto
        val esAtipico: Boolean get() = estadoConsumo is EstadoConsumo.Atipico
    }
}

// ViewModel de la pantalla General: observa el resumen del recibo más reciente, prepara el borrador
// para revisar o ingresar un recibo y, mientras vive, mantiene los recibos sincronizados con la cuenta.
class ReciboViewModel(
    observarResumen: ObservarResumenUseCase,
    private val borradorStore: BorradorReciboStore,
    private val reloj: Reloj,
    sincronizador: SincronizadorRecibo?
) : ViewModel() {

    init {
        viewModelScope.launch { sincronizador?.mantenerSincronizado() }
    }

    val uiState: StateFlow<ReciboUiState> = observarResumen()
        .map { resumen ->
            if (resumen == null) {
                ReciboUiState.SinRecibos
            } else {
                ReciboUiState.ConDatos(
                    recibo = resumen.recibo,
                    importeDisplay = resumen.recibo.importeTotal.formatearSoloNumero(),
                    fechaVencimiento = resumen.fechaVencimiento,
                    estadoConsumo = resumen.estadoConsumo,
                    promedioHistorico = resumen.promedioHistorico
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReciboUiState.Cargando)

    fun iniciarManual() {
        borradorStore.guardar(ReciboBorrador.vacio(PeriodoConsumo.de(reloj.ahora().date)))
    }

    fun prepararRevision(recibo: Recibo) {
        borradorStore.guardar(recibo.aBorrador())
    }

    companion object {
        // Con la inyección iniciada usa Koin; sin ella recurre a datos de prueba en memoria.
        fun desdeInyeccion(): ReciboViewModel {
            val koin = KoinPlatform.getKoinOrNull() ?: return conDatosDePrueba()
            return ReciboViewModel(koin.get(), koin.get(), koin.get(), koin.getOrNull())
        }

        private fun conDatosDePrueba(): ReciboViewModel =
            ReciboViewModel(ObservarResumenUseCase(FakeReciboRepository()), BorradorReciboStore(), RelojDelSistema(), null)
    }
}
