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
    periodoConsumo = Campo(periodoConsumo, confianza = 1f, corregidoPorUsuario = false),
    consumoM3 = Campo(consumoM3, confianza = 1f, corregidoPorUsuario = false),
    importeTotal = Campo(importeTotal, confianza = 1f, corregidoPorUsuario = false),
    numeroMedidor = Campo(numeroMedidor, confianza = 1f, corregidoPorUsuario = false),
    numeroRecibo = Campo(numeroRecibo, confianza = 1f, corregidoPorUsuario = false),
    lecturaAnteriorM3 = Campo(lecturaAnteriorM3, confianza = 1f, corregidoPorUsuario = false),
    lecturaActualM3 = Campo(lecturaActualM3, confianza = 1f, corregidoPorUsuario = false),
    fechaEmision = Campo(fechaEmision, confianza = 1f, corregidoPorUsuario = false),
    fechaVencimiento = Campo(fechaVencimiento, confianza = 1f, corregidoPorUsuario = false),
    tipoConsumo = Campo(tipoConsumo, confianza = 1f, corregidoPorUsuario = false),
    origen = origen
)

