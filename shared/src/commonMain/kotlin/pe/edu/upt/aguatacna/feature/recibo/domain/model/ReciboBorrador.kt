package pe.edu.upt.aguatacna.feature.recibo.domain.model

import kotlinx.datetime.LocalDate

// Datos del recibo aún sin confirmar; cada campo lleva valor + confianza (Campo). Se confirma como Recibo.
data class ReciboBorrador(
    val periodoConsumo: Campo<PeriodoConsumo> = Campo(),
    val consumoM3: Campo<Int> = Campo(),
    val importeTotal: Campo<Dinero> = Campo(),
    val fechaEmision: Campo<LocalDate> = Campo(),
    val fechaVencimiento: Campo<LocalDate> = Campo(),
    val tipoConsumo: Campo<TipoConsumo> = Campo(),
    val lecturaAnteriorM3: Campo<Int> = Campo(),
    val lecturaActualM3: Campo<Int> = Campo(),
    val numeroMedidor: Campo<String> = Campo(),
    val numeroRecibo: Campo<String> = Campo(),
    val origen: OrigenDatos = OrigenDatos.ESCANEADO
) {
    /** true si tiene los campos mínimos para poder confirmar (consumo + importe). */
    val esConfirmable: Boolean
        get() = consumoM3.valor != null && importeTotal.valor != null

    /** true si algún campo crítico (consumo, importe o período) tiene baja confianza. */
    val tieneCamposDudosos: Boolean
        get() = consumoM3.esDudoso || importeTotal.esDudoso || periodoConsumo.esDudoso

    /** Convierte el borrador a un [Recibo] confirmado. Requiere [esConfirmable]. */
    fun confirmar(id: String): Recibo {
        require(esConfirmable) { "El borrador no tiene los campos mínimos (consumo + importe)." }
        val periodo = periodoConsumo.valor ?: PeriodoConsumo(2026, 8)
        val vencimiento = fechaVencimiento.valor ?: if (periodo.mes < 12) {
            LocalDate(periodo.anio, periodo.mes + 1, 11)
        } else {
            LocalDate(periodo.anio + 1, 1, 11)
        }
        val emision = fechaEmision.valor ?: LocalDate(periodo.anio, periodo.mes, 28)

        return Recibo(
            id = id,
            periodoConsumo = periodo,
            consumoM3 = consumoM3.valor!!,
            importeTotal = importeTotal.valor!!,
            fechaEmision = emision,
            fechaVencimiento = vencimiento,
            tipoConsumo = tipoConsumo.valor ?: TipoConsumo.DESCONOCIDO,
            lecturaAnteriorM3 = lecturaAnteriorM3.valor,
            lecturaActualM3 = lecturaActualM3.valor,
            numeroMedidor = numeroMedidor.valor ?: "0412887",
            numeroRecibo = numeroRecibo.valor,
            origen = origen
        )
    }
}
