package pe.edu.upt.aguatacna.feature.reserva

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ClaseIntervalo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Litros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.OrigenLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.DeclararSinAgua
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DeclararSinAguaTest {
    private val declarar = DeclararSinAgua()

    private fun reserva(
        consumo: Double = 50.0,
        tipo: TipoLlenado = TipoLlenado.COMPLETO,
        origen: OrigenLlenado = OrigenLlenado.REAL
    ) = Reserva(CAPACIDAD_1000, ConsumoHorario(consumo), EventoLlenado(enHora(0), tipo, origen))

    @Test // CA-18
    fun laReservaQuedaVaciaEnElMomentoDeLaDeclaracion() {
        val resultado = declarar(reserva(), emptyList(), enHora(16))
        assertEquals(Litros.CERO, resultado.reserva.nivelEn(enHora(16)).litros)
        assertEquals(Litros.CERO, resultado.reserva.nivelEn(enHora(30)).litros)
        assertEquals(enHora(16), resultado.reserva.agotamientoProyectado())
    }

    @Test // CA-19
    fun laDeclaracionAgregaUnIntervaloObservado() {
        val intervalo = assertNotNull(declarar(reserva(), emptyList(), enHora(16)).intervaloObservado)
        assertEquals(ClaseIntervalo.OBSERVADO, intervalo.clase)
        assertEquals(enHora(0), intervalo.inicio)
        assertEquals(enHora(16), intervalo.fin)
        assertEquals(Litros(1000.0), intervalo.litrosConsumidos)
    }

    @Test // CA-19
    fun conLlenadoALaMitadElIntervaloConsumioLaMitad() {
        val resultado = declarar(reserva(tipo = TipoLlenado.MITAD), emptyList(), enHora(10))
        assertEquals(Litros(500.0), assertNotNull(resultado.intervaloObservado).litrosConsumidos)
    }

    @Test // CA-19
    fun elConsumoSeRecalculaConLaObservacion() {
        // 1000 L en 16 h son 62,5 L/h: dentro del tope de 30 % sobre 50 L/h.
        val resultado = declarar(reserva(consumo = 50.0), emptyList(), enHora(16))
        assertEquals(62.5, resultado.reserva.consumo.litrosPorHora, absoluteTolerance = 0.001)
    }

    @Test // CA-19
    fun elIntervaloObservadoSeSumaALosAnteriores() {
        // Mediana entre lo observado antes (100 L/h) y ahora (50 L/h): 75 L/h.
        val previo = intervalo(100, 110, clase = ClaseIntervalo.OBSERVADO)
        val resultado = declarar(reserva(consumo = 75.0), listOf(previo), enHora(20))
        assertEquals(75.0, resultado.reserva.consumo.litrosPorHora, absoluteTolerance = 0.001)
    }

    @Test // CA-20
    fun unaDeclaracionNoSubeElConsumoMasDeUnTreintaPorCiento() {
        // Se vació en 10 h: 100 L/h observados, pero el tope es 50 * 1,3 = 65.
        val resultado = declarar(reserva(consumo = 50.0), emptyList(), enHora(10))
        assertEquals(65.0, resultado.reserva.consumo.litrosPorHora, absoluteTolerance = 0.001)
    }

    @Test // CA-20
    fun unaDeclaracionNoBajaElConsumoMasDeUnTreintaPorCiento() {
        // Se vació en 100 h: 10 L/h observados, pero el piso es 50 * 0,7 = 35.
        val resultado = declarar(reserva(consumo = 50.0), emptyList(), enHora(100))
        assertEquals(35.0, resultado.reserva.consumo.litrosPorHora, absoluteTolerance = 0.001)
    }

    @Test
    fun deUnLlenadoAsumidoNoSeAprendeNadaPeroLaReservaQuedaVacia() {
        val resultado = declarar(reserva(origen = OrigenLlenado.ASUMIDO), emptyList(), enHora(10))
        assertNull(resultado.intervaloObservado)
        assertEquals(50.0, resultado.reserva.consumo.litrosPorHora, absoluteTolerance = 0.001)
        assertEquals(enHora(10), resultado.reserva.agotamientoProyectado())
    }

    @Test
    fun noSePuedeQuedarSinAguaAntesDelLlenado() {
        assertFailsWith<IllegalArgumentException> { declarar(reserva(), emptyList(), enHora(0)) }
    }
}
