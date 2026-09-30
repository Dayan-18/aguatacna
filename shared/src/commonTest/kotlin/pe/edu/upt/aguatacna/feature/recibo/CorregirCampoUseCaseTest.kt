package pe.edu.upt.aguatacna.feature.recibo

import pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.CorregirCampoUseCase
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.CorregirCampoUseCase.CampoEditable
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ResultadoCorreccion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CorregirCampoUseCaseTest {

    private val corregir = CorregirCampoUseCase()
    private val borrador = ReciboBorrador(
        periodoConsumo = Campo(PeriodoConsumo(2026, 8), 0.9f),
        consumoM3 = Campo(10, 0.5f),
        importeTotal = Campo(Dinero(7420L), 0.95f),
        lecturaAnteriorM3 = Campo(100, 0.9f),
        lecturaActualM3 = Campo(133, 0.9f)
    )

    private fun aplicada(campo: CampoEditable): ReciboBorrador =
        assertIs<ResultadoCorreccion.Aplicada>(corregir(borrador, campo)).borrador

    private fun invalida(campo: CampoEditable): String =
        assertIs<ResultadoCorreccion.Invalida>(corregir(borrador, campo)).mensaje

    @Test
    fun corregirElConsumoLoMarcaComoCorregido() {
        assertTrue(borrador.consumoM3.esDudoso)
        val corregido = aplicada(CampoEditable.ConsumoM3(33)).consumoM3
        assertEquals(33, corregido.valor)
        assertTrue(corregido.corregidoPorUsuario)
        assertFalse(corregido.esDudoso)
    }

    @Test
    fun elConsumoDebeEstarEntre0Y999() {
        assertEquals(0, aplicada(CampoEditable.ConsumoM3(0)).consumoM3.valor)
        assertEquals(999, aplicada(CampoEditable.ConsumoM3(999)).consumoM3.valor)
        invalida(CampoEditable.ConsumoM3(1000))
        invalida(CampoEditable.ConsumoM3(-1))
        invalida(CampoEditable.ConsumoM3(null))
    }

    @Test
    fun laLecturaAnteriorNoPuedeSuperarLaActual() {
        assertEquals(133, aplicada(CampoEditable.LecturaAnterior(133)).lecturaAnteriorM3.valor)
        assertEquals("La lectura anterior no puede superar la actual (133 m³)", invalida(CampoEditable.LecturaAnterior(134)))
        invalida(CampoEditable.LecturaAnterior(-5))
        invalida(CampoEditable.LecturaAnterior(null))
    }

    @Test
    fun laLecturaActualNoPuedeSerMenorQueLaAnterior() {
        assertEquals(100, aplicada(CampoEditable.LecturaActual(100)).lecturaActualM3.valor)
        assertEquals(
            "La lectura actual debe ser mayor o igual a la anterior (100 m³)",
            invalida(CampoEditable.LecturaActual(99))
        )
        invalida(CampoEditable.LecturaActual(null))
    }

    @Test
    fun elImporteDebeSerMayorACeroYHasta99999() {
        assertEquals(Dinero(9_999_900L), aplicada(CampoEditable.Importe(Dinero(9_999_900L))).importeTotal.valor)
        invalida(CampoEditable.Importe(Dinero.CERO))
        invalida(CampoEditable.Importe(Dinero(9_999_901L)))
        invalida(CampoEditable.Importe(null))
    }

    @Test
    fun elImporteEscritoSeRedondeaEnVezDeTruncarse() {
        // (78.29 * 100).toLong() daba 7828.
        assertEquals(Dinero(7829L), aplicada(CampoEditable.Importe(Dinero.parsear("78,29"))).importeTotal.valor)
    }

    @Test
    fun corregirElPeriodo() {
        val setiembre = PeriodoConsumo(2026, 9)
        val corregido = aplicada(CampoEditable.Periodo(setiembre)).periodoConsumo
        assertEquals(setiembre, corregido.valor)
        assertTrue(corregido.corregidoPorUsuario)
    }
}
