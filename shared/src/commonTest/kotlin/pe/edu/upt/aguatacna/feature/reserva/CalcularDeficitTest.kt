package pe.edu.upt.aguatacna.feature.reserva

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.CalcularDeficit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CalcularDeficitTest {
    private val calcular = CalcularDeficit()

    // 1000 L a 50 L/h desde la hora 6: se agota en la hora 26.
    private val reserva = Reserva(
        CAPACIDAD_1000, ConsumoHorario(50.0), EventoLlenado(enHora(6), TipoLlenado.COMPLETO)
    )

    @Test // CA-10
    fun elDeficitEsLaDiferenciaEntreElAgotamientoYElAbastecimiento() {
        val deficit = assertNotNull(calcular(reserva, enHora(29)))
        assertEquals(3.0, deficit.horas, absoluteTolerance = 0.001)
        assertTrue(deficit.hayDeficit)
    }

    @Test // CA-11
    fun siElAguaAlcanzaNoHayDeficit() {
        val deficit = assertNotNull(calcular(reserva, enHora(20)))
        assertEquals(0.0, deficit.horas, absoluteTolerance = 0.001)
        assertFalse(deficit.hayDeficit)
    }

    @Test // CA-11
    fun siSeAgotaJustoCuandoVuelveElAguaNoHayDeficit() {
        assertFalse(assertNotNull(calcular(reserva, enHora(26))).hayDeficit)
    }

    @Test // CA-12
    fun sinCronogramaNoHayDeficitNiError() {
        assertNull(calcular(reserva, null))
    }

    @Test // CA-10
    fun unaDeclaracionDeSinAguaFijaDesdeCuandoFalta() {
        val vaciada = reserva.copy(agotadaEn = enHora(15))
        assertEquals(14.0, assertNotNull(calcular(vaciada, enHora(29))).horas, absoluteTolerance = 0.001)
    }
}
