package pe.edu.upt.aguatacna.feature.reserva

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.data.FakeReservaRepository
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Aviso
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.OrigenLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.AbastecimientosDelSector
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.Notificador
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.RegistroDeAvisos
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.EvaluarAvisos
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.RecalcularYAvisar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class AvisosTest {
    private val evaluar = EvaluarAvisos()

    // 1000 L a 50 L/h desde la hora 6: se agota en la hora 26.
    private val reserva = Reserva(CAPACIDAD_1000, ConsumoHorario(50.0), EventoLlenado(enHora(6), TipoLlenado.COMPLETO))

    @Test
    fun sinReservaNoHayAviso() {
        assertNull(evaluar(null, enHora(29), enHora(10)))
    }

    @Test
    fun conDeficitYAgotamientoFuturoAvisaDelAgotamiento() {
        val aviso = assertIs<Aviso.AgotamientoAntesDelAbastecimiento>(evaluar(reserva, enHora(29), enHora(10)))
        assertEquals(enHora(26), aviso.agotamiento)
        assertEquals(3.0, aviso.deficit.horas, absoluteTolerance = 0.001)
    }

    @Test
    fun siAlcanzaNoAvisa() {
        assertNull(evaluar(reserva, enHora(20), enHora(10)))
    }

    @Test
    fun sinCronogramaNoAvisa() {
        assertNull(evaluar(reserva, null, enHora(10)))
    }

    @Test
    fun siYaSeAgotoNoAvisaTarde() {
        assertNull(evaluar(reserva, enHora(29), enHora(27)))
    }

    @Test
    fun conLlenadoAsumidoPideConfirmarEnVezDeAvisarElAgotamiento() {
        val asumida = reserva.copy(llenado = EventoLlenado(enHora(6), TipoLlenado.COMPLETO, OrigenLlenado.ASUMIDO))
        val aviso = assertIs<Aviso.ConfirmarLlenado>(evaluar(asumida, enHora(29), enHora(10)))
        assertEquals(enHora(6), aviso.inicioDeLaVentana)
    }

    private class RegistroEnMemoria : RegistroDeAvisos {
        val avisados = mutableSetOf<String>()
        override fun yaSeAviso(clave: String) = clave in avisados
        override fun marcarComoAvisado(clave: String) {
            avisados += clave
        }
    }

    private class NotificadorGrabador : Notificador {
        val mostrados = mutableListOf<Aviso>()
        override fun mostrar(aviso: Aviso) {
            mostrados += aviso
        }
    }

    private class SectorFijo(private val proximo: LocalDateTime?) : AbastecimientosDelSector {
        override suspend fun iniciosHasta(ahora: LocalDateTime) = emptyList<LocalDateTime>()
        override suspend fun proximoDesde(ahora: LocalDateTime) = proximo
        override suspend fun nombreDelSector(): String? = null
    }

    private fun tarea(registro: RegistroEnMemoria, notificador: NotificadorGrabador, proximo: LocalDateTime?): RecalcularYAvisar {
        val repositorio = FakeReservaRepository(perfilDePrueba(), listOf(llenadoDePrueba(6)), emptyList()) { enHora(10) }
        return RecalcularYAvisar(repositorio, SectorFijo(proximo), registro, notificador)
    }

    @Test
    fun laTareaHorariaAvisaUnaSolaVezElMismoAgotamiento() {
        val registro = RegistroEnMemoria()
        val notificador = NotificadorGrabador()
        val tarea = tarea(registro, notificador, enHora(29))
        assertIs<Aviso.AgotamientoAntesDelAbastecimiento>(ejecutar { tarea(enHora(10)) })
        assertNull(ejecutar { tarea(enHora(11)) })
        assertEquals(1, notificador.mostrados.size)
    }

    @Test
    fun laTareaHorariaNoMuestraNadaSiNoHayQueAvisar() {
        val notificador = NotificadorGrabador()
        assertNull(ejecutar { tarea(RegistroEnMemoria(), notificador, enHora(20))(enHora(10)) })
        assertEquals(0, notificador.mostrados.size)
    }
}
