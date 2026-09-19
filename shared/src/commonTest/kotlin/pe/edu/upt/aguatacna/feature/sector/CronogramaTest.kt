package pe.edu.upt.aguatacna.feature.sector

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CronogramaTest {

    @Test // CA-01
    fun rechazaUnHorarioQueTerminaAntesDeEmpezar() {
        assertFailsWith<IllegalArgumentException> { cronograma(HOY, desde = 9, hasta = 5) }
    }

    @Test // CA-02
    fun elInicioCuentaComoAbastecidoYElFinNo() {
        val c = cronograma(HOY, desde = 5, hasta = 9)
        assertTrue(c.contiene(momento(HOY, 5)))
        assertTrue(c.contiene(momento(HOY, 8, 59)))
        assertFalse(c.contiene(momento(HOY, 9)))
    }

    @Test // CA-03
    fun calculaLaDuracionEnMinutos() {
        assertEquals(240, cronograma(HOY, desde = 5, hasta = 9).duracionMinutos)
    }
}
