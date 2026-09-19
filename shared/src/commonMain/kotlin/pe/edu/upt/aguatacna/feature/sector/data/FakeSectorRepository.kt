package pe.edu.upt.aguatacna.feature.sector.data

import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.feature.sector.domain.model.ConfirmacionHorario
import pe.edu.upt.aguatacna.feature.sector.domain.model.Cronograma
import pe.edu.upt.aguatacna.feature.sector.domain.model.PuntoCisterna
import pe.edu.upt.aguatacna.feature.sector.domain.model.Sector
import pe.edu.upt.aguatacna.feature.sector.domain.repository.SectorRepository

// Implementación temporal para que feature/reserva y la interfaz avancen
// sin esperar a Room ni al servicio de datos (constitución, artículo VII).
class FakeSectorRepository(private val hoy: LocalDate) : SectorRepository {

    private val confirmaciones = mutableListOf<ConfirmacionHorario>()

    override suspend fun obtenerSectores(): List<Sector> = listaSectores

    override suspend fun obtenerCronogramas(sectorId: String): List<Cronograma> =
        cronogramasDeLaSemana(hoy).filter { it.sectorId == sectorId }

    override suspend fun obtenerPuntosCisterna(sectorId: String): List<PuntoCisterna> =
        listaPuntosCisterna.filter { it.sectorId == sectorId }

    override suspend fun obtenerConfirmaciones(
        sectorId: String,
        fecha: LocalDate
    ): List<ConfirmacionHorario> =
        confirmaciones.filter { it.sectorId == sectorId && it.momento.date == fecha }

    override suspend fun registrarConfirmacion(confirmacion: ConfirmacionHorario) {
        confirmaciones.add(confirmacion)
    }
}
