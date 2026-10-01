package pe.edu.upt.aguatacna.feature.recibo

import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.service.EvaluadorConsumo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EvaluadorConsumoTest {

    @Test
    fun masDe100M3EsAltoConsumo() {
        assertEquals(EstadoConsumo.ALTO_CONSUMO, EvaluadorConsumo.evaluar(120))
        assertEquals(EstadoConsumo.ALTO_CONSUMO, EvaluadorConsumo.evaluar(101))
    }

    @Test
    fun hasta100M3EsNormal() {
        assertEquals(EstadoConsumo.NORMAL, EvaluadorConsumo.evaluar(100))
        assertEquals(EstadoConsumo.NORMAL, EvaluadorConsumo.evaluar(33))
        assertEquals(EstadoConsumo.NORMAL, EvaluadorConsumo.evaluar(0))
    }

    @Test
    fun promedioUsaHastaSeisMeses() {
        assertEquals(16, EvaluadorConsumo.promedio(listOf(16, 16, 15, 17, 16, 16)))
        assertEquals(16, EvaluadorConsumo.promedio(listOf(16, 16, 16, 16, 16, 16, 100)))
        assertNull(EvaluadorConsumo.promedio(emptyList()))
    }

    @Test
    fun variacionNecesitaUnPromedioPositivo() {
        assertEquals(106, EvaluadorConsumo.variacionPorcentaje(33, 16))
        assertEquals(-50, EvaluadorConsumo.variacionPorcentaje(8, 16))
        assertNull(EvaluadorConsumo.variacionPorcentaje(20, null))
        assertNull(EvaluadorConsumo.variacionPorcentaje(20, 0))
    }
}
