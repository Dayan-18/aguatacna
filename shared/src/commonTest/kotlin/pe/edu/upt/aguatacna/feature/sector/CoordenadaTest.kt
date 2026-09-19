package pe.edu.upt.aguatacna.feature.sector

import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CoordenadaTest {

    @Test // CA-17
    fun unGradoDeLatitudEquivaleAUnos111Km() {
        val distancia = Coordenada(-18.0, -70.25).distanciaKmHasta(Coordenada(-17.0, -70.25))
        assertEquals(111.19, distancia, absoluteTolerance = 0.01)
    }

    @Test // CA-17
    fun laMismaCoordenadaEstaADistanciaCero() {
        val casa = Coordenada(-18.0, -70.25)
        assertEquals(0.0, casa.distanciaKmHasta(casa), absoluteTolerance = 0.0001)
    }

    @Test // CA-18
    fun rechazaUnaLatitudFueraDeRango() {
        assertFailsWith<IllegalArgumentException> { Coordenada(-95.0, -70.25) }
    }
}
