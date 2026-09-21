package pe.edu.upt.aguatacna.feature.recibo.domain.model

/**
 * Tipo de consumo según el recibo de EPS Tacna.
 *
 * El único valor conocido del recibo de ejemplo es PROMEDIO.
 * Los demás se descubrirán con más muestras (T-5.6).
 */
enum class TipoConsumo {
    /** Lectura real del medidor. */
    LECTURA,

    /** Facturado por promedio (no es lectura real). */
    PROMEDIO,

    /** No se pudo determinar el tipo. */
    DESCONOCIDO;

    companion object {
        /** Parsea el texto del campo "Tipo Consumo:" del recibo. */
        fun parsear(texto: String): TipoConsumo {
            val limpio = texto.trim().uppercase()
            return when {
                limpio.contains("PROMEDIO") -> PROMEDIO
                limpio.contains("LECTURA") || limpio.contains("REAL") -> LECTURA
                else -> DESCONOCIDO
            }
        }
    }
}
