package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.data.FakeAbastecimientosDelSector
import pe.edu.upt.aguatacna.feature.reserva.data.FakeReservaRepository
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.AbastecimientosDelSector
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.ReservaRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val MILIS_POR_MINUTO = 60_000L

// El nivel baja con el tiempo aunque nada cambie en la base, así que la vista se refresca cada minuto.
private fun cadaMinuto(): Flow<Unit> = flow {
    while (true) {
        emit(Unit)
        delay(MILIS_POR_MINUTO)
    }
}

/** Orquesta: escucha la reserva, pide al dominio lo que hace falta y publica un único estado. */
class ReservaViewModel(
    private val repositorio: ReservaRepository,
    private val sector: AbastecimientosDelSector,
    private val ahora: () -> LocalDateTime,
    reloj: Flow<Unit> = cadaMinuto()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservaUiState())
    val uiState: StateFlow<ReservaUiState> = _uiState.asStateFlow()

    private val construirVista = ConstruirVistaReserva()

    init {
        viewModelScope.launch {
            combine(repositorio.observarReserva(), reloj) { reserva, _ -> reserva }
                .collect { publicar(it) }
        }
    }

    fun alEvento(evento: ReservaEvent) {
        viewModelScope.launch {
            val momento = ahora()
            val resultado = when (evento) {
                is ReservaEvent.RegistrarLlenado -> repositorio.registrarLlenado(momento, evento.tipo)
                ReservaEvent.AguaNoLlego -> repositorio.registrarSinLlegada(momento)
                ReservaEvent.MeQuedeSinAgua -> repositorio.declararSinAgua(momento)
                ReservaEvent.DescartarError -> {
                    _uiState.update { it.copy(error = null) }
                    return@launch
                }
            }
            resultado.onFailure { falla -> _uiState.update { it.copy(error = falla.message) } }
        }
    }

    private suspend fun publicar(reserva: Reserva?) {
        val momento = ahora()
        val vista = reserva?.let {
            construirVista(it, sector.proximoDesde(momento), repositorio.litrosPorHabitanteDia(), momento)
        }
        _uiState.update { it.copy(cargando = false, vista = vista) }
    }

    companion object {
        // Temporal: se reemplaza cuando el core conecte la inyección de dependencias (T028).
        @OptIn(ExperimentalTime::class)
        fun conDatosDePrueba(): ReservaViewModel {
            val reloj = { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
            return ReservaViewModel(
                FakeReservaRepository.conDatosDeEjemplo(reloj),
                FakeAbastecimientosDelSector(),
                reloj
            )
        }
    }
}
