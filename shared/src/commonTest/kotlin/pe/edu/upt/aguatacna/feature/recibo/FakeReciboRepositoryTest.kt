package pe.edu.upt.aguatacna.feature.recibo

import kotlinx.coroutines.flow.first
import pe.edu.upt.aguatacna.feature.recibo.data.FakeReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ConfirmarReciboUseCase
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.CorregirCampoUseCase
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ObservarHistorialUseCase
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ObservarResumenUseCase
import pe.edu.upt.aguatacna.feature.reserva.ejecutar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeReciboRepositoryTest {

    @Test
    fun upsertReemplazaReciboDelMismoPeriodo() {
        val repo = FakeReciboRepository()
        val periodo = PeriodoConsumo(2026, 8)

        val r1 = Recibo("1", periodo, 20, Dinero(5000L))
        val r2 = Recibo("2", periodo, 33, Dinero(7420L))

        ejecutar { repo.guardar(r1) }
        var lista = ejecutar { repo.observarRecibos().first() }
        assertEquals(1, lista.size)
        assertEquals(20, lista[0].consumoM3)

        // Mismo período → debe reemplazar (upsert, S2)
        ejecutar { repo.guardar(r2) }
        lista = ejecutar { repo.observarRecibos().first() }
        assertEquals(1, lista.size)
        assertEquals(33, lista[0].consumoM3)
    }

    @Test
    fun ordenarDescendentePorPeriodo() {
        val repo = FakeReciboRepository()
        val rJul = Recibo("1", PeriodoConsumo(2026, 7), 16, Dinero(4000L))
        val rAgo = Recibo("2", PeriodoConsumo(2026, 8), 33, Dinero(7420L))
        val rJun = Recibo("3", PeriodoConsumo(2026, 6), 15, Dinero(3800L))

        ejecutar {
            repo.guardar(rJul)
            repo.guardar(rAgo)
            repo.guardar(rJun)
        }

        val lista = ejecutar { repo.observarRecibos().first() }
        assertEquals(3, lista.size)
        assertEquals(PeriodoConsumo(2026, 8), lista[0].periodoConsumo)
        assertEquals(PeriodoConsumo(2026, 7), lista[1].periodoConsumo)
        assertEquals(PeriodoConsumo(2026, 6), lista[2].periodoConsumo)
    }

    @Test
    fun casosDeUsoFuncionanCorrectamente() {
        val repo = FakeReciboRepository()
        val observarResumen = ObservarResumenUseCase(repo)
        val observarHistorial = ObservarHistorialUseCase(repo)
        val confirmar = ConfirmarReciboUseCase(repo)
        val corregir = CorregirCampoUseCase()

        // Inicialmente vacío
        assertNull(ejecutar { observarResumen().first() })

        // Crear borrador y verificar esDudoso
        val borrador = ReciboBorrador(
            periodoConsumo = Campo(PeriodoConsumo(2026, 8), 0.9f),
            consumoM3 = Campo(10, 0.5f), // dudoso porque confianza < 0.7f
            importeTotal = Campo(Dinero(7420L), 0.95f)
        )
        assertTrue(borrador.consumoM3.esDudoso)

        // Corregir campo
        val corregido = corregir(borrador, CorregirCampoUseCase.CampoEditable.ConsumoM3(33))
        assertEquals(33, corregido.consumoM3.valor)
        assertTrue(corregido.consumoM3.corregidoPorUsuario)
        assertFalse(corregido.consumoM3.esDudoso)

        // Confirmar
        val reciboConfirmado = corregido.confirmar("recibo-1")
        val reemplazado = ejecutar { confirmar(reciboConfirmado) }
        assertFalse(reemplazado) // Primer recibo, no reemplaza

        // Ahora resumen no es nulo
        val resumen = assertNotNull(ejecutar { observarResumen().first() })
        assertEquals("Agosto 2026", resumen.mes)
        assertEquals(33, resumen.consumoM3)

        // Historial no es nulo
        val historial = assertNotNull(ejecutar { observarHistorial().first() })
        assertEquals(1, historial.barras.size)
    }
}
