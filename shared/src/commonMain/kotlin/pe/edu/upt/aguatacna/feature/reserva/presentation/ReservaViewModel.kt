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
import kotlinx.datetime.LocalTime
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.core.util.Reloj
import pe.edu.upt.aguatacna.feature.reserva.data.ReservaDePrueba
import pe.edu.upt.aguatacna.feature.reserva.domain.model.PerfilHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.AbastecimientosDelSector
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.ReservaRepository

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
    private var reservaActual: Reserva? = null

    init {
        viewModelScope.launch {
            combine(repositorio.observarPerfil(), repositorio.observarReserva(), reloj) { perfil, reserva, _ ->
                perfil to reserva
            }.collect { (perfil, reserva) -> publicar(perfil, reserva) }
        }
    }

    fun alEvento(evento: ReservaEvent) {
        viewModelScope.launch {
            val momento = ahora()
            val resultado = when (evento) {
                is ReservaEvent.RegistrarLlenado -> repositorio.registrarLlenado(momento, evento.tipo)
                ReservaEvent.ConfirmarLlenadoAsumido -> confirmarLlenadoAsumido()
                is ReservaEvent.CorregirHoraDelLlenado -> corregirHoraDelLlenado(evento.hora)
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

    // "Sí, se llenó": lo asumido pasa a ser un llenado real en la misma hora.
    private suspend fun confirmarLlenadoAsumido(): Result<Unit> {
        val llenado = reservaActual?.llenado ?: return Result.failure(IllegalStateException(SIN_RESERVA))
        return repositorio.registrarLlenado(llenado.momento, TipoLlenado.COMPLETO)
    }

    // "Corregir hora": el agua llegó el mismo día que se asumió, pero a otra hora.
    private suspend fun corregirHoraDelLlenado(hora: LocalTime): Result<Unit> {
        val llenado = reservaActual?.llenado ?: return Result.failure(IllegalStateException(SIN_RESERVA))
        return repositorio.registrarLlenado(LocalDateTime(llenado.momento.date, hora), TipoLlenado.COMPLETO)
    }

    private suspend fun publicar(perfil: PerfilHogar?, reserva: Reserva?) {
        reservaActual = reserva
        val momento = ahora()
        val vista = if (perfil == null || reserva == null) {
            null
        } else {
            val hogar = ContextoDelHogar(perfil.tipoReservorio, perfil.habitantes.cantidad, sector.nombreDelSector())
            construirVista(reserva, hogar, sector.proximoDesde(momento), repositorio.litrosPorHabitanteDia(), momento)
        }
        _uiState.update { it.copy(cargando = false, hogarConfigurado = perfil != null, vista = vista) }
    }

    companion object {
        private const val SIN_RESERVA = "Aún no hay una reserva"

        // Temporal: se reemplaza cuando el core conecte la inyección de dependencias (T028).
        fun conDatosDePrueba() = ReservaViewModel(ReservaDePrueba.repositorio, ReservaDePrueba.sector, ReservaDePrueba.reloj)

        /** Con la inyección iniciada usa la base real; sin ella (iOS aún) recurre a los datos de prueba. */
        fun desdeInyeccion(): ReservaViewModel {
            val koin = KoinPlatform.getKoinOrNull() ?: return conDatosDePrueba()
            return ReservaViewModel(koin.get(), koin.get(), koin.get<Reloj>()::ahora)
        }
    }
}
