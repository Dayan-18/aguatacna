package pe.edu.upt.aguatacna.feature.recibo.data.sync

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import pe.edu.upt.aguatacna.feature.recibo.data.local.ReciboDao
import pe.edu.upt.aguatacna.feature.recibo.data.local.ReciboEntity

interface NubeRecibo {
    /** Los recibos guardados en la nube, o `null` si no hay una sesión abierta. */
    suspend fun descargar(): List<ReciboEntity>?

    suspend fun subir(recibos: List<ReciboEntity>)
}

/**
 * Mantiene la nube como copia de Room, que sigue siendo la fuente de verdad (constitución, artículo II).
 * Las reglas son las de la reserva:
 * - Un recibo de la nube se baja solo si en el teléfono no existe ni su `id` ni su período.
 * - Todos los recibos del teléfono se suben con upsert: en un conflicto gana el teléfono.
 * - Los borrados no se propagan: esta primera versión solo suma.
 */
class SincronizadorRecibo(
    private val dao: ReciboDao,
    private val nube: NubeRecibo,
    private val haySesion: Flow<Boolean>
) {
    /** Devuelve `true` si sincronizó; `false` si no había sesión o la red falló (se reintenta con el próximo cambio). */
    suspend fun sincronizar(): Boolean = try {
        val remotos = nube.descargar()
        if (remotos == null) false else { igualar(remotos); true }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false
    }

    private suspend fun igualar(remotos: List<ReciboEntity>) {
        val locales = dao.observarTodos().first()
        val ids = locales.map { it.id }.toSet()
        val periodos = locales.map { it.anio to it.mes }.toSet()
        remotos
            .filter { it.id !in ids && (it.anio to it.mes) !in periodos }
            .forEach { dao.guardar(it) }
        if (locales.isNotEmpty()) nube.subir(locales)
    }

    /** Sincroniza al abrir, al iniciar sesión y unos segundos después de cada cambio de recibos. */
    @OptIn(FlowPreview::class)
    suspend fun mantenerSincronizado() {
        combine(haySesion, dao.observarTodos()) { conSesion, _ -> conSesion }
            .debounce(RETARDO_MS)
            .collect { conSesion -> if (conSesion) sincronizar() }
    }

    private companion object {
        const val RETARDO_MS = 2_000L
    }
}
