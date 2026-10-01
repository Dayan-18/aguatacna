package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.edu.upt.aguatacna.data.local.UsuarioDao
import pe.edu.upt.aguatacna.feature.sector.data.FakeSectorRepository
import pe.edu.upt.aguatacna.feature.sector.domain.repository.SectorRepository
import pe.edu.upt.aguatacna.feature.sector.domain.usecase.BuscarCisternasCercanas
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import org.koin.mp.KoinPlatform

class PuntosCisternaViewModel(
    private val repositorio: SectorRepository,
    private val obtenerSectorGuardado: suspend () -> String? = { null }
) : ViewModel() {

    private val _uiState = MutableStateFlow(PuntosCisternaUiState())
    val uiState: StateFlow<PuntosCisternaUiState> = _uiState.asStateFlow()

    private val buscarCisternas = BuscarCisternasCercanas()

    init {
        cargar()
    }

    private fun cargar() {
        viewModelScope.launch {
            try {
                val sectores = repositorio.obtenerSectores()
                // Distancias desde la casa confirmada; si aún no hay casa, desde el centro de su sector.
                val guardado = obtenerSectorGuardado()
                val casa = repositorio.obtenerUbicacionCasa()
                    ?: sectores.firstOrNull { it.id == guardado }?.centro
                if (casa == null) {
                    _uiState.value = _uiState.value.copy(cargando = false)
                    return@launch
                }
                val puntos = sectores.flatMap { repositorio.obtenerPuntosCisterna(it.id) }
                _uiState.value = PuntosCisternaUiState(
                    cargando = false,
                    ubicacionCasa = casa,
                    cisternas = buscarCisternas.buscar(puntos, casa, RADIO_CISTERNAS_KM)
                )
            } catch (e: Exception) {
                // Sin conexión: mostramos la lista vacía en vez de un spinner infinito.
                _uiState.value = _uiState.value.copy(cargando = false)
            }
        }
    }

    companion object {
        private const val RADIO_CISTERNAS_KM = 10.0

        // Solo para previews sin Koin.
        @OptIn(ExperimentalTime::class)
        fun conDatosDePrueba(): PuntosCisternaViewModel {
            val hoy = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            return PuntosCisternaViewModel(FakeSectorRepository(hoy))
        }

        fun desdeInyeccion(): PuntosCisternaViewModel {
            val koin = KoinPlatform.getKoinOrNull() ?: return conDatosDePrueba()
            val usuarioDao = koin.get<UsuarioDao>()
            return PuntosCisternaViewModel(koin.get()) { usuarioDao.obtener()?.sectorId }
        }
    }
}
