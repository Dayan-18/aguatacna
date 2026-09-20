package pe.edu.upt.aguatacna.feature.reserva

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import pe.edu.upt.aguatacna.feature.reserva.data.AbastecimientosDeSector
import pe.edu.upt.aguatacna.feature.sector.data.FakeSectorRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AbastecimientosDeSectorTest {
    private val hoy = LocalDate(2026, 9, 21)
    private val sectores = FakeSectorRepository(hoy)
    private val mediodia = LocalDateTime(hoy, LocalTime(12, 0))

    @Test
    fun sinSectorNoHayAbastecimientosNiProximo() {
        val adaptador = AbastecimientosDeSector(sectores) { null }
        assertEquals(emptyList(), ejecutar { adaptador.iniciosHasta(mediodia) })
        assertNull(ejecutar { adaptador.proximoDesde(mediodia) })
    }

    @Test
    fun losInicioSoloSonLosYaOcurridos() {
        val adaptador = AbastecimientosDeSector(sectores) { "CN-04" }
        val inicios = ejecutar { adaptador.iniciosHasta(mediodia) }
        assertTrue(inicios.isNotEmpty())
        assertTrue(inicios.all { it <= mediodia })
    }

    @Test
    fun elProximoEsElPrimerInicioFuturo() {
        val adaptador = AbastecimientosDeSector(sectores) { "CN-04" }
        val proximo = ejecutar { adaptador.proximoDesde(mediodia) }
        assertTrue(proximo != null && proximo > mediodia)
    }
}
