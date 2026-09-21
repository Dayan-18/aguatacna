package pe.edu.upt.aguatacna.feature.recibo.presentation.captura

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

/**
 * Estados del flujo de captura de foto del recibo (T-6.1).
 */
sealed interface CapturaUiState {
    /** Estado inicial, listo para disparar cámara. */
    data object Inactivo : CapturaUiState

    /** Procesando OCR y analizando campos (T-6.4). */
    data object Procesando : CapturaUiState

    /** Recibo leído con éxito, listo para navegar a revisión (T-6.4). */
    data class Exito(val borrador: ReciboBorrador) : CapturaUiState

    /** Error al procesar o leer el recibo (T-6.6). */
    data class Error(val mensaje: String) : CapturaUiState
}

/**
 * ViewModel que orquesta la captura y procesamiento OCR del recibo.
 */
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
                    liberarFoto() // Liberar foto de memoria
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

    /** T-6.5: Liberar la foto de memoria al terminar (no persistirla). */
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
