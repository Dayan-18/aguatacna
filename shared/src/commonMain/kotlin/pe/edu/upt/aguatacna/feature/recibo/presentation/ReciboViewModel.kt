package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.feature.recibo.data.ReciboRepositoryEnMemoria
import pe.edu.upt.aguatacna.feature.recibo.data.SemillaDepuracion
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ObservarResumenUseCase

/**
 * ViewModel de la pantalla General (T-3.1).
 *
 * Observa el resumen del recibo más reciente y expone un [StateFlow] reactivo.
 * Si la semilla de depuración está activa y el repositorio está vacío, carga los datos de prueba.
 */
class ReciboViewModel(
    private val observarResumen: ObservarResumenUseCase,
    private val repository: ReciboRepository
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

    init {
        // Cargar semilla de depuración si está activa y no hay recibos previos (T-2.3)
        if (SemillaDepuracion.DEBUG_SEED_ENABLED) {
            viewModelScope.launch {
                val existentes = repository.observarRecibos().first()
                if (existentes.isEmpty()) {
                    SemillaDepuracion.generarRecibos().forEach {
                        repository.guardar(it)
                    }
                }
            }
        }
    }

    companion object {
        /** Con la inyección iniciada usa Koin; sin ella recurre a datos de prueba en memoria. */
        fun desdeInyeccion(): ReciboViewModel {
            val koin = KoinPlatform.getKoinOrNull() ?: return conDatosDePrueba()
            return ReciboViewModel(koin.get(), koin.get())
        }

        /** Fallback sin Koin: crea todo manualmente. */
        private fun conDatosDePrueba(): ReciboViewModel {
            val repo = ReciboRepositoryEnMemoria()
            val useCase = ObservarResumenUseCase(repo)
            return ReciboViewModel(useCase, repo)
        }
    }
}
