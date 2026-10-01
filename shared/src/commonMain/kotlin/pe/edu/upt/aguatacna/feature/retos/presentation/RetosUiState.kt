package pe.edu.upt.aguatacna.feature.retos.presentation

import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.feature.retos.domain.model.Racha
import pe.edu.upt.aguatacna.feature.retos.domain.model.Reto
import pe.edu.upt.aguatacna.feature.retos.domain.model.PosicionSector

data class RetosUiState(
    val cargando: Boolean = true,
    val retos: List<Reto> = emptyList(),
    val cumplidos: Set<String> = emptySet(),
    val diasCumplidos: Set<LocalDate> = emptySet(),
    val hoy: LocalDate? = null,
    val racha: Racha = Racha(0),
    val posicionSector: PosicionSector? = null,
    val mensaje: String? = null
)
