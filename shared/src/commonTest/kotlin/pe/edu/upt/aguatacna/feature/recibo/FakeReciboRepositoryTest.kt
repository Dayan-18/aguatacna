package pe.edu.upt.aguatacna.feature.recibo

import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.feature.recibo.data.FakeReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ObservarResumenUseCase
import pe.edu.upt.aguatacna.feature.reserva.ejecutar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeReciboRepositoryTest {

    @Test
    fun upsertReemplazaReciboDelMismoPeriodoConservandoSuId() {
        val repo = FakeReciboRepository()
        val periodo = PeriodoConsumo(2026, 8)

        ejecutar { repo.guardar(Recibo("1", periodo, 20, Dinero(5000L))) }
        ejecutar { repo.guardar(Recibo("2", periodo, 33, Dinero(7420L))) }

        val lista = ejecutar { repo.observarRecibos().first() }
        assertEquals(1, lista.size)
        assertEquals("1", lista[0].id)
        assertEquals(33, lista[0].consumoM3)
    }

    @Test
    fun moverUnReciboDeMesNoDejaDosConElMismoId() {
        val repo = FakeReciboRepository()
        ejecutar { repo.guardar(Recibo("1", PeriodoConsumo(2026, 8), 20, Dinero(5000L))) }
        ejecutar { repo.guardar(Recibo("1", PeriodoConsumo(2026, 9), 20, Dinero(5000L))) }

        val lista = ejecutar { repo.observarRecibos().first() }
        assertEquals(1, lista.size)
        assertEquals(PeriodoConsumo(2026, 9), lista[0].periodoConsumo)
    }

    @Test
    fun ordenarDescendentePorPeriodo() {
        val repo = FakeReciboRepository()
        ejecutar {
            repo.guardar(Recibo("1", PeriodoConsumo(2026, 7), 16, Dinero(4000L)))
            repo.guardar(Recibo("2", PeriodoConsumo(2026, 8), 33, Dinero(7420L)))
            repo.guardar(Recibo("3", PeriodoConsumo(2026, 6), 15, Dinero(3800L)))
        }

        val lista = ejecutar { repo.observarRecibos().first() }
        assertEquals(
            listOf(PeriodoConsumo(2026, 8), PeriodoConsumo(2026, 7), PeriodoConsumo(2026, 6)),
            lista.map { it.periodoConsumo }
        )
    }

    @Test
    fun resumenEvaluaElUltimoReciboConLaReglaDe100M3() {
        val repo = FakeReciboRepository()
        val observarResumen = ObservarResumenUseCase(repo)
        assertNull(ejecutar { observarResumen().first() })

        repo.cargarTodos(SemillaRecibos.generarRecibos())

        val resumen = assertNotNull(ejecutar { observarResumen().first() })
        assertEquals("Agosto 2026", resumen.recibo.periodoConsumo.displayCompleto)
        assertEquals("11 Set 2026", resumen.fechaVencimiento)
        assertEquals(16, resumen.promedioHistorico)
        assertEquals(EstadoConsumo.ALTO_CONSUMO, resumen.estadoConsumo)
    }

    @Test
    fun elVencimientoEsSiempreEl11DelMesSiguiente() {
        val repo = FakeReciboRepository()
        // Aunque el recibo no tenga fecha de vencimiento (o tenga otra), se muestra el 11 del mes siguiente.
        ejecutar { repo.guardar(Recibo("1", PeriodoConsumo(2026, 9), 33, Dinero(7420L))) }

        val resumen = assertNotNull(ejecutar { ObservarResumenUseCase(repo)().first() })
        assertEquals("11 Oct 2026", resumen.fechaVencimiento)
        assertEquals(EstadoConsumo.NORMAL, resumen.estadoConsumo)
        assertNull(resumen.promedioHistorico)
    }

    @Test
    fun elVencimientoDeDiciembreEsEnEneroDelAnioSiguiente() {
        assertEquals(LocalDate(2027, 1, 11), PeriodoConsumo(2026, 12).vencimiento())
    }

    @Test
    fun confirmarNoInventaDatosQueFaltan() {
        val borrador = ReciboBorrador(
            periodoConsumo = Campo(PeriodoConsumo(2026, 8), 0.9f),
            consumoM3 = Campo(33, 0.9f),
            importeTotal = Campo(Dinero(7420L), 0.95f)
        )

        val recibo = borrador.confirmar("r-1")
        assertNull(recibo.fechaEmision)
        assertNull(recibo.fechaVencimiento)
        assertNull(recibo.numeroMedidor)
    }

    @Test
    fun sinPeriodoElBorradorNoEsConfirmable() {
        val borrador = ReciboBorrador(consumoM3 = Campo(33, 1f), importeTotal = Campo(Dinero(7420L), 1f))
        assertTrue(!borrador.esConfirmable)
        assertTrue(ReciboBorrador.vacio(PeriodoConsumo(2026, 8)).periodoConsumo.valor != null)
    }
}
