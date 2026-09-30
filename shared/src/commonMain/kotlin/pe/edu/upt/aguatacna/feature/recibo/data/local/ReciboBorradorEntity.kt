package pe.edu.upt.aguatacna.feature.recibo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TipoConsumo

// Borrador en revisión, guardado como JSON en una sola fila: se reemplaza entero en cada cambio y
// así el esquema no tiene que seguir cada campo del borrador. Solo datos del recibo, sin datos personales.
@Entity(tableName = "recibo_borrador")
data class ReciboBorradorEntity(
    @PrimaryKey val id: Int = FILA_UNICA,
    val contenido: String
) {
    companion object {
        const val FILA_UNICA = 1
    }
}

@Serializable
internal data class CampoGuardado(val valor: String?, val confianza: Float, val corregido: Boolean)

@Serializable
internal data class BorradorGuardado(
    val periodo: CampoGuardado,
    val consumo: CampoGuardado,
    val importe: CampoGuardado,
    val emision: CampoGuardado,
    val vencimiento: CampoGuardado,
    val tipo: CampoGuardado,
    val lecturaAnterior: CampoGuardado,
    val lecturaActual: CampoGuardado,
    val medidor: CampoGuardado,
    val numeroRecibo: CampoGuardado,
    val origen: String,
    val idRecibo: String?
)

private val json = Json { ignoreUnknownKeys = true }

fun ReciboBorrador.aEntidad(): ReciboBorradorEntity = ReciboBorradorEntity(
    contenido = json.encodeToString(
        BorradorGuardado.serializer(),
        BorradorGuardado(
            periodo = periodoConsumo.guardar { "${it.anio}-${it.mes}" },
            consumo = consumoM3.guardar(),
            importe = importeTotal.guardar { it.centimos.toString() },
            emision = fechaEmision.guardar(),
            vencimiento = fechaVencimiento.guardar(),
            tipo = tipoConsumo.guardar { it.name },
            lecturaAnterior = lecturaAnteriorM3.guardar(),
            lecturaActual = lecturaActualM3.guardar(),
            medidor = numeroMedidor.guardar(),
            numeroRecibo = numeroRecibo.guardar(),
            origen = origen.name,
            idRecibo = idRecibo
        )
    )
)

/** null si el contenido guardado ya no se puede leer; en ese caso se empieza un borrador nuevo. */
fun ReciboBorradorEntity.aBorrador(): ReciboBorrador? {
    val g = runCatching { json.decodeFromString(BorradorGuardado.serializer(), contenido) }.getOrNull() ?: return null
    return ReciboBorrador(
        periodoConsumo = g.periodo.leer(::periodoDesde),
        consumoM3 = g.consumo.leer(String::toIntOrNull),
        importeTotal = g.importe.leer { it.toLongOrNull()?.let(::Dinero) },
        fechaEmision = g.emision.leer(::fechaDesde),
        fechaVencimiento = g.vencimiento.leer(::fechaDesde),
        tipoConsumo = g.tipo.leer { t -> TipoConsumo.entries.find { it.name == t } },
        lecturaAnteriorM3 = g.lecturaAnterior.leer(String::toIntOrNull),
        lecturaActualM3 = g.lecturaActual.leer(String::toIntOrNull),
        numeroMedidor = g.medidor.leer { it },
        numeroRecibo = g.numeroRecibo.leer { it },
        origen = OrigenDatos.entries.find { it.name == g.origen } ?: OrigenDatos.MANUAL,
        idRecibo = g.idRecibo
    )
}

private fun <T> Campo<T>.guardar(aTexto: (T) -> String = { it.toString() }) =
    CampoGuardado(valor?.let(aTexto), confianza, corregidoPorUsuario)

private fun <T> CampoGuardado.leer(desde: (String) -> T?): Campo<T> =
    Campo(valor?.let(desde), confianza, corregido)

private fun periodoDesde(texto: String): PeriodoConsumo? {
    val (anio, mes) = texto.split('-').mapNotNull { it.toIntOrNull() }.takeIf { it.size == 2 } ?: return null
    return runCatching { PeriodoConsumo(anio, mes) }.getOrNull()
}

private fun fechaDesde(texto: String): LocalDate? = runCatching { LocalDate.parse(texto) }.getOrNull()
