package pe.edu.upt.aguatacna.feature.retos.data.sync

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pe.edu.upt.aguatacna.feature.retos.domain.model.Reporte
import pe.edu.upt.aguatacna.feature.retos.domain.model.TipoReporte

private const val TABLA_REPORTE = "reporte"

@Serializable
private data class ReporteNube(
    val id: String,
    @SerialName("usuario_id") val usuarioId: String,
    val tipo: String,
    val descripcion: String,
    val latitud: Double? = null,
    val longitud: Double? = null,
    @SerialName("tiene_foto") val tieneFoto: Boolean = false,
    @SerialName("pendiente_sincronizacion") val pendiente: Boolean = false
)

class NubeReportesSupabase(private val supabase: SupabaseClient) {
    suspend fun obtener(): List<Reporte> {
        supabase.auth.currentUserOrNull() ?: return emptyList()
        return supabase.from(TABLA_REPORTE).select().decodeList<ReporteNube>().map { it.aDominio() }
    }

    suspend fun guardar(reporte: Reporte) {
        val usuario = supabase.auth.currentUserOrNull()?.id ?: return
        supabase.from(TABLA_REPORTE).upsert(
            ReporteNube(
                reporte.id, usuario, reporte.tipo.name, reporte.descripcion,
                reporte.latitud, reporte.longitud,
                reporte.fotoBytes != null || reporte.fotoUri != null, false
            )
        )
    }
}

private fun ReporteNube.aDominio() = Reporte(
    id = id,
    tipo = TipoReporte.valueOf(tipo),
    descripcion = descripcion,
    latitud = latitud,
    longitud = longitud,
    fotoUri = if (tieneFoto) "foto-adjunta" else null,
    pendienteSincronizacion = true
)
