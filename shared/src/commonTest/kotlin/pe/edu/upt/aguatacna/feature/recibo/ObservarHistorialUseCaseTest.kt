package pe.edu.upt.aguatacna.feature.recibo

import kotlinx.coroutines.flow.first
import pe.edu.upt.aguatacna.feature.recibo.data.FakeReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ObservarHistorialUseCase
import pe.edu.upt.aguatacna.feature.reserva.ejecutar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ObservarHistorialUseCaseTest {

    @Test
    fun ventanaDe6MesesMarcaElAltoConsumo() {
        val repo = FakeReciboRepository()
        val useCase = ObservarHistorialUseCase(repo)
        repo.cargarTodos(SemillaRecibos.generarRecibos())

        val historial = assertNotNull(ejecutar { useCase().first() })

        assertEquals(6, historial.ventana6Meses.size)
        assertEquals("Mar", historial.ventana6Meses[0].mesCorto)
        assertEquals("Ago", historial.ventana6Meses[5].mesCorto)

        val ago = historial.ventana6Meses[5]
        assertEquals(120, ago.consumoM3)
        assertTrue(ago.esAtipico)
        assertIs<EstadoConsumo.Atipico>(ago.estado)

        val jul = historial.ventana6Meses[4]
        assertFalse(jul.esAtipico)
        assertIs<EstadoConsumo.Normal>(jul.estado)

        // Promedio de marzo a julio: (16+16+15+17+16)/5 = 16
        assertEquals(16, historial.promedioHistorico)
        assertEquals(PeriodoConsumo(2026, 8), historial.mesSeleccionado)
    }

    @Test
    fun mesSinDatoMuestraCasillaVacia() {
        val repo = FakeReciboRepository()
        val useCase = ObservarHistorialUseCase(repo)
        ejecutar {
            repo.guardar(Recibo("1", PeriodoConsumo(2026, 6), 15, Dinero(3800L)))
            repo.guardar(Recibo("2", PeriodoConsumo(2026, 8), 20, Dinero(5000L)))
        }

        val historial = assertNotNull(ejecutar { useCase().first() })

        val slotJulio = assertNotNull(historial.ventana6Meses.find { it.periodo == PeriodoConsumo(2026, 7) })
        assertNull(slotJulio.consumoM3)
        assertNull(slotJulio.estado)
        assertFalse(slotJulio.esAtipico)
    }

    @Test
    fun laVentanaTerminaEnElReciboMasRecienteAunqueHayaUnoMuyAntiguo() {
        val repo = FakeReciboRepository()
        val useCase = ObservarHistorialUseCase(repo)
        ejecutar {
            repo.guardar(Recibo("1", PeriodoConsumo(2026, 1), 15, Dinero(3800L)))
            repo.guardar(Recibo("2", PeriodoConsumo(2026, 3), 20, Dinero(5000L)))
        }

        val historial = assertNotNull(ejecutar { useCase().first() })

        // Antes empezaba en enero e incluía abril, mayo y junio, que aún no llegan.
        assertEquals(PeriodoConsumo(2025, 10), historial.ventana6Meses.first().periodo)
        assertEquals(PeriodoConsumo(2026, 3), historial.ventana6Meses.last().periodo)
    }

    @Test
    fun historialVacioDevuelveNull() {
        val useCase = ObservarHistorialUseCase(FakeReciboRepository())
        assertNull(ejecutar { useCase().first() })
    }
}
