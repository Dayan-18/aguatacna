package pe.edu.upt.aguatacna.feature.sector.data

import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.feature.sector.data.local.SectorDao
import pe.edu.upt.aguatacna.feature.sector.data.sync.NubeSectorSupabase
import pe.edu.upt.aguatacna.feature.sector.domain.model.ConfirmacionHorario
import pe.edu.upt.aguatacna.feature.sector.domain.model.Cronograma
import pe.edu.upt.aguatacna.feature.sector.domain.model.PuntoCisterna
import pe.edu.upt.aguatacna.feature.sector.domain.model.Sector
import pe.edu.upt.aguatacna.feature.sector.domain.repository.SectorRepository

/**
 * Repositorio real. Room es la fuente de verdad (art. II): la sectorización se descarga de
 * Supabase la primera vez y queda cacheada localmente. Las confirmaciones del vecindario se
 * leen de la vista anónima; la del usuario se guarda local y se sube a la nube.
 */
class SectorRepositoryImpl(
    private val dao: SectorDao,
    private val nube: NubeSectorSupabase
) : SectorRepository {

    override suspend fun obtenerSectores(): List<Sector> {
        if (dao.sectores().isEmpty()) dao.guardarSectores(nube.descargarSectores())
        return dao.sectores().map { it.aDominio() }
    }

    override suspend fun obtenerCronogramas(sectorId: String): List<Cronograma> {
        if (dao.cronogramas(sectorId).isEmpty()) dao.guardarCronogramas(nube.descargarCronogramas())
        return dao.cronogramas(sectorId).map { it.aDominio() }
    }

    override suspend fun obtenerPuntosCisterna(sectorId: String): List<PuntoCisterna> {
        if (dao.puntosCisterna(sectorId).isEmpty()) dao.guardarPuntos(nube.descargarPuntos())
        return dao.puntosCisterna(sectorId).map { it.aDominio() }
    }

    override suspend fun obtenerConfirmaciones(sectorId: String, fecha: LocalDate): List<ConfirmacionHorario> =
        nube.descargarConfirmaciones()
            .map { it.aDominio() }
            .filter { it.sectorId == sectorId && it.momento.date == fecha }

    override suspend fun registrarConfirmacion(confirmacion: ConfirmacionHorario) {
        val entidad = confirmacion.aEntidad()
        dao.guardarConfirmacion(entidad)
        nube.subirConfirmacion(entidad)
    }
}
