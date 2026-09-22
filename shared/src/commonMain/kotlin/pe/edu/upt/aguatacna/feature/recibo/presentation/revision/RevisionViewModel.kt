package pe.edu.upt.aguatacna.feature.recibo.presentation.revision

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.feature.recibo.data.BorradorReciboStore
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ConfirmarReciboUseCase

/**
 * ViewModel de la pantalla de revisión ("Revisa tu recibo") (T-8.1).
 *
 * Observa el borrador actual desde [BorradorReciboStore] y ejecuta la confirmación (T-8.5).
 * Verifica que no exista un recibo previo con el mismo período antes de confirmar.
 */
class RevisionViewModel(
    private val borradorStore: BorradorReciboStore,
    private val confirmarRecibo: ConfirmarReciboUseCase,
    private val repository: ReciboRepository
) : ViewModel() {

    val borrador: StateFlow<ReciboBorrador?> = borradorStore.borrador

    private val _errorDuplicado = MutableStateFlow<String?>(null)
    /** Mensaje de error cuando se intenta confirmar un período que ya tiene recibo. */
    val errorDuplicado: StateFlow<String?> = _errorDuplicado.asStateFlow()

    fun confirmar(onCompletado: () -> Unit) {
        val actual = borradorStore.borrador.value ?: return
        if (!actual.esConfirmable) return

        viewModelScope.launch {
            val periodo = actual.periodoConsumo.valor
            if (periodo != null) {
                val existente = repository.obtenerPorPeriodo(periodo)
                if (existente != null) {
                    _errorDuplicado.value =
                        "Ya existe un recibo registrado para ${periodo.displayCompleto}. " +
                        "Modifícalo desde el historial o selecciona otro período."
                    return@launch
                }
            }

            val id = "recibo-${actual.periodoConsumo.valor?.anio}-${actual.periodoConsumo.valor?.mes}"
            val recibo = actual.confirmar(id)
            confirmarRecibo(recibo)
            borradorStore.limpiar()
            _errorDuplicado.value = null
            onCompletado()
        }
    }

    fun descartarError() {
        _errorDuplicado.value = null
    }

    fun guardarBorrador(borrador: ReciboBorrador) {
        borradorStore.guardar(borrador)
    }

    fun descartar() {
        borradorStore.limpiar()
    }

    companion object {
        fun desdeInyeccion(): RevisionViewModel {
            val koin = KoinPlatform.getKoin()
            return RevisionViewModel(koin.get(), koin.get(), koin.get())
        }
    }
}

