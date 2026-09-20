package pe.edu.upt.aguatacna.feature.retos.domain.model

import kotlinx.datetime.LocalDate

data class RetoUsuario(
    val retoId: String,
    val fecha: LocalDate,
    val cumplido: Boolean
) {
    init {
        require(retoId.isNotBlank())
    }
}
