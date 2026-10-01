package pe.edu.upt.aguatacna.feature.retos.data.sync

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pe.edu.upt.aguatacna.feature.retos.domain.model.Reto
import pe.edu.upt.aguatacna.feature.retos.domain.model.RetoUsuario

private const val TABLA_RETO = "reto_catalogo"
private const val TABLA_CUMPLIMIENTO = "reto_usuario"

@Serializable
private data class RetoNube(
    val id: String,
    val titulo: String,
    val descripcion: String,
    @SerialName("litros_meta") val litrosMeta: Int,
    val activo: Boolean
)

@Serializable
private data class CumplimientoNube(
    @SerialName("usuario_id") val usuarioId: String,
    @SerialName("reto_id") val retoId: String,
    val fecha: String,
    val cumplido: Boolean
)

class NubeRetosSupabase(private val supabase: SupabaseClient) {
    suspend fun obtenerRetos(): List<Reto> =
        supabase.from(TABLA_RETO).select().decodeList<RetoNube>()
            .filter { it.activo }.map { Reto(it.id, it.titulo, it.descripcion, it.litrosMeta) }

    suspend fun obtenerCumplimientos(): List<RetoUsuario> {
        supabase.auth.currentUserOrNull() ?: return emptyList()
        return supabase.from(TABLA_CUMPLIMIENTO).select().decodeList<CumplimientoNube>()
            .map { RetoUsuario(it.retoId, LocalDate.parse(it.fecha), it.cumplido) }
    }

    suspend fun guardar(cumplimiento: RetoUsuario) {
        val usuario = supabase.auth.currentUserOrNull()?.id ?: return
        supabase.from(TABLA_CUMPLIMIENTO).upsert(
            CumplimientoNube(usuario, cumplimiento.retoId, cumplimiento.fecha.toString(), cumplimiento.cumplido)
        )
    }
}
