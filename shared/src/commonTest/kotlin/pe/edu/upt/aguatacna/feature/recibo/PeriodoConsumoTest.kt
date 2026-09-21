package pe.edu.upt.aguatacna.feature.recibo

import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PeriodoConsumoTest {

    @Test
    fun parseaLosDoceMesesDelAnio() {
        val mesesEsperados = listOf(
            "ENERO-2026" to 1,
            "FEBRERO-2026" to 2,
            "MARZO-2026" to 3,
            "ABRIL-2026" to 4,
            "MAYO-2026" to 5,
            "JUNIO-2026" to 6,
            "JULIO-2026" to 7,
            "AGOSTO-2026" to 8,
            "SETIEMBRE-2026" to 9,
            "OCTUBRE-2026" to 10,
            "NOVIEMBRE-2026" to 11,
            "DICIEMBRE-2026" to 12
        )

        for ((texto, mesEsperado) in mesesEsperados) {
            val periodo = PeriodoConsumo.parsear(texto)
            assertNotNull(periodo, "Debe parsear '$texto'")
            assertEquals(2026, periodo.anio)
            assertEquals(mesEsperado, periodo.mes)
        }
    }

    @Test
    fun aceptaVariantesSetiembreYSeptiembre() {
        val p1 = PeriodoConsumo.parsear("SETIEMBRE-2026")
        val p2 = PeriodoConsumo.parsear("SEPTIEMBRE-2026")
        assertNotNull(p1)
        assertNotNull(p2)
        assertEquals(9, p1.mes)
        assertEquals(9, p2.mes)
        assertEquals(p1, p2)
    }

    @Test
    fun aceptaSeparadoresGuionEspacioYSlash() {
        assertEquals(PeriodoConsumo(2026, 8), PeriodoConsumo.parsear("AGOSTO-2026"))
        assertEquals(PeriodoConsumo(2026, 8), PeriodoConsumo.parsear("AGOSTO 2026"))
        assertEquals(PeriodoConsumo(2026, 8), PeriodoConsumo.parsear("AGOSTO/2026"))
    }

    @Test
    fun manejaMinusculasYTildes() {
        assertEquals(PeriodoConsumo(2026, 8), PeriodoConsumo.parsear("agosto-2026"))
        assertEquals(PeriodoConsumo(2026, 8), PeriodoConsumo.parsear("Agosto 2026"))
    }

    @Test
    fun rechazaFormatosInvalidos() {
        assertNull(PeriodoConsumo.parsear(""))
        assertNull(PeriodoConsumo.parsear("INVENTADO-2026"))
        assertNull(PeriodoConsumo.parsear("AGOSTO"))
        assertNull(PeriodoConsumo.parsear("2026"))
        assertNull(PeriodoConsumo.parsear("AGOSTO-ABC"))
    }

    @Test
    fun navegaAlMesAnteriorYSiguiente() {
        val agosto = PeriodoConsumo(2026, 8)
        assertEquals(PeriodoConsumo(2026, 7), agosto.anterior())
        assertEquals(PeriodoConsumo(2026, 9), agosto.siguiente())

        val enero = PeriodoConsumo(2026, 1)
        assertEquals(PeriodoConsumo(2025, 12), enero.anterior())

        val diciembre = PeriodoConsumo(2026, 12)
        assertEquals(PeriodoConsumo(2027, 1), diciembre.siguiente())
    }

    @Test
    fun comparacionYDisplay() {
        val p1 = PeriodoConsumo(2026, 7)
        val p2 = PeriodoConsumo(2026, 8)
        assertTrue(p1 < p2)
        assertEquals("Agosto 2026", p2.displayCompleto)
        assertEquals("Ago", p2.mesCorto)
        assertEquals("Agosto", p2.mesLargo)
    }
}
