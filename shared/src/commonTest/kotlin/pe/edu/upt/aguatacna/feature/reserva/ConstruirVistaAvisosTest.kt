package pe.edu.upt.aguatacna.feature.reserva

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Aviso
import pe.edu.upt.aguatacna.feature.reserva.domain.model.AvisoGuardado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Deficit
import pe.edu.upt.aguatacna.feature.reserva.presentation.ConstruirVistaAvisos
import pe.edu.upt.aguatacna.feature.reserva.presentation.DestinoDelAviso
import pe.edu.upt.aguatacna.feature.reserva.presentation.TipoDeAviso
import pe.edu.upt.aguatacna.feature.reserva.presentation.describirCuando
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConstruirVistaAvisosTest {
    private val construir = ConstruirVistaAvisos()
    private val ahora = LocalDateTime(2026, 9, 21, 15, 0)

    private fun guardado(aviso: Aviso, momento: LocalDateTime, leido: Boolean = false) =
        AvisoGuardado("a-1", aviso, momento, leido)

    @Test
    fun elAvisoDeAgotamientoUsaLosTextosDelFigma() {
        val aviso = Aviso.AgotamientoAntesDelAbastecimiento(LocalDateTime(2026, 9, 21, 18, 40), Deficit(10.0 + 20.0 / 60))
        val vista = construir(listOf(guardado(aviso, LocalDateTime(2026, 9, 21, 14, 48))), ahora).single()
        assertEquals(TipoDeAviso.AGOTAMIENTO, vista.tipo)
        assertEquals("Tu reserva se agota antes del próximo abastecimiento", vista.titulo)
        assertEquals("Se acaba a las 6:40 p.m. y te faltarían 10 h 20 min de agua. Toca para ver qué recortar.", vista.texto)
        assertEquals("hace 12 min", vista.cuando)
        assertEquals(DestinoDelAviso.QUE_RECORTAR, vista.destino)
        assertTrue(vista.sinLeer)
    }

    @Test
    fun siSeAgotaOtroDiaLoDiceConEseDia() {
        val aviso = Aviso.AgotamientoAntesDelAbastecimiento(LocalDateTime(2026, 9, 22, 1, 41), Deficit(3.0))
        val vista = construir(listOf(guardado(aviso, LocalDateTime(2026, 9, 21, 14, 0))), ahora).single()
        assertTrue("Se acaba mañana 1:41 a.m." in vista.texto)
    }

    @Test
    fun elAvisoDeConfirmarLlevaAMiReserva() {
        val aviso = Aviso.ConfirmarLlenado(LocalDateTime(2026, 9, 21, 5, 0))
        val vista = construir(listOf(guardado(aviso, LocalDateTime(2026, 9, 21, 5, 15), leido = true)), ahora).single()
        assertEquals("¿Llegó el agua a tu casa?", vista.titulo)
        assertEquals("Confirma con un toque para mantener tu proyección al día.", vista.texto)
        assertEquals("hoy 5:15", vista.cuando)
        assertEquals(DestinoDelAviso.MI_RESERVA, vista.destino)
        assertFalse(vista.sinLeer)
    }

    @Test
    fun conservaElOrdenRecibido() {
        val a = guardado(Aviso.ConfirmarLlenado(LocalDateTime(2026, 9, 21, 5, 0)), LocalDateTime(2026, 9, 21, 5, 15)).copy(id = "a")
        val b = guardado(Aviso.ConfirmarLlenado(LocalDateTime(2026, 9, 20, 5, 0)), LocalDateTime(2026, 9, 20, 5, 15)).copy(id = "b")
        assertEquals(listOf("a", "b"), construir(listOf(a, b), ahora).map { it.id })
    }

    @Test
    fun describeCuandoPasoCadaAviso() {
        assertEquals("ahora", describirCuando(ahora, ahora))
        assertEquals("hace 12 min", describirCuando(LocalDateTime(2026, 9, 21, 14, 48), ahora))
        assertEquals("hace 55 min", describirCuando(LocalDateTime(2026, 9, 21, 14, 5), ahora))
        assertEquals("hoy 13:05", describirCuando(LocalDateTime(2026, 9, 21, 13, 5), ahora))
        assertEquals("hoy 6:30", describirCuando(LocalDateTime(2026, 9, 21, 6, 30), ahora))
        assertEquals("ayer 19:40", describirCuando(LocalDateTime(2026, 9, 20, 19, 40), ahora))
        assertEquals("18/9 8:05", describirCuando(LocalDateTime(2026, 9, 18, 8, 5), ahora))
    }
}
