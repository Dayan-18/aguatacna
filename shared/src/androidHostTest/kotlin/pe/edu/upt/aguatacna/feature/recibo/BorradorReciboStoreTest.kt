package pe.edu.upt.aguatacna.feature.recibo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import pe.edu.upt.aguatacna.feature.recibo.data.BorradorReciboStore
import pe.edu.upt.aguatacna.feature.recibo.data.FakeReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.data.local.ReciboBorradorDao
import pe.edu.upt.aguatacna.feature.recibo.data.local.ReciboBorradorEntity
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TipoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ConfirmarReciboUseCase
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private class FakeReciboBorradorDao : ReciboBorradorDao {
    @Volatile var fila: ReciboBorradorEntity? = null

    override suspend fun obtener(): ReciboBorradorEntity? = fila
    override suspend fun guardar(borrador: ReciboBorradorEntity) {
        fila = borrador
    }
    override suspend fun borrar() {
        fila = null
    }
}

class BorradorReciboStoreTest {
    private val dao = FakeReciboBorradorDao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val borrador = ReciboBorrador(
        periodoConsumo = Campo(PeriodoConsumo(2026, 8), 0.95f),
        consumoM3 = Campo(23, 0.5f),
        importeTotal = Campo(Dinero(7829L), 1f, corregidoPorUsuario = true),
        tipoConsumo = Campo(TipoConsumo.PROMEDIO, 0.9f),
        origen = OrigenDatos.ESCANEADO,
        idRecibo = "recibo-1"
    )

    @AfterTest
    fun cerrar() = scope.cancel()

    private suspend fun esperarHasta(condicion: () -> Boolean) = withTimeout(2_000) {
        while (!condicion()) delay(10)
    }

    @Test
    fun elBorradorSeGuardaSeRestauraYSeBorraAlConfirmar() = runBlocking {
        val store = BorradorReciboStore(dao, scope)
        store.guardar(borrador)
        esperarHasta { dao.fila != null }

        // Otro store con el mismo DAO, como tras reabrir la app.
        val restaurado = BorradorReciboStore(dao, scope)
        restaurado.asegurar { ReciboBorrador() }
        assertEquals(borrador, restaurado.borrador.value)

        // Lo que hace RevisionViewModel al confirmar.
        ConfirmarReciboUseCase(FakeReciboRepository())(restaurado.borrador.value!!)
        restaurado.limpiar()
        esperarHasta { dao.fila == null }
        assertNull(dao.fila)
    }

    @Test
    fun sinDaoFuncionaSoloEnMemoria() = runBlocking {
        val store = BorradorReciboStore(scope = scope)
        store.asegurar { ReciboBorrador.vacio(PeriodoConsumo(2026, 9)) }
        assertEquals(PeriodoConsumo(2026, 9), store.borrador.value?.periodoConsumo?.valor)
    }
}
