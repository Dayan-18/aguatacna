package pe.edu.upt.aguatacna.feature.recibo

import kotlinx.coroutines.flow.first
import pe.edu.upt.aguatacna.feature.recibo.data.ReciboRepositoryEnMemoria
import pe.edu.upt.aguatacna.feature.recibo.data.SemillaDepuracion
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ObservarHistorialUseCase
import pe.edu.upt.aguatacna.feature.reserva.ejecutar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ObservarHistorialUseCaseTest {

    @Test
    fun ventanaDe6MesesCalculaBarrasYUmbral() {
        val repo = ReciboRepositoryEnMemoria()
        val useCase = ObservarHistorialUseCase(repo)

        // Cargar 6 meses de semilla: Mar=16, Abr=16, May=15, Jun=17, Jul=16, Ago=33
        ejecutar { repo.cargarTodos(SemillaDepuracion.generarRecibos()) }

        val historial = assertNotNull(ejecutar { useCase().first() })

        // Verificar ventana continua de 6 meses (T-10.2)
        assertEquals(6, historial.ventana6Meses.size)
        assertEquals("Mar", historial.ventana6Meses[0].mesCorto)
        assertEquals("Ago", historial.ventana6Meses[5].mesCorto)

        // Verificar Agosto atípico y Julio normal (T-10.2)
        val ago = historial.ventana6Meses[5]
        assertEquals(33, ago.consumoM3)
        assertTrue(ago.esAtipico)
        assertTrue(ago.estado is EstadoConsumo.Atipico)

        val jul = historial.ventana6Meses[4]
        assertEquals(16, jul.consumoM3)
        assertFalse(jul.esAtipico)
        assertTrue(jul.estado is EstadoConsumo.Normal)

        // Verificar promedio y umbral atípico (T-10.3)
        // Promedio de Mar a Jul: (16+16+15+17+16)/5 = 16
        assertEquals(16, historial.promedioHistorico)
        assertEquals(32.0, historial.umbralAtipicoM3)

        // Mes inicialmente seleccionado es el más reciente (Agosto 2026)
        assertEquals(PeriodoConsumo(2026, 8), historial.mesSeleccionado)
        assertTrue(historial.estadoMesSeleccionado is EstadoConsumo.Atipico)
    }

    @Test
    fun mesSinDatoMuestraCasillaVacia() {
        val repo = ReciboRepositoryEnMemoria()
        val useCase = ObservarHistorialUseCase(repo)

        // Solo recibos en Junio y Agosto (Julio faltante)
        val rJun = Recibo("1", PeriodoConsumo(2026, 6), 15, Dinero(3800L))
        val rAgo = Recibo("2", PeriodoConsumo(2026, 8), 20, Dinero(5000L))
        ejecutar {
            repo.guardar(rJun)
            repo.guardar(rAgo)
        }

        val historial = assertNotNull(ejecutar { useCase().first() })

        // La ventana de 6 termina en Agosto: Mar, Abr, May, Jun, Jul, Ago
        assertEquals(6, historial.ventana6Meses.size)
        val slotJulio = historial.ventana6Meses.find { it.periodo == PeriodoConsumo(2026, 7) }
        assertNotNull(slotJulio)
        assertNull(slotJulio.consumoM3) // S7: casilla vacía "—"
        assertFalse(slotJulio.esAtipico)
    }

    @Test
    fun historialVacioDevuelveNull() {
        val repo = ReciboRepositoryEnMemoria()
        val useCase = ObservarHistorialUseCase(repo)

        val historial = ejecutar { useCase().first() }
        assertNull(historial)
    }
}
