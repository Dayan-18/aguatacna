package pe.edu.upt.aguatacna.feature.reserva

import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.OrigenLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.ResolverLlenadoVigente
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ResolverLlenadoVigenteTest {
    private val resolver = ResolverLlenadoVigente()

    private fun real(hora: Int, tipo: TipoLlenado = TipoLlenado.COMPLETO) = EventoLlenado(enHora(hora), tipo)

    private fun asumido(hora: Int) = EventoLlenado(enHora(hora), TipoLlenado.COMPLETO, OrigenLlenado.ASUMIDO)

    @Test
    fun sinAbastecimientosNuevosSigueElUltimoLlenadoReal() {
        val vigente = resolver(listOf(real(0), real(10)), listOf(enHora(5)), emptyList(), enHora(20))
        assertEquals(real(10), vigente)
    }

    @Test // CA-13
    fun siElSectorAbastecioSinConfirmacionSeAsumeUnLlenadoCompleto() {
        val vigente = resolver(listOf(real(0)), listOf(enHora(24)), emptyList(), enHora(30))
        assertEquals(asumido(24), vigente)
    }

    @Test // CA-13
    fun conVariasVentanasSinConfirmarSeAsumeLaMasReciente() {
        val vigente = resolver(listOf(real(0)), listOf(enHora(24), enHora(48)), emptyList(), enHora(50))
        assertEquals(asumido(48), vigente)
    }

    @Test // CA-13
    fun sinNingunLlenadoRealSeAsumeDesdeLaPrimeraVentana() {
        assertEquals(asumido(24), resolver(emptyList(), listOf(enHora(24)), emptyList(), enHora(30)))
    }

    @Test // CA-27
    fun confirmarUnLlenadoDespuesDeLaVentanaReemplazaAlAsumido() {
        val vigente = resolver(listOf(real(0), real(26, TipoLlenado.MITAD)), listOf(enHora(24)), emptyList(), enHora(30))
        assertEquals(real(26, TipoLlenado.MITAD), vigente)
    }

    @Test // CA-25
    fun sinLlegadaDescartaElLlenadoAsumidoDeEsaVentana() {
        val vigente = resolver(listOf(real(0)), listOf(enHora(24)), listOf(enHora(27)), enHora(30))
        assertEquals(real(0), vigente)
    }

    @Test // CA-25
    fun sinLlegadaSoloDescartaLaVentanaQueCorresponde() {
        val ventanas = listOf(enHora(24), enHora(48))
        val vigente = resolver(listOf(real(0)), ventanas, listOf(enHora(50)), enHora(60))
        assertEquals(asumido(24), vigente)
    }

    @Test // CA-34
    fun sinCronogramaNoSeAsumeNada() {
        assertEquals(real(0), resolver(listOf(real(0)), emptyList(), emptyList(), enHora(100)))
    }

    @Test // CA-34
    fun sinCronogramaNiLlenadosNoHayNadaQueProyectar() {
        assertNull(resolver(emptyList(), emptyList(), emptyList(), enHora(100)))
    }

    @Test
    fun unAbastecimientoFuturoNoSeAsume() {
        assertEquals(real(0), resolver(listOf(real(0)), listOf(enHora(48)), emptyList(), enHora(30)))
    }

    @Test
    fun unLlenadoRealEnElMismoInstanteQueLaVentanaLaCubre() {
        assertEquals(real(24), resolver(listOf(real(24)), listOf(enHora(24)), emptyList(), enHora(30)))
    }
}
