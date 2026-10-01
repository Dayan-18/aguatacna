// Enums y data classes de soporte del módulo Recibo: no tienen invariantes propias que
// justifiquen un archivo aislado, pero acompañan siempre a Recibo/ReciboBorrador.
package pe.edu.upt.aguatacna.feature.recibo.domain.model

// ── Campo ──────────────────────────────────────────────────────────────────────

// Dato detectado por OCR con su nivel de confianza (0.0 a 1.0), usado en ReciboBorrador.
data class Campo<T>(
    val valor: T? = null,
    val confianza: Float = 0f,
    val corregidoPorUsuario: Boolean = false
) {
    /** true si el valor tiene confianza baja y debería resaltarse en la UI. */
    val esDudoso: Boolean get() = valor != null && confianza < 0.7f && !corregidoPorUsuario

    fun corregir(nuevo: T): Campo<T> = Campo(nuevo, confianza = 1f, corregidoPorUsuario = true)

    companion object {
        fun <T> confirmado(valor: T?): Campo<T> = Campo(valor, confianza = 1f)
    }
}

// ── OrigenDatos ───────────────────────────────────────────────────────────────

// Origen de los datos del recibo: por OCR de una foto o ingresados a mano.
enum class OrigenDatos { ESCANEADO, MANUAL }

// ── TipoConsumo ───────────────────────────────────────────────────────────────

// Tipo de consumo del campo "Tipo Consumo:" del recibo de EPS Tacna.
enum class TipoConsumo {
    LECTURA, // lectura real del medidor
    PROMEDIO, // facturado por promedio, no es lectura real
    DESCONOCIDO;

    companion object {
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

// ── EstadoConsumo ─────────────────────────────────────────────────────────────

// Estado del consumo de un mes: alto consumo si supera el límite de m³ (EvaluadorConsumo.LIMITE_M3).
enum class EstadoConsumo { ALTO_CONSUMO, NORMAL }

// ── TextoReconocido ───────────────────────────────────────────────────────────

// Línea de texto con su posición: el recibo es una tabla (clave a la izquierda, valor a la derecha)
// y la posición ayuda a emparejarlos.
data class LineaTexto(
    val texto: String,
    val x: Float = 0f,
    val y: Float = 0f,
    val ancho: Float = 0f,
    val alto: Float = 0f
)

// Resultado completo del OCR sobre una imagen del recibo.
data class TextoReconocido(
    val textoPlano: String,
    val lineas: List<LineaTexto> = emptyList()
) {
    val estaVacio: Boolean get() = textoPlano.isBlank() && lineas.isEmpty()
}
