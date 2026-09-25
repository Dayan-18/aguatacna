package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.feature.recibo.data.BorradorReciboStore
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.EscanearReciboUseCase

// Estados del flujo de captura de foto del recibo.
sealed interface CapturaUiState {
    data object Inactivo : CapturaUiState
    data object Procesando : CapturaUiState
    data class Exito(val borrador: ReciboBorrador) : CapturaUiState
    data class Error(val mensaje: String) : CapturaUiState
}

// Orquesta la captura de la foto y su procesamiento OCR.
class CapturaViewModel(
    private val escanearRecibo: EscanearReciboUseCase,
    private val borradorStore: BorradorReciboStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<CapturaUiState>(CapturaUiState.Inactivo)
    val uiState: StateFlow<CapturaUiState> = _uiState.asStateFlow()

    private var fotoEnMemoria: ByteArray? = null

    fun onFotoCapturada(bytes: ByteArray) {
        if (bytes.isEmpty()) {
            _uiState.value = CapturaUiState.Error("No se recibió la foto. Por favor, intenta de nuevo.")
            return
        }
        fotoEnMemoria = bytes
        _uiState.value = CapturaUiState.Procesando

        viewModelScope.launch {
            val resultado = withContext(Dispatchers.Default) {
                escanearRecibo(bytes)
            }

            resultado.fold(
                onSuccess = { borrador ->
                    borradorStore.guardar(borrador)
                    liberarFoto()
                    _uiState.value = CapturaUiState.Exito(borrador)
                },
                onFailure = { error ->
                    liberarFoto()
                    val mensaje = error.message ?: "No pudimos leer tu recibo. Prueba con más luz y el recibo completo en el encuadre."
                    _uiState.value = CapturaUiState.Error(mensaje)
                }
            )
        }
    }

    fun reiniciar() {
        liberarFoto()
        _uiState.value = CapturaUiState.Inactivo
    }

    // La foto nunca se persiste; se descarta apenas se procesa.
    private fun liberarFoto() {
        fotoEnMemoria = null
    }

    override fun onCleared() {
        super.onCleared()
        liberarFoto()
    }

    companion object {
        fun desdeInyeccion(): CapturaViewModel {
            val koin = KoinPlatform.getKoin()
            return CapturaViewModel(koin.get(), koin.get())
        }
    }
}
