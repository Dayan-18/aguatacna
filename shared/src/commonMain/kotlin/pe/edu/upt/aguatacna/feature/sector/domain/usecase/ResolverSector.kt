package pe.edu.upt.aguatacna.feature.sector.domain.usecase

import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada
import pe.edu.upt.aguatacna.feature.sector.domain.model.Sector

class ResolverSector {

    fun resolver(ubicacion: Coordenada, sectores: List<Sector>): Sector? =
        sectores.minByOrNull { it.centro.distanciaKmHasta(ubicacion) }
}
