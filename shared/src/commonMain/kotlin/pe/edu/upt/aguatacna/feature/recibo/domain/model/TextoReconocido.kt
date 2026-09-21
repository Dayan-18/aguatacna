package pe.edu.upt.aguatacna.feature.recibo.domain.model

/**
 * Representa una línea de texto con su caja delimitadora (coordenadas relativas 0..1 o píxeles).
 * Requerido por T-4.1: el recibo es una tabla (clave a la izquierda, valor a la derecha)
 * y las posiciones ayudan a emparejarlos.
 */
data class LineaTexto(
    val texto: String,
    val x: Float = 0f,
    val y: Float = 0f,
    val ancho: Float = 0f,
    val alto: Float = 0f
)

/**
 * Resultado completo del OCR (T-4.1).
 */
data class TextoReconocido(
    val textoPlano: String,
    val lineas: List<LineaTexto> = emptyList()
) {
    val estaVacio: Boolean get() = textoPlano.isBlank() && lineas.isEmpty()
}
