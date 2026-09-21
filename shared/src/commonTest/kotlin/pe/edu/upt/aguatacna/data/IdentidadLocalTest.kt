package pe.edu.upt.aguatacna.data

import pe.edu.upt.aguatacna.data.local.UsuarioDao
import pe.edu.upt.aguatacna.data.local.UsuarioEntity
import pe.edu.upt.aguatacna.feature.reserva.ejecutar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class IdentidadLocalTest {

    private class UsuarioDaoEnMemoria : UsuarioDao {
        var guardado: UsuarioEntity? = null
        override suspend fun obtener(): UsuarioEntity? = guardado
        override suspend fun guardar(usuario: UsuarioEntity) {
            guardado = usuario
        }
        override fun observarModoDeAcceso(): kotlinx.coroutines.flow.Flow<String?> = kotlinx.coroutines.flow.flowOf(guardado?.modoAcceso)
        override suspend fun guardarModoDeAcceso(modo: String) {
            guardado = guardado?.copy(modoAcceso = modo)
        }
    }

    @Test
    fun laPrimeraVezCreaElUsuarioConUnUuidLocal() {
        val dao = UsuarioDaoEnMemoria()
        val usuario = ejecutar { IdentidadLocal(dao) { "uuid-1" }.obtenerOCrear() }
        assertEquals("uuid-1", usuario.id)
        assertEquals(usuario, dao.guardado)
        assertNull(usuario.googleSub)
    }

    @Test
    fun elUuidNoCambiaEnLasSiguientesAperturas() {
        val dao = UsuarioDaoEnMemoria()
        ejecutar { IdentidadLocal(dao) { "uuid-1" }.obtenerOCrear() }
        val segunda = ejecutar { IdentidadLocal(dao) { "uuid-2" }.obtenerOCrear() }
        assertEquals("uuid-1", segunda.id)
    }
}
