package pe.edu.upt.aguatacna.feature.reserva

import kotlinx.coroutines.flow.first
import pe.edu.upt.aguatacna.feature.reserva.data.EstimadorPorHabitosProvisional
import pe.edu.upt.aguatacna.feature.reserva.data.FakeReservaRepository
import pe.edu.upt.aguatacna.feature.reserva.domain.model.CapacidadLitros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfiguracionHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Habitantes
import pe.edu.upt.aguatacna.feature.reserva.domain.model.HabitosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoReservorio
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.ConfigurarHogar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConfigurarHogarTest {

    private val estimador = EstimadorPorHabitosProvisional()

    private fun configuracion(
        habitantes: Int = 4,
        habitos: HabitosDelHogar = HabitosDelHogar(2, usaLavadora = true, riegaJardin = false)
    ) = ConfiguracionHogar(
        TipoReservorio.CISTERNA, CapacidadLitros.deLitros(2000.0), Habitantes(habitantes), habitos
    )

    private fun repositorioSinConfigurar() = FakeReservaRepository(null, emptyList(), emptyList()) { enHora(30) }

    @Test
    fun hastaQueSeConfigureNoHayPerfilNiReserva() {
        val repositorio = repositorioSinConfigurar()
        assertNull(ejecutar { repositorio.observarPerfil().first() })
        assertNull(ejecutar { repositorio.observarReserva().first() })
    }

    @Test
    fun guardaLaConfiguracionConElConsumoEstimadoDeLosHabitos() {
        val repositorio = repositorioSinConfigurar()
        val resultado = ejecutar { ConfigurarHogar(estimador, repositorio)(configuracion()) }
        assertTrue(resultado.isSuccess)
        val perfil = assertNotNull(ejecutar { repositorio.observarPerfil().first() })
        assertEquals(TipoReservorio.CISTERNA, perfil.tipoReservorio)
        assertEquals(configuracion(), perfil.configuracion)
        assertNotNull(perfil.consumoPorHabitos)
    }

    @Test
    fun volverAConfigurarDescartaElConsumoAprendido() {
        val repositorio = FakeReservaRepository(perfilDePrueba(), listOf(llenadoDePrueba(0)), emptyList()) { enHora(16) }
        ejecutar { repositorio.declararSinAgua(enHora(16)) }
        ejecutar { ConfigurarHogar(estimador, repositorio)(configuracion()) }
        val perfil = assertNotNull(ejecutar { repositorio.observarPerfil().first() })
        assertNull(perfil.consumoVigente)
    }

    @Test // CA-16
    fun masGenteYMasHabitosDanMasConsumo() {
        val poco = estimador.estimar(HabitosDelHogar(0, usaLavadora = false, riegaJardin = false), Habitantes(2))
        val mucho = estimador.estimar(HabitosDelHogar(3, usaLavadora = true, riegaJardin = true), Habitantes(5))
        assertTrue(mucho.litrosPorHora > poco.litrosPorHora)
    }

    @Test
    fun elEstimadorSiempreDaUnConsumoPositivo() {
        val minimo = estimador.estimar(HabitosDelHogar(0, usaLavadora = false, riegaJardin = false), Habitantes(1))
        assertTrue(minimo.litrosPorHora > 0.0)
    }
}
