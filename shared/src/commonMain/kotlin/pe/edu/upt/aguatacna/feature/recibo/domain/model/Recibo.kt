package pe.edu.upt.aguatacna.feature.recibo.domain.model

import kotlinx.datetime.LocalDate

// Recibo confirmado, identificado por su periodoConsumo (el mes de consumo, no el de facturación).
// Si se vuelve a escanear el mismo período, se reemplaza (upsert).
data class Recibo(
    val id: String,
    val periodoConsumo: PeriodoConsumo,
    val consumoM3: Int,
    val importeTotal: Dinero,
    val fechaEmision: LocalDate? = null,
    val fechaVencimiento: LocalDate? = null,
    val tipoConsumo: TipoConsumo = TipoConsumo.DESCONOCIDO,
    val lecturaAnteriorM3: Int? = null,
    val lecturaActualM3: Int? = null,
    val numeroMedidor: String? = null,
    val numeroRecibo: String? = null,
    val origen: OrigenDatos = OrigenDatos.ESCANEADO
)

// Convierte un Recibo confirmado en un ReciboBorrador para permitir su edición o revisión.
fun Recibo.aBorrador(): ReciboBorrador = ReciboBorrador(
    periodoConsumo = Campo.confirmado(periodoConsumo),
    consumoM3 = Campo.confirmado(consumoM3),
    importeTotal = Campo.confirmado(importeTotal),
    numeroMedidor = Campo.confirmado(numeroMedidor),
    numeroRecibo = Campo.confirmado(numeroRecibo),
    lecturaAnteriorM3 = Campo.confirmado(lecturaAnteriorM3),
    lecturaActualM3 = Campo.confirmado(lecturaActualM3),
    fechaEmision = Campo.confirmado(fechaEmision),
    fechaVencimiento = Campo.confirmado(fechaVencimiento),
    tipoConsumo = Campo.confirmado(tipoConsumo),
    origen = origen,
    idRecibo = id
)
