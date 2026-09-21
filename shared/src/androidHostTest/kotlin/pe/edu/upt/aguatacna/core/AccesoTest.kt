package pe.edu.upt.aguatacna.core

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import pe.edu.upt.aguatacna.core.sesion.InicioConGoogleNoDisponible
import pe.edu.upt.aguatacna.core.sesion.ModoDeAcceso
import pe.edu.upt.aguatacna.core.sesion.RegistroDeAccesoEnMemoria
import pe.edu.upt.aguatacna.core.sesion.RegistroDeAccesoEnRoom
import pe.edu.upt.aguatacna.core.sesion.ResultadoInicio
import pe.edu.upt.aguatacna.data.local.UsuarioDao
import pe.edu.upt.aguatacna.data.local.UsuarioEntity
import pe.edu.upt.aguatacna.feature.bienvenida.presentation.AccesoUiState
import pe.edu.upt.aguatacna.feature.bienvenida.presentation.AccesoViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class FakeUsuarioDao : UsuarioDao {
    val modo = MutableStateFlow<String?>(null)
    override suspend fun obtener(): UsuarioEntity? = null
    override suspend fun guardar(usuario: UsuarioEntity) = Unit
    override fun observarModoDeAcceso(): Flow<String?> = modo
    override suspend fun guardarModoDeAcceso(modo: String) { this.modo.value = modo }
}

class AccesoTest {
    @Test
    fun alPrincipioNoSeHaElegidoNingunModo() = runBlocking {
        assertNull(RegistroDeAccesoEnMemoria().observar().first())
    }

    @Test
    fun elModoElegidoSeRecuerda() = runBlocking {
        val registro = RegistroDeAccesoEnMemoria()
        registro.guardar(ModoDeAcceso.SIN_CUENTA)
        assertEquals(ModoDeAcceso.SIN_CUENTA, registro.observar().first())
    }

    @Test
    fun enRoomSeGuardaPorNombreYSeLeeComoModo() = runBlocking {
        val dao = FakeUsuarioDao()
        val registro = RegistroDeAccesoEnRoom(dao)
        assertNull(registro.observar().first())
        registro.guardar(ModoDeAcceso.GOOGLE)
        assertEquals("GOOGLE", dao.modo.value)
        assertEquals(ModoDeAcceso.GOOGLE, registro.observar().first())
    }

    @Test
    fun unValorDesconocidoEnLaBaseSeTrataComoSinElegir() = runBlocking {
        val dao = FakeUsuarioDao().apply { modo.value = "OTRO" }
        assertNull(RegistroDeAccesoEnRoom(dao).observar().first())
    }

    @Test
    fun sinConsentimientoNoSePuedeEntrar() {
        assertTrue(AccesoUiState().puedeEntrar)
        assertFalse(AccesoUiState(consentimiento = false).puedeEntrar)
        assertFalse(AccesoUiState(enCurso = true).puedeEntrar)
    }

    @Test
    fun googleSinImplementacionResponderNoDisponible() = runBlocking {
        assertEquals(ResultadoInicio.NoDisponible, InicioConGoogleNoDisponible.iniciar())
    }

    @Test
    fun losMensajesDeErrorSoloAparecenCuandoHaceFalta() {
        assertNull(AccesoViewModel.mensajeDe(ResultadoInicio.Exitoso))
        assertNull(AccesoViewModel.mensajeDe(ResultadoInicio.Cancelado))
        assertTrue(AccesoViewModel.mensajeDe(ResultadoInicio.NoDisponible)!!.contains("sin cuenta"))
        assertTrue(AccesoViewModel.mensajeDe(ResultadoInicio.SinCuentas)!!.contains("cuenta de Google"))
        assertTrue(AccesoViewModel.mensajeDe(ResultadoInicio.Fallido("x"))!!.contains("No pudimos"))
    }
}

class HashDelNonceTest {
    @Test
    fun elHashEsSha256EnHexadecimal() {
        assertEquals(
            "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            pe.edu.upt.aguatacna.core.sesion.InicioConGoogleAndroid.hashDelNonce("abc")
        )
    }
}
