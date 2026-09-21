package pe.edu.upt.aguatacna.feature.reserva.data.sync

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import pe.edu.upt.aguatacna.feature.reserva.data.local.EventoLlenadoEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.NovedadReservaEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.PerfilHogarEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.ReservaDao

/** Lo que la nube guarda de un usuario: su perfil y sus registros. */
data class DatosDeReserva(
    val perfil: PerfilHogarEntity?,
    val llenados: List<EventoLlenadoEntity>,
    val novedades: List<NovedadReservaEntity>
)

interface NubeReserva {
    /** Lo guardado en la nube, o `null` si no hay una sesión abierta. */
    suspend fun descargar(): DatosDeReserva?

    suspend fun subir(perfil: PerfilHogarEntity?, llenados: List<EventoLlenadoEntity>, novedades: List<NovedadReservaEntity>)
}

/**
 * Mantiene la nube como copia de Room, que sigue siendo la fuente de verdad (constitución, artículo III).
 * Cada usuario se guarda con su UUID local; la nube lo relaciona con su cuenta. Las reglas:
 * - Los registros se identifican por su `id`: los que faltan de un lado se copian al otro y nunca se duplican.
 * - El perfil del teléfono manda; el de la nube solo se usa en un teléfono que aún no tiene perfil.
 * - Los borrados no se propagan: esta primera versión solo suma.
 */
class SincronizadorReserva(
    private val dao: ReservaDao,
    private val usuarioId: String,
    private val nube: NubeReserva,
    private val haySesion: Flow<Boolean>
) {
    /** Devuelve `true` si sincronizó; `false` si no había sesión o la red falló (se reintenta con el próximo cambio). */
    suspend fun sincronizar(): Boolean = try {
        val remoto = nube.descargar()
        if (remoto == null) false else { igualar(remoto); true }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false
    }

    private suspend fun igualar(remoto: DatosDeReserva) {
        var perfil = dao.observarPerfil(usuarioId).first()
        if (perfil == null && remoto.perfil != null) {
            perfil = remoto.perfil.copy(usuarioId = usuarioId).also { dao.guardarPerfil(it) }
        }
        val llenados = dao.observarLlenados(usuarioId).first()
        val novedades = dao.observarNovedades(usuarioId).first()

        val idsDeLlenadosEnNube = remoto.llenados.map { it.id }.toSet()
        val idsDeNovedadesEnNube = remoto.novedades.map { it.id }.toSet()
        val llenadosLocales = llenados.map { it.id }.toSet()
        val novedadesLocales = novedades.map { it.id }.toSet()

        remoto.llenados.filter { it.id !in llenadosLocales }.forEach { dao.guardarLlenado(it.copy(usuarioId = usuarioId)) }
        remoto.novedades.filter { it.id !in novedadesLocales }.forEach { dao.guardarNovedad(it.copy(usuarioId = usuarioId)) }

        nube.subir(
            perfil = perfil,
            llenados = llenados.filter { it.id !in idsDeLlenadosEnNube },
            novedades = novedades.filter { it.id !in idsDeNovedadesEnNube }
        )
    }

    /** Sincroniza al abrir, al iniciar sesión y unos segundos después de cada cambio de datos. */
    @OptIn(FlowPreview::class)
    suspend fun mantenerSincronizado() {
        combine(
            haySesion,
            dao.observarPerfil(usuarioId),
            dao.observarLlenados(usuarioId),
            dao.observarNovedades(usuarioId)
        ) { conSesion, _, _, _ -> conSesion }
            .debounce(RETARDO_MS)
            .collect { conSesion -> if (conSesion) sincronizar() }
    }

    private companion object {
        const val RETARDO_MS = 2_000L
    }
}
