package pe.edu.upt.aguatacna.feature.recibo.presentation.comun

/**
 * Utilidades de formateo de números en formato peruano (T-3.4).
 *
 * - Separador de miles: espacio
 * - Separador decimal: coma
 * - Ejemplo: 1 284, 74,20
 */
object FormateadorNumeros {

    /** Formatea entero con separador de miles (espacio). Ej: 1284 → "1 284" */
    fun formatearEntero(valor: Int): String {
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

    /** Formatea m³: "33 m³", "1 284 m³" */
    fun formatearM3(valor: Int): String = "${formatearEntero(valor)} m³"

    /** Formatea porcentaje con signo: "+106 %", "-5 %" */
    fun formatearPorcentaje(valor: Int): String =
        if (valor >= 0) "+$valor %" else "$valor %"
}
