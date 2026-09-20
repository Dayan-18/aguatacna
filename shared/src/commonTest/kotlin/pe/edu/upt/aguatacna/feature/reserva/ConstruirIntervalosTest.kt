package pe.edu.upt.aguatacna.feature.reserva

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ClaseIntervalo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Litros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.OrigenLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.ConstruirIntervalos
import kotlin.test.Test
import kotlin.test.assertEquals

class ConstruirIntervalosTest {
    private val construir = ConstruirIntervalos()

    private fun llenado(hora: Int, tipo: TipoLlenado = TipoLlenado.COMPLETO, origen: OrigenLlenado = OrigenLlenado.REAL) =
        EventoLlenado(enHora(hora), tipo, origen)

    @Test
    fun cadaParDeLlenadosConsecutivosDaUnIntervalo() {
        val intervalos = construir(listOf(llenado(0), llenado(24), llenado(72)), CAPACIDAD_1000)
        assertEquals(listOf(24.0, 48.0), intervalos.map { it.horas })
        assertEquals(ClaseIntervalo.POR_LLENADO, intervalos.first().clase)
    }

    @Test // CA-26
    fun unLlenadoAsumidoNoCuentaComoDato() {
        val eventos = listOf(llenado(0), llenado(24, origen = OrigenLlenado.ASUMIDO), llenado(48))
        assertEquals(listOf(48.0), construir(eventos, CAPACIDAD_1000).map { it.horas })
    }

    @Test
    fun losEventosDesordenadosSeOrdenanPorMomento() {
        val intervalos = construir(listOf(llenado(48), llenado(0), llenado(24)), CAPACIDAD_1000)
        assertEquals(listOf(24.0, 24.0), intervalos.map { it.horas })
    }

    @Test
    fun unLlenadoALaMitadSoloConsumeLaMitad() {
        val intervalos = construir(listOf(llenado(0, TipoLlenado.MITAD), llenado(24)), CAPACIDAD_1000)
        assertEquals(Litros(500.0), intervalos.single().litrosConsumidos)
    }

    @Test
    fun dosLlenadosEnElMismoInstanteNoDanIntervalo() {
        assertEquals(emptyList(), construir(listOf(llenado(5), llenado(5)), CAPACIDAD_1000))
    }

    @Test
    fun conUnSoloLlenadoNoHayIntervalos() {
        assertEquals(emptyList(), construir(listOf(llenado(0)), CAPACIDAD_1000))
    }
}
