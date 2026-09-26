package pe.edu.upt.aguatacna.feature.asistente.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.feature.asistente.data.AsistenteRepositoryImpl
import pe.edu.upt.aguatacna.feature.asistente.domain.usecase.EnviarMensajeUseCase
import pe.edu.upt.aguatacna.feature.asistente.domain.usecase.LimpiarConversacionUseCase
import pe.edu.upt.aguatacna.feature.asistente.domain.usecase.ObservarMensajesUseCase

/**
 * ViewModel del Asistente Hídrico.
 * Conecta los casos de uso con el estado reactivo consumido por Compose.
 */
class AsistenteViewModel(
    private val observarMensajes: ObservarMensajesUseCase,
    private val enviarMensajeUseCase: EnviarMensajeUseCase,
    private val limpiarConversacionUseCase: LimpiarConversacionUseCase
) : ViewModel() {

    private val _estaEscribiendo = MutableStateFlow(false)
    private val _textoEntrada = MutableStateFlow("")
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AsistenteUiState> = combine(
        observarMensajes(),
        _estaEscribiendo,
        _textoEntrada,
        _error
    ) { mensajes, escribiendo, texto, error ->
        AsistenteUiState(
            mensajes = mensajes,
            estaEscribiendo = escribiendo,
            textoEntrada = texto,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AsistenteUiState()
    )

    fun onTextoEntradaCambiado(nuevoTexto: String) {
        _textoEntrada.value = nuevoTexto
    }

    fun enviarMensaje(textoPersonalizado: String? = null) {
        val textoAEnviar = (textoPersonalizado ?: _textoEntrada.value).trim()
        if (textoAEnviar.isBlank() || _estaEscribiendo.value) return

        _textoEntrada.value = ""
        _estaEscribiendo.value = true
        _error.value = null

        viewModelScope.launch {
            val resultado = enviarMensajeUseCase(textoAEnviar)
            _estaEscribiendo.value = false
            resultado.onFailure { error ->
                _error.value = error.message
            }
        }
    }

    fun reiniciarConversacion() {
        viewModelScope.launch {
            limpiarConversacionUseCase()
            _textoEntrada.value = ""
            _estaEscribiendo.value = false
            _error.value = null
        }
    }

    companion object {
        fun desdeInyeccion(): AsistenteViewModel {
            val koin = KoinPlatform.getKoinOrNull() ?: return conDatosDePrueba()
            return AsistenteViewModel(
                observarMensajes = koin.get(),
                enviarMensajeUseCase = koin.get(),
                limpiarConversacionUseCase = koin.get()
            )
        }

        private fun conDatosDePrueba(): AsistenteViewModel {
            val repositorio = AsistenteRepositoryImpl()
            return AsistenteViewModel(
                observarMensajes = ObservarMensajesUseCase(repositorio),
                enviarMensajeUseCase = EnviarMensajeUseCase(repositorio),
                limpiarConversacionUseCase = LimpiarConversacionUseCase(repositorio)
            )
        }
    }
}
