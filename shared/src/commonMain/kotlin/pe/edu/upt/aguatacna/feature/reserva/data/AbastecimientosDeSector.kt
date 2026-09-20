package pe.edu.upt.aguatacna.feature.reserva.data

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.AbastecimientosDelSector
import pe.edu.upt.aguatacna.feature.sector.domain.repository.SectorRepository
import pe.edu.upt.aguatacna.feature.sector.domain.usecase.ProximoAbastecimiento

/** Une reserva con el cronograma de sector usando los contratos que sector publicó para ello. */
class AbastecimientosDeSector(
    private val sectores: SectorRepository,
    private val sectorDelUsuario: suspend () -> String?
) : AbastecimientosDelSector {

    override suspend fun iniciosHasta(ahora: LocalDateTime): List<LocalDateTime> {
        val sectorId = sectorDelUsuario() ?: return emptyList()
        return sectores.obtenerCronogramas(sectorId).map { it.inicio }.filter { it <= ahora }
    }

    override suspend fun proximoDesde(ahora: LocalDateTime): LocalDateTime? {
        val sectorId = sectorDelUsuario() ?: return null
        return ProximoAbastecimiento().calcular(sectores.obtenerCronogramas(sectorId), ahora)?.inicio
    }
}
