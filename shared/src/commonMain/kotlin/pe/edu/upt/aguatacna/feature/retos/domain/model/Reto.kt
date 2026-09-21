package pe.edu.upt.aguatacna.feature.retos.domain.model

data class Reto(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val litrosMeta: Int,
    val activo: Boolean = true
) {
    init {
        require(id.isNotBlank())
        require(titulo.isNotBlank())
        require(litrosMeta > 0)
    }
}
