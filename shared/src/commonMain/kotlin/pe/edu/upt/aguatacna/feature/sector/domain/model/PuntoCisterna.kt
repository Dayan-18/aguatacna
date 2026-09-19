package pe.edu.upt.aguatacna.feature.sector.domain.model

import kotlinx.datetime.LocalTime

enum class EstadoCisterna { ACTIVO, EN_RUTA, TERMINADO }

data class PuntoCisterna(
    val id: String,
    val sectorId: String,
    val nombre: String,
    val ubicacion: Coordenada,
    val horarioInicio: LocalTime,
    val horarioFin: LocalTime,
    val estado: EstadoCisterna
)
