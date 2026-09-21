package pe.edu.upt.aguatacna.feature.reserva

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import pe.edu.upt.aguatacna.feature.reserva.presentation.describirMomento
import pe.edu.upt.aguatacna.feature.reserva.presentation.formatearDuracion
import pe.edu.upt.aguatacna.feature.reserva.presentation.formatearHora
import pe.edu.upt.aguatacna.feature.reserva.presentation.formatearMiles
import pe.edu.upt.aguatacna.feature.reserva.presentation.textoDeInsignia
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FormatoReservaTest {
    private val ahora = LocalDateTime(2026, 9, 19, 15, 0)

    @Test
    fun formateaLaHoraEnDoceHoras() {
        assertEquals("6:40 p.m.", formatearHora(LocalTime(18, 40)))
        assertEquals("5:00 a.m.", formatearHora(LocalTime(5, 0)))
        assertEquals("12:05 a.m.", formatearHora(LocalTime(0, 5)))
        assertEquals("12:30 p.m.", formatearHora(LocalTime(12, 30)))
    }

    @Test
    fun formateaLaDuracion() {
        assertEquals("10 h 20 min", formatearDuracion(10.0 + 20.0 / 60))
        assertEquals("3 h", formatearDuracion(3.0))
        assertEquals("45 min", formatearDuracion(0.75))
    }

    @Test
    fun describeHoyYMananaComoElFigma() {
        assertEquals("hoy 6:40 p.m.", describirMomento(LocalDateTime(2026, 9, 19, 18, 40), ahora))
        assertEquals("mañana 5:00 a.m.", describirMomento(LocalDateTime(2026, 9, 20, 5, 0), ahora))
    }

    @Test
    fun describeUnDiaLejanoConLaFecha() {
        assertEquals("el 23/9 5:00 a.m.", describirMomento(LocalDateTime(2026, 9, 23, 5, 0), ahora))
    }

    @Test
    fun redondeaAlMinutoMasCercano() {
        // 18:39:53 se muestra como 6:40 p.m., igual que el Figma.
        assertEquals("hoy 6:40 p.m.", describirMomento(LocalDateTime(2026, 9, 19, 18, 39, 53), ahora))
    }

    @Test
    fun elRedondeoPuedeCambiarDeDia() {
        assertEquals("mañana 12:00 a.m.", describirMomento(LocalDateTime(2026, 9, 19, 23, 59, 40), ahora))
    }

    @Test
    fun separaLosMilesConEspacio() {
        assertEquals("1 100", formatearMiles(1100))
        assertEquals("680", formatearMiles(680))
        assertEquals("12 345", formatearMiles(12345))
    }

    @Test
    fun laInsigniaNoApareceSinAvisosSinLeer() {
        assertNull(textoDeInsignia(0))
        assertNull(textoDeInsignia(-1))
    }

    @Test
    fun laInsigniaMuestraLaCantidadHastaNueveYDespuesMasNueve() {
        assertEquals("1", textoDeInsignia(1))
        assertEquals("9", textoDeInsignia(9))
        assertEquals("9+", textoDeInsignia(10))
        assertEquals("9+", textoDeInsignia(50))
    }
}
