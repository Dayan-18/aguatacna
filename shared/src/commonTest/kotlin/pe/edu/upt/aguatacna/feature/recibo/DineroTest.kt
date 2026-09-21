package pe.edu.upt.aguatacna.feature.recibo

import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DineroTest {

    @Test
    fun creacionDesdeCentimosYSoles() {
        val d1 = Dinero(7420L)
        assertEquals(74, d1.soles)
        assertEquals(20, d1.centavos)

        val d2 = Dinero.desdeSoles(74.20)
        assertEquals(7420L, d2.centimos)
        assertEquals(d1, d2)
    }

    @Test
    fun operacionesAritmeticas() {
        val d1 = Dinero(5000L)
        val d2 = Dinero(2420L)
        assertEquals(Dinero(7420L), d1 + d2)
        assertEquals(Dinero(2580L), d1 - d2)
        assertTrue(d1 > d2)
    }

    @Test
    fun formateoPeruano() {
        val d = Dinero(7420L)
        assertEquals("S/ 74,20", d.formatear())
        assertEquals("74,20", d.formatearSoloNumero())

        val miles = Dinero(125050L) // S/ 1,250.50
        assertEquals("S/ 1 250,50", miles.formatear())
    }

    @Test
    fun parseoDeStrings() {
        val d1 = Dinero.parsear("78.00")
        assertNotNull(d1)
        assertEquals(7800L, d1.centimos)

        val d2 = Dinero.parsear("*****78.00")
        assertNotNull(d2)
        assertEquals(7800L, d2.centimos)

        val d3 = Dinero.parsear(" 74,20 ")
        assertNotNull(d3)
        assertEquals(7420L, d3.centimos)

        assertNull(Dinero.parsear("invalido"))
    }
}
