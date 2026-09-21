package pe.edu.upt.aguatacna.feature.recibo.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador

/**
 * Almacén en memoria del borrador de recibo actualmente en captura/revisión (T-6.4, T-8.1).
 * Vive solo en memoria durante el flujo de escaneo y revisión.
 */
class BorradorReciboStore {
    private val _borrador = MutableStateFlow<ReciboBorrador?>(null)
    val borrador: StateFlow<ReciboBorrador?> = _borrador.asStateFlow()

    fun guardar(nuevo: ReciboBorrador) {
        _borrador.value = nuevo
    }

    fun limpiar() {
        _borrador.value = null
    }

    fun actualizar(transform: (ReciboBorrador) -> ReciboBorrador) {
        _borrador.value?.let { actual ->
            _borrador.value = transform(actual)
        }
    }
}
