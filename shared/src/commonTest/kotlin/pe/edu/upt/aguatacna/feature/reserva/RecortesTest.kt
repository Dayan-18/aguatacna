package pe.edu.upt.aguatacna.feature.reserva

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Deficit
import pe.edu.upt.aguatacna.feature.reserva.domain.model.HabitosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Litros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Recomendacion
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.SimularRecortes
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.SugerirRecortes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RecortesTest {
    private val sugerir = SugerirRecortes()
    private val simular = SimularRecortes()
    private val todoLoQueGasta = HabitosDelHogar(duchasPorDia = 2, usaLavadora = true, riegaJardin = true)

    @Test // CA-21
    fun sinDeficitNoSeSugiereNada() {
        assertEquals(emptyList(), sugerir(Deficit(0.0), todoLoQueGasta))
    }

    @Test // CA-21, CA-12
    fun sinCronogramaNoSeSugiereNada() {
        assertEquals(emptyList(), sugerir(null, todoLoQueGasta))
    }

    @Test // CA-22
    fun conDeficitSeOrdenanPorLitrosDeMayorAMenor() {
        val sugeridas = sugerir(Deficit(10.0), todoLoQueGasta)
        assertEquals(
            listOf(Recomendacion.POSTERGAR_LAVADO, Recomendacion.NO_REGAR, Recomendacion.DUCHAS_CORTAS, Recomendacion.CERRAR_EL_CANO),
            sugeridas
        )
        assertEquals(sugeridas.sortedByDescending { it.litrosQueAhorra }, sugeridas)
    }

    @Test // CA-32
    fun noSeSugiereRegarSiElHogarNoRiega() {
        val sinRiego = todoLoQueGasta.copy(riegaJardin = false)
        assertFalse(Recomendacion.NO_REGAR in sugerir(Deficit(5.0), sinRiego))
    }

    @Test // CA-32
    fun noSeSugiereLaLavadoraSiNoTiene() {
        val sinLavadora = todoLoQueGasta.copy(usaLavadora = false)
        assertFalse(Recomendacion.POSTERGAR_LAVADO in sugerir(Deficit(5.0), sinLavadora))
    }

    @Test // CA-32
    fun elCanoSiempreSeSugiere() {
        val nada = HabitosDelHogar(0, usaLavadora = false, riegaJardin = false)
        assertEquals(listOf(Recomendacion.CERRAR_EL_CANO), sugerir(Deficit(5.0), nada))
    }

    @Test // CA-29
    fun elAhorroSumaLosLitrosDeLasElegidas() {
        val elegidas = setOf(Recomendacion.POSTERGAR_LAVADO, Recomendacion.DUCHAS_CORTAS)
        assertEquals(Litros(150.0), simular(Deficit(10.0), ConsumoHorario(50.0), elegidas).ahorro)
    }

    @Test // CA-30, CA-31
    fun elEjemploDelFigmaGanaTresHorasYVeinteYFaltanCasiSiete() {
        val elegidas = setOf(
            Recomendacion.POSTERGAR_LAVADO, Recomendacion.DUCHAS_CORTAS,
            Recomendacion.NO_REGAR, Recomendacion.CERRAR_EL_CANO
        )
        val resultado = simular(Deficit(10.0 + 20.0 / 60), ConsumoHorario(82.0), elegidas)
        assertEquals(Litros(275.0), resultado.ahorro)
        assertEquals(3.354, resultado.horasGanadas, absoluteTolerance = 0.001)
        assertEquals(6.979, resultado.horasQueFaltan, absoluteTolerance = 0.001)
        assertFalse(resultado.cubreElDeficit)
    }

    @Test // CA-31
    fun siLoRecortadoAlcanzaNoFaltaNada() {
        val elegidas = setOf(Recomendacion.POSTERGAR_LAVADO, Recomendacion.NO_REGAR)
        val resultado = simular(Deficit(1.0), ConsumoHorario(50.0), elegidas)
        assertEquals(0.0, resultado.horasQueFaltan, absoluteTolerance = 0.001)
        assertTrue(resultado.cubreElDeficit)
    }

    @Test
    fun sinElegirNadaNoSeGanaNada() {
        val resultado = simular(Deficit(4.0), ConsumoHorario(50.0), emptySet())
        assertEquals(0.0, resultado.horasGanadas, absoluteTolerance = 0.001)
        assertEquals(4.0, resultado.horasQueFaltan, absoluteTolerance = 0.001)
    }
}
