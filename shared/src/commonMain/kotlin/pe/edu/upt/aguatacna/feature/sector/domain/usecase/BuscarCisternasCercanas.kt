package pe.edu.upt.aguatacna.feature.sector.domain.usecase

import pe.edu.upt.aguatacna.feature.sector.domain.model.CisternaCercana
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada
import pe.edu.upt.aguatacna.feature.sector.domain.model.PuntoCisterna

class BuscarCisternasCercanas {

    fun buscar(
        puntos: List<PuntoCisterna>,
        ubicacion: Coordenada,
        radioKm: Double
    ): List<CisternaCercana> =
        puntos
            .map { CisternaCercana(it, it.ubicacion.distanciaKmHasta(ubicacion)) }
            .filter { it.distanciaKm <= radioKm }
            .sortedBy { it.distanciaKm }
}
