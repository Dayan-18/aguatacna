package pe.edu.upt.aguatacna.feature.retos.presentation

import pe.edu.upt.aguatacna.feature.retos.domain.model.Racha
import pe.edu.upt.aguatacna.feature.retos.domain.model.Reto

data class RetosUiState(
    val cargando: Boolean = true,
    val retos: List<Reto> = emptyList(),
    val cumplidos: Set<String> = emptySet(),
    val racha: Racha = Racha(0),
    val mensaje: String? = null
)
