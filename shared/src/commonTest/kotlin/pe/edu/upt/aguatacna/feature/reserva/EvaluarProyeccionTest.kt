package pe.edu.upt.aguatacna.feature.reserva

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EstadoProyeccion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.EvaluarProyeccion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EvaluarProyeccionTest {
    private val evaluar = EvaluarProyeccion()

    // 1000 L a 50 L/h desde la hora 6: se agota en la hora 26.
    private val reserva = Reserva(
        CAPACIDAD_1000, ConsumoHorario(50.0), EventoLlenado(enHora(6), TipoLlenado.COMPLETO)
    )

    @Test // CA-33
    fun conDeficitNoAlcanza() {
        assertEquals(EstadoProyeccion.NO_ALCANZA, evaluar(reserva, enHora(29)))
    }

    @Test // CA-33
    fun conMenosDeDosHorasDeMargenEstaAjustada() {
        assertEquals(EstadoProyeccion.AJUSTADA, evaluar(reserva, enHora(25)))
    }

    @Test // CA-33
    fun seAgotaJustoCuandoVuelveElAguaEstaAjustada() {
        assertEquals(EstadoProyeccion.AJUSTADA, evaluar(reserva, enHora(26)))
    }

    @Test // CA-33
    fun conDosHorasDeMargenYaEstaComoda() {
        assertEquals(EstadoProyeccion.COMODA, evaluar(reserva, enHora(24)))
    }

    @Test // CA-33
    fun conMuchoMargenEstaComoda() {
        assertEquals(EstadoProyeccion.COMODA, evaluar(reserva, enHora(10)))
    }

    @Test // CA-12
    fun sinCronogramaNoHayEstado() {
        assertNull(evaluar(reserva, null))
    }
}
