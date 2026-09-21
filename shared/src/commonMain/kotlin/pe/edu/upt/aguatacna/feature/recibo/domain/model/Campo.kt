package pe.edu.upt.aguatacna.feature.recibo.domain.model

/**
 * Dato detectado con nivel de confianza, usado en [ReciboBorrador].
 *
 * @param valor    El valor detectado (null si no se pudo leer).
 * @param confianza Nivel de confianza del OCR, de 0.0 (nulo) a 1.0 (seguro).
 * @param corregidoPorUsuario true si el usuario lo corrigió manualmente.
 */
data class Campo<T>(
    val valor: T? = null,
    val confianza: Float = 0f,
    val corregidoPorUsuario: Boolean = false
) {
    /** true si el valor tiene confianza baja y debería resaltarse en la UI. */
    val esDudoso: Boolean get() = valor != null && confianza < 0.7f && !corregidoPorUsuario
}
