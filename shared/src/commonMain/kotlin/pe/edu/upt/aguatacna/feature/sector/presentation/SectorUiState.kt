package pe.edu.upt.aguatacna.feature.sector.presentation

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.sector.domain.model.CisternaCercana
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada
import pe.edu.upt.aguatacna.feature.sector.domain.model.Cronograma
import pe.edu.upt.aguatacna.feature.sector.domain.model.Sector

data class SectorUiState(
    val cargando: Boolean = true,
    val ahora: LocalDateTime? = null,
    val sector: Sector? = null,
    val ubicacionCasa: Coordenada? = null,
    val cronogramaDeHoy: Cronograma? = null,
    val aguaLlegandoAhora: Boolean = false,
    val proximoAbastecimiento: Cronograma? = null,
    val cisternas: List<CisternaCercana> = emptyList(),
    val confirmacionesDeHoy: Int = 0,
    val mensaje: String? = null
)
