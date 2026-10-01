package pe.edu.upt.aguatacna.feature.reserva

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ClaseIntervalo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Habitantes
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.CalcularLitrosPorHabitanteDia
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class LitrosPorHabitanteDiaTest {
    private val calcular = CalcularLitrosPorHabitanteDia()

    @Test // CA-05
    fun doscientosCincuentaLitrosDiariosEntreCincoPersonasSonCincuenta() {
        val resultado = calcular(intervalosDe(96, 96), Habitantes(5))
        assertEquals(50.0, assertNotNull(resultado).valor, absoluteTolerance = 0.001)
    }

    @Test // CA-05
    fun unLlenadoDiarioDeMilCienLitrosEntreCuatroSonDoscientosSetentaYCinco() {
        val resultado = calcular(listOf(intervalo(0, 24, litros = 1100.0), intervalo(24, 48, litros = 1100.0)), Habitantes(4))
        assertEquals(275.0, assertNotNull(resultado).valor, absoluteTolerance = 0.001)
    }

    @Test // CA-36
    fun conUnSoloIntervaloInferidoAunNoHayIndicador() {
        assertNull(calcular(intervalosDe(24), Habitantes(4)))
    }

    @Test // CA-36
    fun unIntervaloObservadoBastaParaElIndicador() {
        val observado = intervalo(0, 24, clase = ClaseIntervalo.OBSERVADO)
        assertEquals(250.0, assertNotNull(calcular(listOf(observado), Habitantes(4))).valor, absoluteTolerance = 0.001)
    }

    @Test // CA-35
    fun dosLlenadosSeguidosNoDisparanElIndicador() {
        val relleno = intervalo(48, 49, litros = 1050.0)
        val resultado = calcular(intervalosDe(24, 24) + relleno, Habitantes(4))
        assertEquals(250.0, assertNotNull(resultado).valor, absoluteTolerance = 0.001)
    }

    @Test // CA-23
    fun sinIntervalosNoHayIndicador() {
        assertNull(calcular(emptyList(), Habitantes(4)))
    }
}
