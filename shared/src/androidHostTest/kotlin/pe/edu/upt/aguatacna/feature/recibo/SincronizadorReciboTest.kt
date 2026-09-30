package pe.edu.upt.aguatacna.feature.recibo

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import pe.edu.upt.aguatacna.feature.recibo.data.local.ReciboEntity
import pe.edu.upt.aguatacna.feature.recibo.data.sync.NubeRecibo
import pe.edu.upt.aguatacna.feature.recibo.data.sync.SincronizadorRecibo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private class NubeReciboEnMemoria(var recibos: List<ReciboEntity>? = emptyList()) : NubeRecibo {
    var fallar = false
    var subidas = 0

    override suspend fun descargar(): List<ReciboEntity>? {
        if (fallar) error("sin red")
        return recibos
    }

    override suspend fun subir(recibos: List<ReciboEntity>) {
        subidas++
        val ids = recibos.map { it.id }.toSet()
        this.recibos = this.recibos.orEmpty().filter { it.id !in ids } + recibos
    }
}

class SincronizadorReciboTest {
    private val dao = FakeReciboDao()
    private val nube = NubeReciboEnMemoria()

    private fun sincronizador(conSesion: Boolean = true) = SincronizadorRecibo(dao, nube, flowOf(conSesion))

    private fun recibo(id: String, mes: Int, consumo: Int = 20) =
        ReciboEntity(id = id, anio = 2026, mes = mes, consumoM3 = consumo, importeCentimos = 5000L)

    private suspend fun locales() = dao.observarTodos().first()

    @Test
    fun sinSesionNoHaceNada() = runBlocking {
        nube.recibos = null
        dao.guardar(recibo("r-8", 8))

        assertFalse(sincronizador(conSesion = false).sincronizar())

        assertEquals(0, nube.subidas)
    }

    @Test
    fun unTelefonoVacioRestauraLosRecibosDeLaNube() = runBlocking {
        nube.recibos = listOf(recibo("r-7", 7), recibo("r-8", 8))

        assertTrue(sincronizador().sincronizar())

        assertEquals(setOf("r-7", "r-8"), locales().map { it.id }.toSet())
        assertEquals(0, nube.subidas)
    }

    @Test
    fun loLocalSeSubeALaNube() = runBlocking {
        dao.guardar(recibo("r-8", 8, consumo = 33))

        sincronizador().sincronizar()

        assertEquals(listOf(33), nube.recibos!!.map { it.consumoM3 })
    }

    @Test
    fun noDuplicaUnPeriodoQueYaExisteEnElTelefono() = runBlocking {
        dao.guardar(recibo("local-8", 8, consumo = 33))
        nube.recibos = listOf(recibo("otro-telefono-8", 8, consumo = 40))

        sincronizador().sincronizar()

        assertEquals(listOf("local-8"), locales().map { it.id })
    }

    @Test
    fun unErrorDeRedDevuelveFalseSinRomper() = runBlocking {
        nube.fallar = true
        assertFalse(sincronizador().sincronizar())
    }
}
