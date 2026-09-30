package pe.edu.upt.aguatacna.feature.recibo

import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TipoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.service.EvaluadorConsumo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class EvaluadorConsumoTest {

    private val previos = listOf(16, 16, 15, 17, 16, 16)

    @Test
    fun consumo33EsNormalAunqueDupliqueElPromedio() {
        val estado = EvaluadorConsumo.evaluar(33, previos, TipoConsumo.LECTURA, "Agosto")

        assertIs<EstadoConsumo.Normal>(estado)
        assertEquals(106, estado.excesoPorcentaje)
        assertEquals(16, estado.promedioHistorico)
        assertEquals("+106 %", estado.variacionTexto)
    }

    @Test
    fun consumo120EsAltoConsumoConMensajeCorto() {
        val estado = EvaluadorConsumo.evaluar(120, previos, TipoConsumo.LECTURA, "Agosto")

        assertIs<EstadoConsumo.Atipico>(estado)
        assertEquals("Alto consumo", estado.chipTexto)
        assertEquals("Agosto superó los 100 m³. Revisa si hay alguna fuga en casa.", estado.mensaje)
    }

    @Test
    fun exactamente100NoEsAltoConsumo() {
        assertIs<EstadoConsumo.Normal>(EvaluadorConsumo.evaluar(100, previos, TipoConsumo.LECTURA))
    }

    @Test
    fun reciboPorPromedioDe120TambienEsAltoConsumo() {
        assertIs<EstadoConsumo.Atipico>(EvaluadorConsumo.evaluar(120, previos, TipoConsumo.PROMEDIO, "Agosto"))
    }

    @Test
    fun reciboPorPromedioBajoElLimiteEsFacturadoPorPromedio() {
        val estado = EvaluadorConsumo.evaluar(33, previos, TipoConsumo.PROMEDIO, "Agosto")
        assertEquals(EstadoConsumo.FacturadoPorPromedio, estado)
        assertEquals("—", estado.variacionTexto)
    }

    @Test
    fun sinMesesPreviosEsSinHistorial() {
        assertEquals(EstadoConsumo.SinHistorial, EvaluadorConsumo.evaluar(10, emptyList(), TipoConsumo.LECTURA))
    }

    @Test
    fun altoConsumoSinHistorialMuestraVariacionVacia() {
        val estado = EvaluadorConsumo.evaluar(150, emptyList(), TipoConsumo.LECTURA, "Agosto")
        assertIs<EstadoConsumo.Atipico>(estado)
        assertNull(estado.excesoPorcentaje)
        assertEquals("—", estado.variacionTexto)
    }

    @Test
    fun promedioUsaHastaSeisMeses() {
        assertEquals(16, EvaluadorConsumo.promedio(listOf(16, 16, 16, 16, 16, 16, 100)))
        assertNull(EvaluadorConsumo.promedio(emptyList()))
    }

    @Test
    fun variacionNecesitaUnPromedioPositivo() {
        assertEquals(25, EvaluadorConsumo.variacionPorcentaje(20, 16))
        assertEquals(-50, EvaluadorConsumo.variacionPorcentaje(8, 16))
        assertNull(EvaluadorConsumo.variacionPorcentaje(20, null))
        assertNull(EvaluadorConsumo.variacionPorcentaje(20, 0))
    }
}
