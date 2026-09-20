package pe.edu.upt.aguatacna.feature.reserva

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import pe.edu.upt.aguatacna.feature.reserva.presentation.describirMomento
import pe.edu.upt.aguatacna.feature.reserva.presentation.formatearDuracion
import pe.edu.upt.aguatacna.feature.reserva.presentation.formatearHora
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
