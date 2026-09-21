package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.edu.upt.aguatacna.feature.sector.data.FakeSectorRepository
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada
import pe.edu.upt.aguatacna.feature.sector.domain.repository.SectorRepository
import pe.edu.upt.aguatacna.feature.sector.domain.usecase.BuscarCisternasCercanas
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class PuntosCisternaViewModel(
    private val repositorio: SectorRepository,
    private val ubicacion: Coordenada
) : ViewModel() {

    private val _uiState = MutableStateFlow(PuntosCisternaUiState())
    val uiState: StateFlow<PuntosCisternaUiState> = _uiState.asStateFlow()

    private val buscarCisternas = BuscarCisternasCercanas()

    init {
        cargar()
    }

    private fun cargar() {
        viewModelScope.launch {
            val sectores = repositorio.obtenerSectores()
            val puntos = sectores.flatMap { repositorio.obtenerPuntosCisterna(it.id) }
            _uiState.value = PuntosCisternaUiState(
                cargando = false,
                cisternas = buscarCisternas.buscar(puntos, ubicacion, RADIO_CISTERNAS_KM)
            )
        }
    }

    companion object {
        private const val RADIO_CISTERNAS_KM = 10.0

        // Casa de prueba en Ciudad Nueva, hasta tener el permiso de ubicación (semana 14).
        private val CASA_DE_PRUEBA = Coordenada(-17.9841, -70.2372)

        // Temporal: se reemplaza cuando exista la inyección de dependencias (core/di).
        @OptIn(ExperimentalTime::class)
        fun conDatosDePrueba(): PuntosCisternaViewModel {
            val hoy = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            return PuntosCisternaViewModel(FakeSectorRepository(hoy), CASA_DE_PRUEBA)
        }
    }
}
