package pe.edu.upt.aguatacna.feature.recibo.domain.model

/**
 * Origen de los datos del recibo.
 */
enum class OrigenDatos {
    /** Datos obtenidos por escaneo OCR de una foto. */
    ESCANEADO,

    /** Datos ingresados manualmente por el usuario. */
    MANUAL
}
