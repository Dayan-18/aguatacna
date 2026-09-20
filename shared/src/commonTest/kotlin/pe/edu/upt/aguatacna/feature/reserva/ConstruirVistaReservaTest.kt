package pe.edu.upt.aguatacna.feature.reserva

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.CapacidadLitros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfirmacionEstimacion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EstadoProyeccion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.LitrosPorHabitanteDia
import pe.edu.upt.aguatacna.feature.reserva.domain.model.OrigenLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoReservorio
import pe.edu.upt.aguatacna.feature.reserva.presentation.ContextoDelHogar
import pe.edu.upt.aguatacna.feature.reserva.presentation.saludoPara
import pe.edu.upt.aguatacna.feature.reserva.presentation.ConstruirVistaReserva
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ConstruirVistaReservaTest {
    private val construir = ConstruirVistaReserva()

    // El hogar del Figma: 1 100 L a 82 L/h, llenado a las 5:15 de hoy.
    private val reserva = Reserva(
        CapacidadLitros.deLitros(1100.0),
        ConsumoHorario(82.0),
        EventoLlenado(LocalDateTime(2026, 9, 19, 5, 15), TipoLlenado.COMPLETO)
    )
    private val ahora = LocalDateTime(2026, 9, 19, 15, 0)
    private val manana5 = LocalDateTime(2026, 9, 20, 5, 0)
    private val hogar = ContextoDelHogar(TipoReservorio.TANQUE_ELEVADO, 4, "Ciudad Nueva")

    @Test
    fun muestraElNivelYLaProyeccionDelFigma() {
        val vista = construir(reserva, hogar, manana5, LitrosPorHabitanteDia(275.0), ahora)
        // 9 h 45 min desde el llenado: 1100 - 82 * 9,75 = 300,5 L.
        assertEquals(301, vista.nivelLitros)
        assertEquals(1100, vista.capacidadLitros)
        assertEquals(27, vista.porcentaje)
        assertEquals("Buenas tardes", vista.saludo)
        assertEquals("Ciudad Nueva · 4 personas", vista.subtituloHogar)
        assertEquals("Tanque elevado · último llenado hoy 5:15 a.m.", vista.textoLlenado)
        assertNull(vista.horaLlenadoAsumido)
        assertEquals("hoy 6:40 p.m.", vista.textoAgotamiento)
        assertEquals("mañana 5:00 a.m.", vista.textoVuelveElAgua)
        assertEquals("10 h 20 min", vista.textoDeficit)
        assertEquals(EstadoProyeccion.NO_ALCANZA, vista.estado)
        assertEquals(82, vista.consumoLitrosPorHora)
        assertEquals(275, vista.litrosPorHabitanteDia)
        assertEquals(ConfirmacionEstimacion.CONFIRMADA, vista.confirmacion)
    }

    @Test // CA-13
    fun unLlenadoAsumidoSeMuestraComoAsumido() {
        val asumida = reserva.copy(llenado = EventoLlenado(LocalDateTime(2026, 9, 19, 5, 0), TipoLlenado.COMPLETO, OrigenLlenado.ASUMIDO))
        val vista = construir(asumida, hogar, manana5, null, ahora)
        assertEquals("Tanque elevado · llenado asumido hoy 5:00 a.m.", vista.textoLlenado)
        assertEquals("5:00 a.m.", vista.horaLlenadoAsumido)
        assertEquals(ConfirmacionEstimacion.NO_CONFIRMADA, vista.confirmacion)
    }

    @Test // CA-12
    fun sinCronogramaNoHayDeficitNiEstado() {
        val vista = construir(reserva, hogar, null, null, ahora)
        assertNull(vista.textoVuelveElAgua)
        assertNull(vista.textoDeficit)
        assertNull(vista.estado)
        assertNull(vista.litrosPorHabitanteDia)
    }

    @Test // CA-11
    fun siAlcanzaElDeficitEsCero() {
        val vista = construir(reserva.copy(consumo = ConsumoHorario(30.0)), hogar, manana5, null, ahora)
        assertEquals("0 min", vista.textoDeficit)
        assertEquals(EstadoProyeccion.COMODA, vista.estado)
    }

    @Test
    fun elSaludoDependeDeLaHora() {
        assertEquals("Buenos días", saludoPara(7))
        assertEquals("Buenas tardes", saludoPara(15))
        assertEquals("Buenas noches", saludoPara(21))
    }

    @Test
    fun sinSectorElSubtituloSoloDiceLasPersonas() {
        val vista = construir(reserva, ContextoDelHogar(TipoReservorio.CISTERNA, 1, null), manana5, null, ahora)
        assertEquals("1 persona", vista.subtituloHogar)
        assertEquals("Cisterna · último llenado hoy 5:15 a.m.", vista.textoLlenado)
    }
}
