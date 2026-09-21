package pe.edu.upt.aguatacna.feature.recibo.presentation.revision

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.feature.recibo.data.BorradorReciboStore
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ConfirmarReciboUseCase

/**
 * ViewModel de la pantalla de revisión ("Revisa tu recibo") (T-8.1).
 *
 * Observa el borrador actual desde [BorradorReciboStore] y ejecuta la confirmación (T-8.5).
 */
class RevisionViewModel(
    private val borradorStore: BorradorReciboStore,
    private val confirmarRecibo: ConfirmarReciboUseCase
) : ViewModel() {

    val borrador: StateFlow<ReciboBorrador?> = borradorStore.borrador

    fun confirmar(onCompletado: () -> Unit) {
        val actual = borradorStore.borrador.value ?: return
        if (!actual.esConfirmable) return

        viewModelScope.launch {
            val id = "recibo-${actual.periodoConsumo.valor?.anio}-${actual.periodoConsumo.valor?.mes}"
            val recibo = actual.confirmar(id)
            confirmarRecibo(recibo)
            borradorStore.limpiar()
            onCompletado()
        }
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
            return RevisionViewModel(koin.get(), koin.get())
        }
    }
}
