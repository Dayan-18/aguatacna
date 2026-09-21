package pe.edu.upt.aguatacna.feature.bienvenida.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.core.sesion.InicioConGoogle
import pe.edu.upt.aguatacna.core.sesion.InicioConGoogleNoDisponible
import pe.edu.upt.aguatacna.core.sesion.ModoDeAcceso
import pe.edu.upt.aguatacna.core.sesion.RegistroDeAcceso
import pe.edu.upt.aguatacna.core.sesion.RegistroDeAccesoEnMemoria
import pe.edu.upt.aguatacna.core.sesion.ResultadoInicio

/** Lo que muestra la pantalla 01: si ya se sabe el modo de acceso, el consentimiento y el aviso de error. */
data class AccesoUiState(
    val cargando: Boolean = true,
    val modo: ModoDeAcceso? = null,
    val consentimiento: Boolean = true,
    val enCurso: Boolean = false,
    val mensaje: String? = null
) {
    val puedeEntrar: Boolean get() = consentimiento && !enCurso
}

class AccesoViewModel(
    private val registro: RegistroDeAcceso,
    private val google: InicioConGoogle
) : ViewModel() {
    private val _uiState = MutableStateFlow(AccesoUiState())
    val uiState: StateFlow<AccesoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            registro.observar().collect { modo -> _uiState.update { it.copy(cargando = false, modo = modo) } }
        }
    }

    fun alternarConsentimiento() = _uiState.update { it.copy(consentimiento = !it.consentimiento) }

    fun descartarMensaje() = _uiState.update { it.copy(mensaje = null) }

    fun empezarSinCuenta() {
        if (!_uiState.value.puedeEntrar) return
        viewModelScope.launch { registro.guardar(ModoDeAcceso.SIN_CUENTA) }
    }

    fun entrarConGoogle() {
        if (!_uiState.value.puedeEntrar) return
        _uiState.update { it.copy(enCurso = true, mensaje = null) }
        viewModelScope.launch {
            val resultado = google.iniciar()
            if (resultado is ResultadoInicio.Exitoso) registro.guardar(ModoDeAcceso.GOOGLE)
            _uiState.update { it.copy(enCurso = false, mensaje = mensajeDe(resultado)) }
        }
    }

    companion object {
        fun mensajeDe(resultado: ResultadoInicio): String? = when (resultado) {
            ResultadoInicio.Exitoso, ResultadoInicio.Cancelado -> null
            ResultadoInicio.NoDisponible -> "Entrar con Google aún no está disponible. Puedes empezar sin cuenta."
            is ResultadoInicio.Fallido -> "No pudimos entrar con Google. Inténtalo de nuevo o empieza sin cuenta."
        }

        /** Sin la inyección iniciada (vistas previas, iOS por ahora) se recuerda la elección solo en memoria. */
        fun desdeInyeccion(): AccesoViewModel {
            val koin = KoinPlatform.getKoinOrNull()
                ?: return AccesoViewModel(RegistroDeAccesoEnMemoria(), InicioConGoogleNoDisponible)
            return AccesoViewModel(koin.get(), koin.get())
        }
    }
}
