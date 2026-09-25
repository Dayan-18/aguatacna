package pe.edu.upt.aguatacna.feature.recibo.domain.model

// Período de consumo (año + mes de consumo, no el de facturación), ej. entrada "AGOSTO-2026".
data class PeriodoConsumo(
    val anio: Int,
    val mes: Int // 1..12
) : Comparable<PeriodoConsumo> {

    init {
        require(mes in 1..12) { "Mes fuera de rango: $mes" }
        require(anio in 2000..2100) { "Año fuera de rango: $anio" }
    }

    /** Nombre corto del mes en español (3 letras). "Set" para setiembre. */
    val mesCorto: String get() = MESES_CORTOS[mes - 1]

    /** Nombre largo del mes en español, con la primera en mayúscula. */
    val mesLargo: String get() = MESES_LARGOS[mes - 1]

    /** Display: "Agosto 2026" */
    val displayCompleto: String get() = "$mesLargo $anio"

    override fun compareTo(other: PeriodoConsumo): Int {
        val cmpAnio = anio.compareTo(other.anio)
        return if (cmpAnio != 0) cmpAnio else mes.compareTo(other.mes)
    }

    /** Devuelve el período del mes anterior. */
    fun anterior(): PeriodoConsumo =
        if (mes == 1) PeriodoConsumo(anio - 1, 12)
        else PeriodoConsumo(anio, mes - 1)

    /** Devuelve el período del mes siguiente. */
    fun siguiente(): PeriodoConsumo =
        if (mes == 12) PeriodoConsumo(anio + 1, 1)
        else PeriodoConsumo(anio, mes + 1)

    companion object {
        private val MESES_LARGOS = listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Setiembre", "Octubre", "Noviembre", "Diciembre"
        )

        private val MESES_CORTOS = listOf(
            "Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Set", "Oct", "Nov", "Dic"
        )

        // Mapeo de nombres en español (mayúsculas, sin tildes) a número de mes
        private val NOMBRE_A_MES: Map<String, Int> = buildMap {
            val nombres = listOf(
                listOf("ENERO"),
                listOf("FEBRERO"),
                listOf("MARZO"),
                listOf("ABRIL"),
                listOf("MAYO"),
                listOf("JUNIO"),
                listOf("JULIO"),
                listOf("AGOSTO"),
                listOf("SETIEMBRE", "SEPTIEMBRE"),  // ambas variantes
                listOf("OCTUBRE"),
                listOf("NOVIEMBRE"),
                listOf("DICIEMBRE")
            )
            nombres.forEachIndexed { index, variantes ->
                variantes.forEach { put(it, index + 1) }
            }
        }

        // Parsea "MES-AÑO" (acepta guion, espacio o slash); devuelve null si no se puede parsear
        fun parsear(texto: String): PeriodoConsumo? {
            val limpio = texto.trim().uppercase()
                .replace("Á", "A").replace("É", "E").replace("Í", "I")
                .replace("Ó", "O").replace("Ú", "U")

            // Intentar "MES-AÑO", "MES AÑO", "MES/AÑO"
            val partes = limpio.split('-', ' ', '/')
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            if (partes.size != 2) return null

            val mesTexto = partes[0]
            val anioTexto = partes[1]

            val mes = NOMBRE_A_MES[mesTexto] ?: return null
            val anio = anioTexto.toIntOrNull() ?: return null

            return try {
                PeriodoConsumo(anio, mes)
            } catch (_: IllegalArgumentException) {
                null
            }
        }
    }
}
