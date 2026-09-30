package pe.edu.upt.aguatacna.feature.recibo.domain.model

import kotlinx.datetime.LocalDate

// Datos del recibo aún sin confirmar; cada campo lleva valor + confianza (Campo). Se confirma como Recibo.
// idRecibo es el id del recibo que se está editando, o null si es uno nuevo.
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
    val origen: OrigenDatos = OrigenDatos.ESCANEADO,
    val idRecibo: String? = null
) {
    /** true si tiene los campos mínimos para poder confirmar (período + consumo + importe). */
    val esConfirmable: Boolean
        get() = periodoConsumo.valor != null && consumoM3.valor != null && importeTotal.valor != null

    /** true si algún campo crítico (consumo, importe o período) tiene baja confianza. */
    val tieneCamposDudosos: Boolean
        get() = consumoM3.esDudoso || importeTotal.esDudoso || periodoConsumo.esDudoso

    /** Convierte el borrador a un [Recibo] confirmado. Requiere [esConfirmable]; lo que falte queda en null. */
    fun confirmar(id: String): Recibo {
        val periodo = periodoConsumo.valor
        val consumo = consumoM3.valor
        val importe = importeTotal.valor
        require(periodo != null && consumo != null && importe != null) {
            "El borrador no tiene los campos mínimos (período + consumo + importe)."
        }
        return Recibo(
            id = id,
            periodoConsumo = periodo,
            consumoM3 = consumo,
            importeTotal = importe,
            fechaEmision = fechaEmision.valor,
            fechaVencimiento = fechaVencimiento.valor,
            tipoConsumo = tipoConsumo.valor ?: TipoConsumo.DESCONOCIDO,
            lecturaAnteriorM3 = lecturaAnteriorM3.valor,
            lecturaActualM3 = lecturaActualM3.valor,
            numeroMedidor = numeroMedidor.valor,
            numeroRecibo = numeroRecibo.valor,
            origen = origen
        )
    }

    companion object {
        /** Borrador en blanco para ingresar un recibo a mano, con el período ya elegido. */
        fun vacio(periodo: PeriodoConsumo): ReciboBorrador =
            ReciboBorrador(periodoConsumo = Campo.confirmado(periodo), origen = OrigenDatos.MANUAL)
    }
}
