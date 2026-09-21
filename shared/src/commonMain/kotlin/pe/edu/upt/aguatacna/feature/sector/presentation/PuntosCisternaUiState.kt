package pe.edu.upt.aguatacna.feature.sector.presentation

import pe.edu.upt.aguatacna.feature.sector.domain.model.CisternaCercana
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada

data class PuntosCisternaUiState(
    val cargando: Boolean = true,
    val ubicacionCasa: Coordenada? = null,
    val cisternas: List<CisternaCercana> = emptyList()
)
