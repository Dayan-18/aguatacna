package pe.edu.upt.aguatacna.feature.recibo.data.sync

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pe.edu.upt.aguatacna.feature.recibo.data.local.ReciboEntity

private const val TABLA_RECIBO = "recibo"

@Serializable
internal data class ReciboNube(
    @SerialName("usuario_id") val usuarioId: String,
    val id: String,
    val anio: Int,
    val mes: Int,
    @SerialName("consumo_m3") val consumoM3: Int,
    @SerialName("importe_centimos") val importeCentimos: Long,
    @SerialName("fecha_emision") val fechaEmision: String? = null,
    @SerialName("fecha_vencimiento") val fechaVencimiento: String? = null,
    @SerialName("tipo_consumo") val tipoConsumo: String,
    @SerialName("lectura_anterior_m3") val lecturaAnteriorM3: Int? = null,
    @SerialName("lectura_actual_m3") val lecturaActualM3: Int? = null,
    @SerialName("numero_medidor") val numeroMedidor: String? = null,
    @SerialName("numero_recibo") val numeroRecibo: String? = null,
    val origen: String
)

/** Lee y escribe la tabla `recibo` de Supabase; la seguridad por fila limita cada consulta al usuario con sesión. */
class NubeReciboSupabase(private val supabase: SupabaseClient) : NubeRecibo {

    override suspend fun descargar(): List<ReciboEntity>? {
        supabase.auth.currentUserOrNull() ?: return null
        return supabase.from(TABLA_RECIBO).select().decodeList<ReciboNube>().map { it.aLocal() }
    }

    override suspend fun subir(recibos: List<ReciboEntity>) {
        val cuenta = supabase.auth.currentUserOrNull()?.id ?: return
        supabase.from(TABLA_RECIBO).upsert(recibos.map { it.aNube(cuenta) })
    }
}

private fun ReciboNube.aLocal() = ReciboEntity(
    id, anio, mes, consumoM3, importeCentimos, fechaEmision, fechaVencimiento,
    tipoConsumo, lecturaAnteriorM3, lecturaActualM3, numeroMedidor, numeroRecibo, origen
)

private fun ReciboEntity.aNube(cuenta: String) = ReciboNube(
    cuenta, id, anio, mes, consumoM3, importeCentimos, fechaEmision, fechaVencimiento,
    tipoConsumo, lecturaAnteriorM3, lecturaActualM3, numeroMedidor, numeroRecibo, origen
)
