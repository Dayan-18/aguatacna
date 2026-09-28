package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.edu.upt.aguatacna.feature.sector.data.FakeSectorRepository
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada
import pe.edu.upt.aguatacna.feature.sector.domain.repository.SectorRepository
import pe.edu.upt.aguatacna.feature.sector.domain.usecase.ResolverSector
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.data.local.UsuarioDao

class RegistrarDomicilioViewModel(
    private val repositorio: SectorRepository,
    private val ubicacion: Coordenada,
    private val guardarSectorEnUsuario: suspend (String) -> Unit = {}
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrarDomicilioUiState())
    val uiState: StateFlow<RegistrarDomicilioUiState> = _uiState.asStateFlow()

    private val resolverSector = ResolverSector()

    init {
        detectarSector()
    }

    fun detectarSector() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                val sector = resolverSector.resolver(ubicacion, repositorio.obtenerSectores())
                if (sector == null) {
                    _uiState.update { it.copy(cargando = false) }
                    return@launch
                }
                val minutos = repositorio.obtenerCronogramas(sector.id).firstOrNull()?.duracionMinutos
                _uiState.value = RegistrarDomicilioUiState(
                    cargando = false,
                    sector = sector,
                    continuidad = minutos?.let { "${duracionATexto(it)}/día" } ?: "Sin horario",
                    etiquetaMapa = "SECTOR ${sector.id.substringAfterLast('-')}"
                )
            } catch (e: Exception) {
                // Sin conexión o error del servidor: no dejamos la pantalla colgada.
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    // Guarda el sector detectado en el usuario local (Room). No necesita internet ni sesión.
    fun confirmarSector() {
        val sector = _uiState.value.sector ?: return
        viewModelScope.launch {
            try {
                guardarSectorEnUsuario(sector.id)
                _uiState.update { it.copy(guardado = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(guardado = false) }
            }
        }
    }

    companion object {
        // Casa de prueba en Ciudad Nueva, hasta tener el permiso de ubicación (semana 14).
        private val CASA_DE_PRUEBA = Coordenada(-17.9841, -70.2372)

        // Temporal: se reemplaza cuando exista la inyección de dependencias (core/di).
        @OptIn(ExperimentalTime::class)
        fun conDatosDePrueba(): RegistrarDomicilioViewModel {
            val hoy = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            return RegistrarDomicilioViewModel(FakeSectorRepository(hoy), CASA_DE_PRUEBA)
        }

        fun desdeInyeccion(): RegistrarDomicilioViewModel {
            val koin = KoinPlatform.getKoinOrNull() ?: return conDatosDePrueba()
            return RegistrarDomicilioViewModel(koin.get(), CASA_DE_PRUEBA, koin.get<UsuarioDao>()::guardarSector)
        }
    }
}
