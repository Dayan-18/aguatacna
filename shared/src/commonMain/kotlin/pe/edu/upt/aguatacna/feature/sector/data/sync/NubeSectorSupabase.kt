package pe.edu.upt.aguatacna.feature.sector.data.sync

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pe.edu.upt.aguatacna.feature.sector.data.local.ConfirmacionHorarioEntity
import pe.edu.upt.aguatacna.feature.sector.data.local.CronogramaEntity
import pe.edu.upt.aguatacna.feature.sector.data.local.PuntoCisternaEntity
import pe.edu.upt.aguatacna.feature.sector.data.local.SectorEntity

private const val TABLA_SECTOR = "sector"
private const val TABLA_CRONOGRAMA = "cronograma"
private const val TABLA_PUNTO = "punto_cisterna"
private const val TABLA_CONFIRMACION = "confirmacion_horario"
private const val VISTA_CONFIRMACION = "confirmacion_publica"

@Serializable
internal data class SectorNube(
    val id: String,
    val nombre: String,
    val distrito: String,
    val latitud: Double,
    val longitud: Double
)

@Serializable
internal data class CronogramaNube(
    val id: String,
    @SerialName("sector_id") val sectorId: String,
    val fecha: String,
    @SerialName("hora_inicio") val horaInicio: String,
    @SerialName("hora_fin") val horaFin: String,
    val tipo: String,
    val fuente: String
)

@Serializable
internal data class PuntoNube(
    val id: String,
    @SerialName("sector_id") val sectorId: String,
    val nombre: String,
    val latitud: Double,
    val longitud: Double,
    @SerialName("horario_inicio") val horarioInicio: String,
    @SerialName("horario_fin") val horarioFin: String,
    val estado: String
)

// Para insertar: usuario_id lo pone la BD por defecto (auth.uid()); no se envía desde aquí.
@Serializable
internal data class ConfirmacionInsertNube(
    val id: String,
    @SerialName("sector_id") val sectorId: String,
    val momento: String,
    val tipo: String
)

// Desde la vista anónima: sin id ni usuario_id, solo hora y tipo (constitución, art. IX).
@Serializable
internal data class ConfirmacionPublicaNube(
    @SerialName("sector_id") val sectorId: String,
    val momento: String,
    val tipo: String
)

// Postgres devuelve las horas con segundos ("05:00:00") y los instantes hasta el segundo;
// se recortan al formato que guarda Room ("05:00" y "yyyy-MM-ddTHH:mm").
internal fun horaCorta(texto: String): String = texto.take(5)
internal fun momentoCorto(texto: String): String = texto.take(16)

/** Lee la sectorización pública y sube/lee confirmaciones. La vista anónima nunca expone quién confirmó. */
class NubeSectorSupabase(private val supabase: SupabaseClient) {

    suspend fun descargarSectores(): List<SectorEntity> =
        supabase.from(TABLA_SECTOR).select().decodeList<SectorNube>()
            .map { SectorEntity(it.id, it.nombre, it.distrito, it.latitud, it.longitud) }

    suspend fun descargarCronogramas(): List<CronogramaEntity> =
        supabase.from(TABLA_CRONOGRAMA).select().decodeList<CronogramaNube>()
            .map { CronogramaEntity(it.id, it.sectorId, it.fecha, horaCorta(it.horaInicio), horaCorta(it.horaFin), it.tipo, it.fuente) }

    suspend fun descargarPuntos(): List<PuntoCisternaEntity> =
        supabase.from(TABLA_PUNTO).select().decodeList<PuntoNube>()
            .map { PuntoCisternaEntity(it.id, it.sectorId, it.nombre, it.latitud, it.longitud, horaCorta(it.horarioInicio), horaCorta(it.horarioFin), it.estado) }

    /** Confirmaciones del vecindario desde la vista anónima; el id es sintético y la identidad no viaja. */
    suspend fun descargarConfirmaciones(): List<ConfirmacionHorarioEntity> =
        supabase.from(VISTA_CONFIRMACION).select().decodeList<ConfirmacionPublicaNube>()
            .mapIndexed { i, c -> ConfirmacionHorarioEntity("${c.sectorId}-$i", c.sectorId, "anon", momentoCorto(c.momento), c.tipo) }

    /** Sube la confirmación del usuario con sesión; la política de la tabla la liga a su cuenta. */
    suspend fun subirConfirmacion(confirmacion: ConfirmacionHorarioEntity) {
        supabase.auth.currentUserOrNull() ?: return
        supabase.from(TABLA_CONFIRMACION).upsert(
            ConfirmacionInsertNube(confirmacion.id, confirmacion.sectorId, confirmacion.momento, confirmacion.tipo)
        )
    }
}
