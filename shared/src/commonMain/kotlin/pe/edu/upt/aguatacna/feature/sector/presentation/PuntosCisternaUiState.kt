package pe.edu.upt.aguatacna.feature.sector.presentation

import pe.edu.upt.aguatacna.feature.sector.domain.model.CisternaCercana

data class PuntosCisternaUiState(
    val cargando: Boolean = true,
    val cisternas: List<CisternaCercana> = emptyList()
)
