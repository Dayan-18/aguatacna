package pe.edu.upt.aguatacna.feature.recibo.domain.model

// Importe en céntimos (Long); nunca se usa Double para dinero. Ejemplo: S/ 74,20 -> Dinero(7420L)
data class Dinero(val centimos: Long) : Comparable<Dinero> {

    val soles: Long get() = centimos / 100
    val centavos: Int get() = (centimos % 100).toInt()

    override fun compareTo(other: Dinero): Int = centimos.compareTo(other.centimos)

    operator fun plus(other: Dinero): Dinero = Dinero(centimos + other.centimos)
    operator fun minus(other: Dinero): Dinero = Dinero(centimos - other.centimos)

    // Formato peruano sin el prefijo "S/": "74,20" (miles con espacio, decimales con coma)
    fun formatearSoloNumero(): String {
        val signo = if (centimos < 0) "-" else ""
        val abs = kotlin.math.abs(centimos)
        val parteEntera = formatearEnteroConMiles(abs / 100)
        val parteDecimal = (abs % 100).toString().padStart(2, '0')
        return "$signo$parteEntera,$parteDecimal"
    }

    // Formato peruano completo: "S/ 74,20"
    fun formatear(): String = "S/ ${formatearSoloNumero()}"

    companion object {
        val CERO = Dinero(0L)

        // Crea Dinero desde soles con decimales (ej: 74.20 -> 7420)
        fun desdeSoles(soles: Double): Dinero =
            Dinero(kotlin.math.round(soles * 100).toLong())

        // Crea Dinero desde string del recibo (ej: "78.00" -> 7800)
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
