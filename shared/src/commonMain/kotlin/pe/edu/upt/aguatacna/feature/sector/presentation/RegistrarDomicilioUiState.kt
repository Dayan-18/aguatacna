package pe.edu.upt.aguatacna.feature.sector.presentation

import pe.edu.upt.aguatacna.feature.sector.domain.model.Sector

data class RegistrarDomicilioUiState(
    val cargando: Boolean = false,
    val sector: Sector? = null,
    val continuidad: String = "",
    val etiquetaMapa: String = "SECTOR"
)
