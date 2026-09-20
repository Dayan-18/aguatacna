package pe.edu.upt.aguatacna.core

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import pe.edu.upt.aguatacna.core.di.QUALIFICADOR_USUARIO
import pe.edu.upt.aguatacna.core.di.iniciarKoin
import pe.edu.upt.aguatacna.data.local.UsuarioDao
import pe.edu.upt.aguatacna.data.local.UsuarioEntity
import pe.edu.upt.aguatacna.feature.reserva.FakeReservaDao
import pe.edu.upt.aguatacna.feature.reserva.data.local.ReservaDao
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.AbastecimientosDelSector
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.ReservaRepository
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class InyeccionTest {

    private val plataformaDePrueba = module {
        single { FakeReservaDao() }
        single<ReservaDao> { get<FakeReservaDao>() }
        single<UsuarioDao> {
            object : UsuarioDao {
                override suspend fun obtener(): UsuarioEntity? = null
                override suspend fun guardar(usuario: UsuarioEntity) = Unit
            }
        }
        single(QUALIFICADOR_USUARIO) { "u-1" }
    }

    @AfterTest
    fun cerrar() = stopKoin()

    @Test
    fun elGrafoResuelveElRepositorioDeReservaYSusDependencias() {
        val koin = iniciarKoin { modules(plataformaDePrueba) }.koin
        assertNotNull(koin.get<ReservaRepository>())
        assertNotNull(koin.get<AbastecimientosDelSector>())
    }

    @Test
    fun elRepositorioInyectadoOperaSobreLaBaseDeLaPlataforma() = runBlocking {
        val koin = iniciarKoin { modules(plataformaDePrueba) }.koin
        assertNull(koin.get<ReservaRepository>().observarPerfil().first())
    }
}
