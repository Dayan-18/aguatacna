package pe.edu.upt.aguatacna.feature.recibo.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TipoConsumo

/**
 * Entidad de persistencia en Room para Recibo (T-11.1, T-11.2).
 * Únicamente almacena datos de consumo y facturación (sin DNI, nombres ni fotos).
 */
@Entity(tableName = "recibo")
data class ReciboEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(defaultValue = "2026") val anio: Int,
    @ColumnInfo(defaultValue = "1") val mes: Int,
    @ColumnInfo(defaultValue = "0") val consumoM3: Int,
    @ColumnInfo(defaultValue = "0") val importeCentimos: Long,
    val fechaEmision: String? = null,
    val fechaVencimiento: String? = null,
    @ColumnInfo(defaultValue = "LECTURA") val tipoConsumo: String = "LECTURA",
    val lecturaAnteriorM3: Int? = null,
    val lecturaActualM3: Int? = null,
    val numeroMedidor: String? = null,
    val numeroRecibo: String? = null,
    @ColumnInfo(defaultValue = "MANUAL") val origen: String = "MANUAL"
)

fun ReciboEntity.toDomain(): Recibo = Recibo(
    id = id,
    periodoConsumo = PeriodoConsumo(anio, mes),
    consumoM3 = consumoM3,
    importeTotal = Dinero(importeCentimos),
    fechaEmision = fechaEmision?.let { try { LocalDate.parse(it) } catch (e: Exception) { null } },
    fechaVencimiento = fechaVencimiento?.let { try { LocalDate.parse(it) } catch (e: Exception) { null } },
    tipoConsumo = try { TipoConsumo.valueOf(tipoConsumo) } catch (e: Exception) { TipoConsumo.DESCONOCIDO },
    lecturaAnteriorM3 = lecturaAnteriorM3,
    lecturaActualM3 = lecturaActualM3,
    numeroMedidor = numeroMedidor,
    numeroRecibo = numeroRecibo,
    origen = try { OrigenDatos.valueOf(origen) } catch (e: Exception) { OrigenDatos.MANUAL }
)

fun Recibo.toEntity(): ReciboEntity = ReciboEntity(
    id = id,
    anio = periodoConsumo.anio,
    mes = periodoConsumo.mes,
    consumoM3 = consumoM3,
    importeCentimos = importeTotal.centimos,
    fechaEmision = fechaEmision?.toString(),
    fechaVencimiento = fechaVencimiento?.toString(),
    tipoConsumo = tipoConsumo.name,
    lecturaAnteriorM3 = lecturaAnteriorM3,
    lecturaActualM3 = lecturaActualM3,
    numeroMedidor = numeroMedidor,
    numeroRecibo = numeroRecibo,
    origen = origen.name
)
