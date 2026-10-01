package pe.edu.upt.aguatacna.feature.recibo

import kotlinx.coroutines.flow.first
import pe.edu.upt.aguatacna.feature.recibo.data.FakeReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.model.aBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ConfirmarReciboUseCase
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ResultadoConfirmacion
import pe.edu.upt.aguatacna.feature.reserva.ejecutar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ConfirmarReciboUseCaseTest {

    private val repo = FakeReciboRepository()
    private val confirmar = ConfirmarReciboUseCase(repo)
    private val agosto = PeriodoConsumo(2026, 8)

    private fun nuevo(periodo: PeriodoConsumo = agosto, consumo: Int = 20) = ReciboBorrador(
        periodoConsumo = Campo(periodo, 1f),
        consumoM3 = Campo(consumo, 1f),
        importeTotal = Campo(Dinero(5000L), 1f)
    )

    private fun recibos(): List<Recibo> = ejecutar { repo.observarRecibos().first() }

    @Test
    fun unReciboNuevoSeGuarda() {
        val resultado = ejecutar { confirmar(nuevo()) }

        assertIs<ResultadoConfirmacion.Guardado>(resultado)
        assertFalse(resultado.reemplazo)
        assertEquals(1, recibos().size)
    }

    @Test
    fun unBorradorIncompletoNoSeGuarda() {
        val resultado = ejecutar { confirmar(nuevo().copy(importeTotal = Campo())) }

        assertEquals(ResultadoConfirmacion.Incompleto, resultado)
        assertTrue(recibos().isEmpty())
    }

    @Test
    fun editarUnReciboExistenteLoReemplaza() {
        val guardado = ejecutar { confirmar(nuevo()) } as ResultadoConfirmacion.Guardado
        val editado = guardado.recibo.aBorrador().copy(consumoM3 = Campo(25, 1f, corregidoPorUsuario = true))

        val resultado = ejecutar { confirmar(editado) }

        assertIs<ResultadoConfirmacion.Guardado>(resultado)
        assertTrue(resultado.reemplazo)
        assertEquals(listOf(25), recibos().map { it.consumoM3 })
    }

    @Test
    fun otroReciboDelMismoMesEsDuplicado() {
        ejecutar { confirmar(nuevo()) }

        val resultado = ejecutar { confirmar(nuevo(consumo = 99)) }

        assertEquals(ResultadoConfirmacion.Duplicado(agosto), resultado)
        assertEquals(listOf(20), recibos().map { it.consumoM3 })
    }

    @Test
    fun cambiarElPeriodoDeUnReciboEditadoLoMueveSinDuplicarlo() {
        val guardado = ejecutar { confirmar(nuevo()) } as ResultadoConfirmacion.Guardado
        val setiembre = PeriodoConsumo(2026, 9)
        val movido = guardado.recibo.aBorrador().copy(periodoConsumo = Campo(setiembre, 1f, corregidoPorUsuario = true))

        assertIs<ResultadoConfirmacion.Guardado>(ejecutar { confirmar(movido) })

        val lista = recibos()
        assertEquals(1, lista.size)
        assertEquals(setiembre, lista[0].periodoConsumo)
        assertEquals(guardado.recibo.id, lista[0].id)
    }

    @Test
    fun moverUnReciboAUnMesOcupadoEsDuplicado() {
        ejecutar { confirmar(nuevo(periodo = PeriodoConsumo(2026, 7))) }
        val guardado = ejecutar { confirmar(nuevo()) } as ResultadoConfirmacion.Guardado
        val movido = guardado.recibo.aBorrador().copy(periodoConsumo = Campo(PeriodoConsumo(2026, 7), 1f))

        assertIs<ResultadoConfirmacion.Duplicado>(ejecutar { confirmar(movido) })
    }

    @Test
    fun unReciboNuevoNoPisaAUnoQueSeMovioDeMes() {
        val guardado = ejecutar { confirmar(nuevo()) } as ResultadoConfirmacion.Guardado
        val movido = guardado.recibo.aBorrador().copy(periodoConsumo = Campo(PeriodoConsumo(2026, 9), 1f))
        ejecutar { confirmar(movido) }

        assertIs<ResultadoConfirmacion.Guardado>(ejecutar { confirmar(nuevo()) })

        assertEquals(2, recibos().size)
    }
}
