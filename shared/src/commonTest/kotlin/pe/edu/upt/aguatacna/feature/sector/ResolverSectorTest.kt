package pe.edu.upt.aguatacna.feature.sector

import pe.edu.upt.aguatacna.feature.sector.data.listaSectores
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada
import pe.edu.upt.aguatacna.feature.sector.domain.usecase.ResolverSector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ResolverSectorTest {

    private val resolver = ResolverSector()

    @Test // CA-09
    fun asignaElSectorDeCentroMasCercano() {
        val casaEnCiudadNueva = Coordenada(-17.9841, -70.2372)
        assertEquals("CN-04", resolver.resolver(casaEnCiudadNueva, listaSectores)?.id)
    }

    @Test // CA-09
    fun sinSectoresDevuelveNull() {
        assertNull(resolver.resolver(Coordenada(-18.0, -70.25), emptyList()))
    }
}
