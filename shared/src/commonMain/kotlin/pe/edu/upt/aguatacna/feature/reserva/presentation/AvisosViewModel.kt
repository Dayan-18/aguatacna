package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.core.util.Reloj
import pe.edu.upt.aguatacna.core.util.RelojDelSistema
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.RegistroDeAvisos

class AvisosViewModel(
    private val registro: RegistroDeAvisos,
    private val ahora: () -> LocalDateTime
) : ViewModel() {

    private val _uiState = MutableStateFlow(AvisosUiState())
    val uiState: StateFlow<AvisosUiState> = _uiState.asStateFlow()

    private val construirVista = ConstruirVistaAvisos()

    init {
        viewModelScope.launch {
            registro.observar().collect { guardados ->
                _uiState.value = AvisosUiState(cargando = false, avisos = construirVista(guardados, ahora()))
            }
        }
    }

    /** Se llama al salir de la pantalla: lo que se vio deja de contar como nuevo. */
    fun marcarComoLeidos() {
        viewModelScope.launch { registro.marcarTodosComoLeidos() }
    }

    companion object {
        /** Sin la inyección iniciada (iOS aún) la lista queda vacía, en lugar de fallar. */
        fun desdeInyeccion(): AvisosViewModel {
            val koin = KoinPlatform.getKoinOrNull()
            val reloj = koin?.get<Reloj>() ?: RelojDelSistema()
            return AvisosViewModel(koin?.get() ?: RegistroVacio, reloj::ahora)
        }
    }
}
