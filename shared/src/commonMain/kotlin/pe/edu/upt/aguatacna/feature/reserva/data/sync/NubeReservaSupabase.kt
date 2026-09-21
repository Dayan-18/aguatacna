package pe.edu.upt.aguatacna.feature.reserva.data.sync

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pe.edu.upt.aguatacna.feature.reserva.data.local.EventoLlenadoEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.NovedadReservaEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.PerfilHogarEntity

private const val TABLA_PERFIL = "perfil_hogar"
private const val TABLA_LLENADOS = "evento_llenado"
private const val TABLA_NOVEDADES = "novedad_reserva"

@Serializable
internal data class PerfilNube(
    @SerialName("usuario_id") val usuarioId: String,
    @SerialName("tipo_reservorio") val tipoReservorio: String,
    @SerialName("capacidad_litros") val capacidadLitros: Double,
    val habitantes: Int,
    @SerialName("duchas_por_dia") val duchasPorDia: Int,
    @SerialName("usa_lavadora") val usaLavadora: Boolean,
    @SerialName("riega_jardin") val riegaJardin: Boolean,
    @SerialName("consumo_por_habitos_litros_hora") val consumoPorHabitosLitrosHora: Double? = null,
    @SerialName("consumo_vigente_litros_hora") val consumoVigenteLitrosHora: Double? = null
)

@Serializable
internal data class LlenadoNube(
    val id: String,
    @SerialName("usuario_id") val usuarioId: String,
    val momento: String,
    val tipo: String
)

@Serializable
internal data class NovedadNube(
    val id: String,
    @SerialName("usuario_id") val usuarioId: String,
    val momento: String,
    val tipo: String,
    @SerialName("inicio_observado") val inicioObservado: String? = null,
    @SerialName("litros_observados") val litrosObservados: Double? = null
)

/** Postgres devuelve los instantes con segundos ("…T05:15:00"); en Room se guardan como "…T05:15". */
internal fun normalizarMomento(texto: String): String = LocalDateTime.parse(texto).toString()

/** Lee y escribe en las tablas de Supabase; la seguridad por fila limita cada consulta al usuario con sesión. */
class NubeReservaSupabase(private val supabase: SupabaseClient) : NubeReserva {

    override suspend fun descargar(): DatosDeReserva? {
        supabase.auth.currentUserOrNull() ?: return null
        val perfil = supabase.from(TABLA_PERFIL).select().decodeList<PerfilNube>().firstOrNull()
        val llenados = supabase.from(TABLA_LLENADOS).select().decodeList<LlenadoNube>()
        val novedades = supabase.from(TABLA_NOVEDADES).select().decodeList<NovedadNube>()
        return DatosDeReserva(
            perfil = perfil?.aLocal(),
            llenados = llenados.map { EventoLlenadoEntity(it.id, it.usuarioId, normalizarMomento(it.momento), it.tipo) },
            novedades = novedades.map {
                NovedadReservaEntity(
                    it.id, it.usuarioId, normalizarMomento(it.momento), it.tipo,
                    it.inicioObservado?.let(::normalizarMomento), it.litrosObservados
                )
            }
        )
    }

    override suspend fun subir(
        perfil: PerfilHogarEntity?,
        llenados: List<EventoLlenadoEntity>,
        novedades: List<NovedadReservaEntity>
    ) {
        val cuenta = supabase.auth.currentUserOrNull()?.id ?: return
        perfil?.let { supabase.from(TABLA_PERFIL).upsert(it.aNube(cuenta)) }
        if (llenados.isNotEmpty()) {
            supabase.from(TABLA_LLENADOS).upsert(llenados.map { LlenadoNube(it.id, cuenta, it.momento, it.tipo) })
        }
        if (novedades.isNotEmpty()) {
            supabase.from(TABLA_NOVEDADES).upsert(
                novedades.map { NovedadNube(it.id, cuenta, it.momento, it.tipo, it.inicioObservado, it.litrosObservados) }
            )
        }
    }
}

private fun PerfilNube.aLocal() = PerfilHogarEntity(
    usuarioId, tipoReservorio, capacidadLitros, habitantes, duchasPorDia,
    usaLavadora, riegaJardin, consumoPorHabitosLitrosHora, consumoVigenteLitrosHora
)

private fun PerfilHogarEntity.aNube(cuenta: String) = PerfilNube(
    cuenta, tipoReservorio, capacidadLitros, habitantes, duchasPorDia,
    usaLavadora, riegaJardin, consumoPorHabitosLitrosHora, consumoVigenteLitrosHora
)
