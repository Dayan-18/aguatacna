package pe.edu.upt.aguatacna.feature.sector

import kotlinx.datetime.LocalTime
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada
import pe.edu.upt.aguatacna.feature.sector.domain.model.EstadoCisterna
import pe.edu.upt.aguatacna.feature.sector.domain.model.PuntoCisterna
import pe.edu.upt.aguatacna.feature.sector.domain.usecase.BuscarCisternasCercanas
import kotlin.test.Test
import kotlin.test.assertEquals

class BuscarCisternasCercanasTest {

    private val buscar = BuscarCisternasCercanas()
    private val casa = Coordenada(-18.0000, -70.2500)

    // 0.01 grados de latitud son aproximadamente 1.1 km
    private fun punto(id: String, latitud: Double) = PuntoCisterna(
        id = id,
        sectorId = "CN-04",
        nombre = id,
        ubicacion = Coordenada(latitud, -70.2500),
        horarioInicio = LocalTime(7, 0),
        horarioFin = LocalTime(13, 0),
        estado = EstadoCisterna.ACTIVO
    )

    private val puntos = listOf(
        punto("lejos", -18.0300),
        punto("cerca", -18.0100),
        punto("fuera", -18.0600)
    )

    @Test // CA-10
    fun descartaLasQueEstanFueraDelRadio() {
        val ids = buscar.buscar(puntos, casa, radioKm = 4.0).map { it.punto.id }
        assertEquals(listOf("cerca", "lejos"), ids)
    }

    @Test // CA-11
    fun ordenaDeLaMasCercanaALaMasLejana() {
        val resultado = buscar.buscar(puntos, casa, radioKm = 10.0)
        assertEquals(listOf("cerca", "lejos", "fuera"), resultado.map { it.punto.id })
        assertEquals(1.11, resultado.first().distanciaKm, absoluteTolerance = 0.01)
    }
}
