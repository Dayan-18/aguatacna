package pe.edu.upt.aguatacna.feature.reserva

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.CapacidadLitros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.HabitosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Recomendacion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.presentation.ConstruirVistaRecortes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConstruirVistaRecortesTest {
    private val construir = ConstruirVistaRecortes()

    // El hogar del Figma: 1 100 L a 82 L/h, llenado a las 5:15; vuelve el agua mañana a las 5:00.
    private val reserva = Reserva(
        CapacidadLitros.deLitros(1100.0),
        ConsumoHorario(82.0),
        EventoLlenado(LocalDateTime(2026, 9, 19, 5, 15), TipoLlenado.COMPLETO)
    )
    private val ahora = LocalDateTime(2026, 9, 19, 15, 0)
    private val manana5 = LocalDateTime(2026, 9, 20, 5, 0)
    private val habitos = HabitosDelHogar(2, usaLavadora = true, riegaJardin = true)

    @Test
    fun conDeficitMuestraLasOpcionesTodasMarcadas() {
        val vista = assertNotNull(construir(reserva, habitos, manana5, emptySet(), ahora))
        assertEquals("10 h 20 min", vista.textoDeficit)
        assertEquals("hoy 6:40 p.m.", vista.textoAgotamiento)
        assertEquals("mañana 5:00 a.m.", vista.textoVuelveElAgua)
        assertEquals(4, vista.opciones.size)
        assertTrue(vista.opciones.all { it.elegida })
        assertEquals(275, vista.ahorroLitros)
        assertEquals("3 h 21 min", vista.textoGanas)
        assertFalse(vista.cubreElDeficit)
    }

    @Test
    fun desmarcarUnaOpcionBajaElAhorro() {
        val vista = assertNotNull(construir(reserva, habitos, manana5, setOf(Recomendacion.POSTERGAR_LAVADO), ahora))
        assertEquals(185, vista.ahorroLitros)
        assertFalse(vista.opciones.first { it.recomendacion == Recomendacion.POSTERGAR_LAVADO }.elegida)
    }

    @Test // CA-32
    fun soloAparecenLasOpcionesQueCorrespondenAlosHabitos() {
        val vista = assertNotNull(construir(reserva, habitos.copy(riegaJardin = false), manana5, emptySet(), ahora))
        assertFalse(vista.opciones.any { it.recomendacion == Recomendacion.NO_REGAR })
    }

    @Test // CA-21
    fun sinDeficitNoHayVista() {
        assertNull(construir(reserva.copy(consumo = ConsumoHorario(20.0)), habitos, manana5, emptySet(), ahora))
    }

    @Test // CA-12
    fun sinCronogramaNoHayVista() {
        assertNull(construir(reserva, habitos, null, emptySet(), ahora))
    }

    @Test
    fun siLoElegidoCubreElDeficitLoDice() {
        // Un déficit pequeño: se agota poco antes de que vuelva el agua.
        val casiAlcanza = reserva.copy(consumo = ConsumoHorario(60.0))
        val vista = assertNotNull(construir(casiAlcanza, habitos, LocalDateTime(2026, 9, 20, 0, 0), emptySet(), ahora))
        assertTrue(vista.cubreElDeficit)
        assertEquals("Ya te alcanza", vista.textoFaltan)
    }
}
