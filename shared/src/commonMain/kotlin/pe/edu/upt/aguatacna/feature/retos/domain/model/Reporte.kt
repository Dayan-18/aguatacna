package pe.edu.upt.aguatacna.feature.retos.domain.model

enum class TipoReporte { CORTE_NO_PROGRAMADO, FUGA, BAJA_PRESION, CISTERNA }

data class Reporte(
    val id: String,
    val tipo: TipoReporte,
    val descripcion: String,
    val latitud: Double?,
    val longitud: Double?,
    val fotoUri: String? = null,
    val pendienteSincronizacion: Boolean = true
) {
    init {
        require(id.isNotBlank())
        require(descripcion.isNotBlank())
        require((latitud == null) == (longitud == null))
    }
}
