package pe.edu.upt.aguatacna.feature.reserva

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import pe.edu.upt.aguatacna.feature.reserva.data.RegistroDeAvisosEnRoom
import pe.edu.upt.aguatacna.feature.reserva.data.local.AvisoReservaDao
import pe.edu.upt.aguatacna.feature.reserva.data.local.AvisoReservaEntity
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Aviso
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Deficit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** DAO en memoria que imita el índice único de usuario y clave de la tabla. */
private class FakeAvisoReservaDao : AvisoReservaDao {
    val filas = MutableStateFlow<List<AvisoReservaEntity>>(emptyList())

    override suspend fun contar(usuarioId: String, clave: String) =
        filas.value.count { it.usuarioId == usuarioId && it.clave == clave }

    override suspend fun guardar(aviso: AvisoReservaEntity) {
        if (contar(aviso.usuarioId, aviso.clave) == 0) filas.value = filas.value + aviso
    }

    override fun observar(usuarioId: String): Flow<List<AvisoReservaEntity>> =
        filas.map { lista -> lista.filter { it.usuarioId == usuarioId }.sortedByDescending { it.momento } }

    override suspend fun marcarTodosLeidos(usuarioId: String) {
        filas.value = filas.value.map { if (it.usuarioId == usuarioId) it.copy(leido = true) else it }
    }
}

class RegistroDeAvisosEnRoomTest {
    private val dao = FakeAvisoReservaDao()
    private var contador = 0
    private val registro = RegistroDeAvisosEnRoom(dao, "u-1") { "id-${contador++}" }

    private val agotamiento = Aviso.AgotamientoAntesDelAbastecimiento(enHora(26), Deficit(3.0))
    private val confirmar = Aviso.ConfirmarLlenado(enHora(5))

    @Test
    fun unAvisoRegistradoYaNoSeVuelveAAvisar() = runBlocking {
        assertFalse(registro.yaSeAviso(agotamiento.clave))
        registro.registrar(agotamiento, enHora(10))
        assertTrue(registro.yaSeAviso(agotamiento.clave))
    }

    @Test
    fun registrarDosVecesElMismoAvisoNoLoDuplica() = runBlocking {
        registro.registrar(agotamiento, enHora(10))
        registro.registrar(agotamiento, enHora(11))
        assertEquals(1, registro.observar().first().size)
    }

    @Test
    fun laListaSaleConLosMasRecientesPrimeroYSusDatos() = runBlocking {
        registro.registrar(confirmar, enHora(5))
        registro.registrar(agotamiento, enHora(10))
        val lista = registro.observar().first()
        assertEquals(listOf(agotamiento, confirmar), lista.map { it.aviso })
        assertEquals(enHora(10), lista.first().momento)
        assertFalse(lista.first().leido)
    }

    @Test
    fun marcarComoLeidosLosDejaLeidos() = runBlocking {
        registro.registrar(agotamiento, enHora(10))
        registro.marcarTodosComoLeidos()
        assertTrue(registro.observar().first().single().leido)
    }

    @Test
    fun cadaUsuarioSoloVeSusAvisos() = runBlocking {
        registro.registrar(agotamiento, enHora(10))
        val otro = RegistroDeAvisosEnRoom(dao, "u-2") { "otro-${contador++}" }
        assertTrue(otro.observar().first().isEmpty())
        assertFalse(otro.yaSeAviso(agotamiento.clave))
    }
}
