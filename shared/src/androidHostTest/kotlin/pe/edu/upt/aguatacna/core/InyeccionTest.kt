package pe.edu.upt.aguatacna.core

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
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
import pe.edu.upt.aguatacna.feature.sector.data.local.ConfirmacionHorarioEntity
import pe.edu.upt.aguatacna.feature.sector.data.local.CronogramaEntity
import pe.edu.upt.aguatacna.feature.sector.data.local.PuntoCisternaEntity
import pe.edu.upt.aguatacna.feature.sector.data.local.SectorDao
import pe.edu.upt.aguatacna.feature.sector.data.local.SectorEntity
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
                override fun observarModoDeAcceso(): kotlinx.coroutines.flow.Flow<String?> = kotlinx.coroutines.flow.flowOf(null)
                override suspend fun guardarModoDeAcceso(modo: String) = Unit
                override fun observarSector(): kotlinx.coroutines.flow.Flow<String?> = kotlinx.coroutines.flow.flowOf(null)
                override suspend fun guardarSector(sectorId: String) = Unit
            }
        }
        single<SectorDao> {
            object : SectorDao {
                override suspend fun sectores(): List<SectorEntity> = emptyList()
                override suspend fun cronogramas(sectorId: String): List<CronogramaEntity> = emptyList()
                override suspend fun puntosCisterna(sectorId: String): List<PuntoCisternaEntity> = emptyList()
                override suspend fun confirmaciones(sectorId: String): List<ConfirmacionHorarioEntity> = emptyList()
                override suspend fun guardarSectores(sectores: List<SectorEntity>) = Unit
                override suspend fun guardarCronogramas(cronogramas: List<CronogramaEntity>) = Unit
                override suspend fun guardarPuntos(puntos: List<PuntoCisternaEntity>) = Unit
                override suspend fun guardarConfirmacion(confirmacion: ConfirmacionHorarioEntity) = Unit
            }
        }
        single(QUALIFICADOR_USUARIO) { "u-1" }
        // En memoria: estas pruebas no necesitan que la sesión sobreviva a nada.
        single<Settings> { MapSettings() }
        single<pe.edu.upt.aguatacna.feature.recibo.domain.port.ReconocedorTexto> {
            object : pe.edu.upt.aguatacna.feature.recibo.domain.port.ReconocedorTexto {
                override suspend fun reconocer(bytesImagen: ByteArray): Result<pe.edu.upt.aguatacna.feature.recibo.domain.model.TextoReconocido> =
                    Result.success(pe.edu.upt.aguatacna.feature.recibo.domain.model.TextoReconocido(""))
            }
        }
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
