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
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.core.util.Reloj
import pe.edu.upt.aguatacna.feature.reserva.data.ReservaDePrueba
import pe.edu.upt.aguatacna.feature.reserva.domain.model.PerfilHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Recomendacion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.AbastecimientosDelSector
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.ReservaRepository

private const val MILIS_POR_MINUTO = 60_000L

private fun cadaMinuto(): Flow<Unit> = flow {
    while (true) {
        emit(Unit)
        delay(MILIS_POR_MINUTO)
    }
}

/** Por defecto todas las recomendaciones vienen marcadas, como en el Figma; el usuario desmarca. */
class QueRecortarViewModel(
    private val repositorio: ReservaRepository,
    private val sector: AbastecimientosDelSector,
    private val ahora: () -> LocalDateTime,
    reloj: Flow<Unit> = cadaMinuto()
) : ViewModel() {

    private val desmarcadas = MutableStateFlow<Set<Recomendacion>>(emptySet())
    private val _uiState = MutableStateFlow(QueRecortarUiState())
    val uiState: StateFlow<QueRecortarUiState> = _uiState.asStateFlow()

    private val construirVista = ConstruirVistaRecortes()

    init {
        viewModelScope.launch {
            combine(repositorio.observarPerfil(), repositorio.observarReserva(), desmarcadas, reloj) { perfil, reserva, marcas, _ ->
                Triple(perfil, reserva, marcas)
            }.collect { (perfil, reserva, marcas) -> publicar(perfil, reserva, marcas) }
        }
    }

    fun alEvento(evento: QueRecortarEvent) {
        when (evento) {
            is QueRecortarEvent.Alternar -> desmarcadas.update {
                if (evento.recomendacion in it) it - evento.recomendacion else it + evento.recomendacion
            }
        }
    }

    private suspend fun publicar(perfil: PerfilHogar?, reserva: Reserva?, marcas: Set<Recomendacion>) {
        val momento = ahora()
        val vista = if (perfil == null || reserva == null) {
            null
        } else {
            construirVista(reserva, perfil.habitos, sector.proximoDesde(momento), marcas, momento)
        }
        _uiState.value = QueRecortarUiState(cargando = false, vista = vista)
    }

    companion object {
        fun conDatosDePrueba() = QueRecortarViewModel(ReservaDePrueba.repositorio, ReservaDePrueba.sector, ReservaDePrueba.reloj)

        fun desdeInyeccion(): QueRecortarViewModel {
            val koin = KoinPlatform.getKoinOrNull() ?: return conDatosDePrueba()
            return QueRecortarViewModel(koin.get(), koin.get(), koin.get<Reloj>()::ahora)
        }
    }
}
