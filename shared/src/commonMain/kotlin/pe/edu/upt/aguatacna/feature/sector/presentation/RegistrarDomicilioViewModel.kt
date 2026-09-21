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

class RegistrarDomicilioViewModel(
    private val repositorio: SectorRepository,
    private val ubicacion: Coordenada
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
    }
}
