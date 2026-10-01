package pe.edu.upt.aguatacna.feature.recibo.presentation.componentes

// Campos del recibo que el usuario puede corregir a mano.
// maxDigitos cuenta la coma del importe: 7 caracteres admiten "1234,56".
enum class TipoCampoEdicion(val nombre: String, val unidad: String, val maxDigitos: Int) {
    CONSUMO_M3("consumo", "m³", 3),
    LECTURA_ANTERIOR("lectura anterior", "m³", 6),
    LECTURA_ACTUAL("lectura actual", "m³", 6),
    IMPORTE("importe total", "S/", 7),
    PERIODO("período de consumo", "", 0);

    val permiteComa: Boolean get() = this == IMPORTE
}
