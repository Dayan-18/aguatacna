package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.edu.upt.aguatacna.feature.sector.data.FakeSectorRepository
import pe.edu.upt.aguatacna.feature.sector.domain.model.ConfirmacionHorario
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada
import pe.edu.upt.aguatacna.feature.sector.domain.model.TipoConfirmacion
import pe.edu.upt.aguatacna.feature.sector.domain.repository.SectorRepository
import pe.edu.upt.aguatacna.feature.sector.domain.usecase.BuscarCisternasCercanas
import pe.edu.upt.aguatacna.feature.sector.domain.usecase.ObtenerCronogramaVigente
import pe.edu.upt.aguatacna.feature.sector.domain.usecase.ProximoAbastecimiento
import pe.edu.upt.aguatacna.feature.sector.domain.usecase.ResolverSector
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SectorViewModel(
    private val repositorio: SectorRepository,
    private val ubicacionCasa: Coordenada,
    private val ahora: () -> LocalDateTime
) : ViewModel() {

    private val _uiState = MutableStateFlow(SectorUiState())
    val uiState: StateFlow<SectorUiState> = _uiState.asStateFlow()

    private val resolverSector = ResolverSector()
    private val cronogramaVigente = ObtenerCronogramaVigente()
    private val proximoAbastecimiento = ProximoAbastecimiento()
    private val buscarCisternas = BuscarCisternasCercanas()

    init {
        cargarSector()
    }

    private fun cargarSector() {
        viewModelScope.launch {
            val momento = ahora()
            val sectores = repositorio.obtenerSectores()
            val sector = resolverSector.resolver(ubicacionCasa, sectores)
            if (sector == null) {
                _uiState.update { it.copy(cargando = false) }
                return@launch
            }
            val cronogramas = repositorio.obtenerCronogramas(sector.id)
            val puntos = sectores.flatMap { repositorio.obtenerPuntosCisterna(it.id) }
            _uiState.value = SectorUiState(
                cargando = false,
                ahora = momento,
                sector = sector,
                ubicacionCasa = ubicacionCasa,
                cronogramaDeHoy = cronogramas.firstOrNull { it.fecha == momento.date },
                aguaLlegandoAhora = cronogramaVigente.obtener(cronogramas, momento) != null,
                proximoAbastecimiento = proximoAbastecimiento.calcular(cronogramas, momento),
                cisternas = buscarCisternas.buscar(puntos, ubicacionCasa, RADIO_CISTERNAS_KM),
                confirmacionesDeHoy = repositorio.obtenerConfirmaciones(sector.id, momento.date).size
            )
        }
    }

    fun confirmar(tipo: TipoConfirmacion) {
        val sector = _uiState.value.sector ?: return
        viewModelScope.launch {
            val momento = ahora()
            repositorio.registrarConfirmacion(
                ConfirmacionHorario(
                    id = "$tipo-$momento",
                    sectorId = sector.id,
                    // Provisional hasta que core/ genere el UUID local (constitución, artículo III).
                    usuarioId = "invitado",
                    momento = momento,
                    tipo = tipo
                )
            )
            val total = repositorio.obtenerConfirmaciones(sector.id, momento.date).size
            _uiState.update {
                it.copy(confirmacionesDeHoy = total, mensaje = "Gracias, registramos tu confirmación")
            }
        }
    }

    companion object {
        private const val RADIO_CISTERNAS_KM = 10.0

        // Casa de prueba en Ciudad Nueva, hasta tener el permiso de ubicación (semana 14).
        private val CASA_DE_PRUEBA = Coordenada(-17.9841, -70.2372)

        // Temporal: se reemplaza cuando exista la inyección de dependencias (core/di).
        @OptIn(ExperimentalTime::class)
        fun conDatosDePrueba(): SectorViewModel {
            val reloj = { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
            return SectorViewModel(FakeSectorRepository(reloj().date), CASA_DE_PRUEBA, reloj)
        }
    }
}
