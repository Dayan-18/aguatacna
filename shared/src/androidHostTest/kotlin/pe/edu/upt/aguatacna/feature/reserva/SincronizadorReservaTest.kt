package pe.edu.upt.aguatacna.feature.reserva

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import pe.edu.upt.aguatacna.feature.reserva.data.local.EventoLlenadoEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.NovedadReservaEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.PerfilHogarEntity
import pe.edu.upt.aguatacna.feature.reserva.data.sync.DatosDeReserva
import pe.edu.upt.aguatacna.feature.reserva.data.sync.NubeReserva
import pe.edu.upt.aguatacna.feature.reserva.data.sync.SincronizadorReserva
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class NubeEnMemoria(var datos: DatosDeReserva? = DatosDeReserva(null, null, emptyList(), emptyList())) : NubeReserva {
    var fallar = false
    var subidas = 0

    override suspend fun descargar(): DatosDeReserva? {
        if (fallar) error("sin red")
        return datos
    }

    override suspend fun subir(
        perfil: PerfilHogarEntity?,
        sectorId: String?,
        llenados: List<EventoLlenadoEntity>,
        novedades: List<NovedadReservaEntity>
    ) {
        subidas++
        val actual = datos ?: return
        datos = DatosDeReserva(
            perfil = perfil ?: actual.perfil,
            sectorId = if (perfil != null) sectorId else actual.sectorId,
            llenados = actual.llenados + llenados,
            novedades = actual.novedades + novedades
        )
    }
}

class SincronizadorReservaTest {
    private val dao = FakeReservaDao()
    private val nube = NubeEnMemoria()
    private val sector = MutableStateFlow<String?>(null)
    private val sincronizador = SincronizadorReserva(dao, "local-1", nube, flowOf(true), sector) { sector.value = it }

    private fun perfil(usuario: String, habitantes: Int = 4) =
        PerfilHogarEntity(usuario, "TANQUE_ELEVADO", 1000.0, habitantes, 2, true, false, 48.0, null)

    private fun llenado(id: String) = EventoLlenadoEntity(id, "local-1", "2026-09-19T05:15", "LLENADO")

    private fun novedad(id: String) = NovedadReservaEntity(id, "local-1", "2026-09-19T08:00", NovedadReservaEntity.SIN_LLEGADA)

    @Test
    fun subeAlaNubeLoQueSoloEstaEnElTelefono() = runBlocking {
        dao.perfil.value = perfil("local-1")
        dao.llenados.value = listOf(llenado("l-1"))
        dao.novedades.value = listOf(novedad("n-1"))

        assertTrue(sincronizador.sincronizar())

        val enNube = nube.datos!!
        assertEquals("local-1", enNube.perfil?.usuarioId)
        assertEquals(listOf("l-1"), enNube.llenados.map { it.id })
        assertEquals(listOf("n-1"), enNube.novedades.map { it.id })
    }

    @Test
    fun noVuelveASubirLoQueLaNubeYaTiene() = runBlocking {
        dao.llenados.value = listOf(llenado("l-1"))
        sincronizador.sincronizar()
        sincronizador.sincronizar()
        assertEquals(1, nube.datos!!.llenados.size)
    }

    @Test
    fun bajaAlTelefonoLoQueSoloEstaEnLaNubeConElUsuarioLocal() = runBlocking {
        nube.datos = DatosDeReserva(
            perfil("cuenta-9", habitantes = 6),
            null,
            listOf(EventoLlenadoEntity("l-9", "cuenta-9", "2026-09-18T05:00", "LLENADO")),
            listOf(NovedadReservaEntity("n-9", "cuenta-9", "2026-09-18T08:00", NovedadReservaEntity.SIN_AGUA))
        )

        sincronizador.sincronizar()

        assertEquals("local-1", dao.perfil.value?.usuarioId)
        assertEquals(6, dao.perfil.value?.habitantes)
        assertEquals("local-1", dao.llenados.first().single().usuarioId)
        assertEquals("local-1", dao.novedades.first().single().usuarioId)
    }

    @Test
    fun elPerfilDelTelefonoNoLoPisaElDeLaNube() = runBlocking {
        dao.perfil.value = perfil("local-1", habitantes = 4)
        nube.datos = DatosDeReserva(perfil("cuenta-9", habitantes = 9), null, emptyList(), emptyList())

        sincronizador.sincronizar()

        assertEquals(4, dao.perfil.value?.habitantes)
        assertEquals(4, nube.datos!!.perfil?.habitantes)
    }

    @Test
    fun elSectorDelTelefonoSubeConElPerfil() = runBlocking {
        dao.perfil.value = perfil("local-1")
        sector.value = "AA-02"
        sincronizador.sincronizar()
        assertEquals("AA-02", nube.datos!!.sectorId)
    }

    @Test
    fun enUnTelefonoSinDomicilioSeBajaElSectorDeLaNube() = runBlocking {
        nube.datos = DatosDeReserva(perfil("cuenta-9"), "GA-07", emptyList(), emptyList())
        sincronizador.sincronizar()
        assertEquals("GA-07", sector.value)
    }

    @Test
    fun elSectorDelTelefonoNoLoPisaElDeLaNube() = runBlocking {
        dao.perfil.value = perfil("local-1")
        sector.value = "AA-02"
        nube.datos = DatosDeReserva(perfil("cuenta-9"), "GA-07", emptyList(), emptyList())
        sincronizador.sincronizar()
        assertEquals("AA-02", sector.value)
        assertEquals("AA-02", nube.datos!!.sectorId)
    }

    @Test
    fun sinSesionNoHaceNada() = runBlocking {
        nube.datos = null
        dao.llenados.value = listOf(llenado("l-1"))
        assertFalse(sincronizador.sincronizar())
        assertEquals(0, nube.subidas)
    }

    @Test
    fun siLaRedFallaNoRompeYLoLocalQuedaIntacto() = runBlocking {
        nube.fallar = true
        dao.llenados.value = listOf(llenado("l-1"))
        assertFalse(sincronizador.sincronizar())
        assertEquals(1, dao.llenados.first().size)
        assertNull(dao.perfil.value)
    }
}
