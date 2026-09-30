package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.core.util.Reloj
import pe.edu.upt.aguatacna.feature.recibo.data.BorradorReciboStore
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.service.ValidadorRecibo
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ConfirmarReciboUseCase
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ResultadoConfirmacion

// Pantalla de revisión: muestra el borrador con sus advertencias y lo confirma con el caso de uso.
class RevisionViewModel(
    private val borradorStore: BorradorReciboStore,
    private val confirmarRecibo: ConfirmarReciboUseCase,
    reloj: Reloj
) : ViewModel() {

    val borrador: StateFlow<ReciboBorrador?> = borradorStore.borrador

    val advertencias: StateFlow<List<String>> = borradorStore.borrador
        .map { actual -> actual?.let { ValidadorRecibo.validar(it).map { a -> a.mensaje } }.orEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _errorDuplicado = MutableStateFlow<String?>(null)
    val errorDuplicado: StateFlow<String?> = _errorDuplicado.asStateFlow()

    init {
        viewModelScope.launch {
            borradorStore.asegurar { ReciboBorrador.vacio(PeriodoConsumo.de(reloj.ahora().date)) }
        }
    }

    fun confirmar(onCompletado: () -> Unit) {
        val actual = borradorStore.borrador.value ?: return
        viewModelScope.launch {
            when (val resultado = confirmarRecibo(actual)) {
                is ResultadoConfirmacion.Guardado -> {
                    borradorStore.limpiar()
                    _errorDuplicado.value = null
                    onCompletado()
                }
                is ResultadoConfirmacion.Duplicado -> _errorDuplicado.value =
                    "Ya existe un recibo registrado para ${resultado.periodo.displayCompleto}. " +
                        "Modifícalo desde el historial o selecciona otro período."
                ResultadoConfirmacion.Incompleto -> Unit
            }
        }
    }

    fun descartarError() {
        _errorDuplicado.value = null
    }

    companion object {
        fun desdeInyeccion(): RevisionViewModel {
            val koin = KoinPlatform.getKoin()
            return RevisionViewModel(koin.get(), koin.get(), koin.get())
        }
    }
}
