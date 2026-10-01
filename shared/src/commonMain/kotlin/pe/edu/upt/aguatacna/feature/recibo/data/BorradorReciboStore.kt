package pe.edu.upt.aguatacna.feature.recibo.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upt.aguatacna.feature.recibo.data.local.ReciboBorradorDao
import pe.edu.upt.aguatacna.feature.recibo.data.local.aBorrador
import pe.edu.upt.aguatacna.feature.recibo.data.local.aEntidad
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador

// Borrador de recibo mientras dura la captura/revisión. Con DAO se copia en Room para que sobreviva
// si el sistema cierra la app; sin DAO (pruebas y vistas previas) vive solo en memoria.
class BorradorReciboStore(
    private val dao: ReciboBorradorDao? = null,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val _borrador = MutableStateFlow<ReciboBorrador?>(null)
    val borrador: StateFlow<ReciboBorrador?> = _borrador.asStateFlow()

    // Lo que el usuario escriba mientras se lee Room gana sobre lo guardado (compareAndSet).
    private val restauracion = scope.launch {
        dao?.obtener()?.aBorrador()?.let { _borrador.compareAndSet(null, it) }
    }

    init {
        if (dao != null) {
            scope.launch {
                restauracion.join()
                _borrador.collect { actual -> if (actual == null) dao.borrar() else dao.guardar(actual.aEntidad()) }
            }
        }
    }

    /** Si no hay borrador (ni uno guardado por restaurar), deja el que devuelva [crear]. */
    suspend fun asegurar(crear: () -> ReciboBorrador) {
        restauracion.join()
        _borrador.compareAndSet(null, crear())
    }

    fun guardar(nuevo: ReciboBorrador) {
        _borrador.value = nuevo
    }

    fun limpiar() {
        _borrador.value = null
    }
}
