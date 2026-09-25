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

// Estado de consumo evaluado según la regla de Sunass; cada estado trae su texto para la UI.
sealed class EstadoConsumo {
    abstract val chipTexto: String
    abstract val mensaje: String
    abstract val variacionTexto: String

    // Alto consumo: supera los 100 m³ (se muestra en naranja/Ocre)
    data class Atipico(
        val excesoPorcentaje: Int,
        val promedioHistorico: Int,
        val mes: String
    ) : EstadoConsumo() {
        override val chipTexto: String = "Alto consumo"
        override val mensaje: String =
            "$mes supera los 100 m³. Se recomienda verificar posibles fugas o solicitar una revisión de medidor."
        override val variacionTexto: String = "+$excesoPorcentaje %"
    }

    // Consumo normal: dentro del límite de 100 m³ (se muestra en teal/AguaMedia)
    data class Normal(
        val excesoPorcentaje: Int,
        val promedioHistorico: Int
    ) : EstadoConsumo() {
        override val chipTexto: String = "Normal"
        override val mensaje: String =
            "Consumo dentro de los límites normales (≤ 100 m³). Todo en orden."
        override val variacionTexto: String = if (excesoPorcentaje >= 0) "+$excesoPorcentaje %" else "$excesoPorcentaje %"
    }

    // Registro de recibo sin historial suficiente para comparar
    data class SinHistorial(
        val mesesDisponibles: Int = 0
    ) : EstadoConsumo() {
        override val chipTexto: String = "Registro"
        override val mensaje: String =
            "Registro de consumo guardado correctamente."
        override val variacionTexto: String = "—"
    }

    // Facturado por promedio: no es una lectura real del medidor
    data object FacturadoPorPromedio : EstadoConsumo() {
        override val chipTexto: String = "Por promedio"
        override val mensaje: String =
            "EPS facturó este mes por promedio; no es una lectura real."
        override val variacionTexto: String = "—"
    }
}

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
