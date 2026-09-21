package pe.edu.upt.aguatacna.feature.recibo.domain.model

/**
 * Importe en **céntimos** (Long). Nunca se usa `Double` para dinero.
 *
 * Ejemplo: S/ 74,20 → `Dinero(7420L)`
 */
data class Dinero(val centimos: Long) : Comparable<Dinero> {

    /** Parte entera (soles). */
    val soles: Long get() = centimos / 100

    /** Parte decimal (céntimos, siempre 0..99). */
    val centavos: Int get() = (centimos % 100).toInt()

    override fun compareTo(other: Dinero): Int =
        centimos.compareTo(other.centimos)

    operator fun plus(other: Dinero): Dinero = Dinero(centimos + other.centimos)
    operator fun minus(other: Dinero): Dinero = Dinero(centimos - other.centimos)

    /**
     * Formato peruano: "S/ 74,20".
     * Separador de miles: espacio. Separador decimal: coma.
     */
    fun formatear(): String {
        val signo = if (centimos < 0) "-" else ""
        val abs = kotlin.math.abs(centimos)
        val parteEntera = abs / 100
        val parteDecimal = (abs % 100).toString().padStart(2, '0')

        val enteraFormateada = formatearEnteroConMiles(parteEntera)
        return "${signo}S/ $enteraFormateada,$parteDecimal"
    }

    /**
     * Solo el número sin "S/": "74,20"
     */
    fun formatearSoloNumero(): String {
        val signo = if (centimos < 0) "-" else ""
        val abs = kotlin.math.abs(centimos)
        val parteEntera = abs / 100
        val parteDecimal = (abs % 100).toString().padStart(2, '0')

        val enteraFormateada = formatearEnteroConMiles(parteEntera)
        return "$signo$enteraFormateada,$parteDecimal"
    }

    companion object {
        val CERO = Dinero(0L)

        /** Crea Dinero desde soles con decimales (ej: 74.20 → 7420). */
        fun desdeSoles(soles: Double): Dinero =
            Dinero(kotlin.math.round(soles * 100).toLong())

        /** Crea Dinero desde string del recibo (ej: "78.00" → 7800). */
        fun parsear(texto: String): Dinero? {
            val limpio = texto.trim()
                .replace(",", ".")
                .replace(" ", "")
                .replace("*", "")
            val numero = limpio.toDoubleOrNull() ?: return null
            return desdeSoles(numero)
        }

        private fun formatearEnteroConMiles(valor: Long): String {
            val str = valor.toString()
            if (str.length <= 3) return str
            val sb = StringBuilder()
            var count = 0
            for (i in str.length - 1 downTo 0) {
                if (count > 0 && count % 3 == 0) sb.insert(0, ' ')
                sb.insert(0, str[i])
                count++
            }
            return sb.toString()
        }
    }
}
