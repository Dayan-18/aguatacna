package pe.edu.upt.aguatacna.feature.sector.domain.repository

import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.feature.sector.domain.model.ConfirmacionHorario
import pe.edu.upt.aguatacna.feature.sector.domain.model.Cronograma
import pe.edu.upt.aguatacna.feature.sector.domain.model.PuntoCisterna
import pe.edu.upt.aguatacna.feature.sector.domain.model.Sector

interface SectorRepository {
    suspend fun obtenerSectores(): List<Sector>
    suspend fun obtenerCronogramas(sectorId: String): List<Cronograma>
    suspend fun obtenerPuntosCisterna(sectorId: String): List<PuntoCisterna>
    suspend fun obtenerConfirmaciones(sectorId: String, fecha: LocalDate): List<ConfirmacionHorario>
    suspend fun registrarConfirmacion(confirmacion: ConfirmacionHorario)
}
