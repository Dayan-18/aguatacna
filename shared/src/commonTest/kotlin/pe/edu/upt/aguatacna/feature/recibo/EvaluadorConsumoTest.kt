package pe.edu.upt.aguatacna.feature.recibo

import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TipoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.service.EvaluadorConsumo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class EvaluadorConsumoTest {

    // Casos obligatorios de la tabla del plan T-1.3

    @Test
    fun consumo33ConPromedio16EsAtipicoConExceso106() {
        val mesesPrevios = listOf(16, 16, 15, 17, 16, 16)
        val estado = EvaluadorConsumo.evaluar(
            consumoM3 = 33,
            mesesPreviosM3 = mesesPrevios,
            tipoConsumo = TipoConsumo.LECTURA,
            mesDisplay = "Agosto"
        )

        assertIs<EstadoConsumo.Atipico>(estado)
        assertEquals(106, estado.excesoPorcentaje)
        assertEquals(16, estado.promedioHistorico)
        assertEquals("+106 %", estado.variacionTexto)
    }

    @Test
    fun consumo32ConPromedio16EsNormalConExceso100() {
        // Exceso 100 %, no es mayor a 100 -> Normal
        val mesesPrevios = listOf(16, 16, 15, 17, 16, 16)
        val estado = EvaluadorConsumo.evaluar(
            consumoM3 = 32,
            mesesPreviosM3 = mesesPrevios,
            tipoConsumo = TipoConsumo.LECTURA,
            mesDisplay = "Agosto"
        )

        assertIs<EstadoConsumo.Normal>(estado)
        assertEquals(100, estado.excesoPorcentaje)
        assertEquals(16, estado.promedioHistorico)
        assertEquals("+100 %", estado.variacionTexto)
    }

    @Test
    fun consumo20ConPromedio16EsNormalConExceso25() {
        val mesesPrevios = listOf(16, 16, 15, 17, 16, 16)
        val estado = EvaluadorConsumo.evaluar(
            consumoM3 = 20,
            mesesPreviosM3 = mesesPrevios,
            tipoConsumo = TipoConsumo.LECTURA,
            mesDisplay = "Agosto"
        )

        assertIs<EstadoConsumo.Normal>(estado)
        assertEquals(25, estado.excesoPorcentaje)
        assertEquals(16, estado.promedioHistorico)
        assertEquals("+25 %", estado.variacionTexto)
    }

    @Test
    fun conSoloDosMesesEsSinHistorial() {
        val mesesPrevios = listOf(16, 16)
        val estado = EvaluadorConsumo.evaluar(
            consumoM3 = 40,
            mesesPreviosM3 = mesesPrevios,
            tipoConsumo = TipoConsumo.LECTURA,
            mesDisplay = "Agosto"
        )

        assertIs<EstadoConsumo.SinHistorial>(estado)
        assertEquals(2, estado.mesesDisponibles)
        assertEquals("—", estado.variacionTexto)
    }

    @Test
    fun conHistorialVacioEsSinHistorial() {
        val estado = EvaluadorConsumo.evaluar(
            consumoM3 = 10,
            mesesPreviosM3 = emptyList(),
            tipoConsumo = TipoConsumo.LECTURA,
            mesDisplay = "Agosto"
        )

        assertIs<EstadoConsumo.SinHistorial>(estado)
        assertEquals(0, estado.mesesDisponibles)
        assertEquals("—", estado.variacionTexto)
    }

    @Test
    fun tipoConsumoPromedioSiempreEsFacturadoPorPromedio() {
        val mesesPrevios = listOf(16, 16, 15, 17, 16, 16)
        val estado = EvaluadorConsumo.evaluar(
            consumoM3 = 33,
            mesesPreviosM3 = mesesPrevios,
            tipoConsumo = TipoConsumo.PROMEDIO,
            mesDisplay = "Agosto"
        )

        assertEquals(EstadoConsumo.FacturadoPorPromedio, estado)
        assertEquals("—", estado.variacionTexto)
    }
}
