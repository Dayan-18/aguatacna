package pe.edu.upt.aguatacna.feature.reserva

import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import pe.edu.upt.aguatacna.feature.reserva.data.FakeReservaRepository
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfirmacionEstimacion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.DatosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Habitantes
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Litros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeReservaRepositoryTest {

    private val hogar = DatosDelHogar(CAPACIDAD_1000, Habitantes(4), ConsumoHorario(50.0))

    private fun repositorio(
        eventos: List<EventoLlenado> = emptyList(),
        ventanas: List<LocalDateTime> = emptyList(),
        ahora: Int = 30
    ) = FakeReservaRepository(hogar, eventos, ventanas) { enHora(ahora) }

    private fun reservaDe(repositorio: FakeReservaRepository): Reserva =
        assertNotNull(ejecutar { repositorio.observarReserva().first() })

    private fun llenadoReal(hora: Int) = EventoLlenado(enHora(hora), TipoLlenado.COMPLETO)

    @Test
    fun conLosDatosDelFigmaLaReservaSeAgotaALas1840() {
        val hoy = LocalDateTime(2026, 9, 19, 15, 0)
        val reserva = reservaDe(FakeReservaRepository.conDatosDeEjemplo { hoy })
        // 1100 L a 82 L/h duran 13 h 24 min 53 s; el Figma lo muestra redondeado a 18:40.
        val segundos = reserva.agotamientoProyectado().time.toSecondOfDay()
        assertEquals(LocalTime(18, 40).toSecondOfDay().toDouble(), segundos.toDouble(), absoluteTolerance = 60.0)
        assertEquals(82.0, reserva.consumo.litrosPorHora, absoluteTolerance = 0.001)
    }

    @Test
    fun sinLlenadosNiAbastecimientosNoHayReserva() {
        assertNull(ejecutar { repositorio().observarReserva().first() })
    }

    @Test
    fun registrarUnLlenadoActualizaLaReserva() {
        val repositorio = repositorio(listOf(llenadoReal(0)))
        assertTrue(ejecutar { repositorio.registrarLlenado(enHora(28), TipoLlenado.COMPLETO) }.isSuccess)
        assertEquals(enHora(28), reservaDe(repositorio).llenado.momento)
    }

    @Test
    fun noSePuedeRegistrarUnLlenadoEnElFuturo() {
        val resultado = ejecutar { repositorio().registrarLlenado(enHora(40), TipoLlenado.COMPLETO) }
        assertTrue(resultado.isFailure)
    }

    @Test // CA-18
    fun declararSinAguaVaciaLaReserva() {
        val repositorio = repositorio(listOf(llenadoReal(0)), ahora = 16)
        assertTrue(ejecutar { repositorio.declararSinAgua(enHora(16)) }.isSuccess)
        val reserva = reservaDe(repositorio)
        assertEquals(enHora(16), reserva.agotamientoProyectado())
        assertEquals(Litros.CERO, reserva.nivelEn(enHora(16)).litros)
    }

    @Test
    fun sinReservaNoSePuedeDeclararSinAgua() {
        assertTrue(ejecutar { repositorio().declararSinAgua(enHora(10)) }.isFailure)
    }

    @Test // CA-13
    fun siElSectorAbastecioSinConfirmacionLaReservaNoEstaConfirmada() {
        val reserva = reservaDe(repositorio(listOf(llenadoReal(0)), listOf(enHora(24))))
        assertEquals(ConfirmacionEstimacion.NO_CONFIRMADA, reserva.confirmacion)
    }

    @Test // CA-25
    fun reportarQueNoLlegoDevuelveAlUltimoLlenadoReal() {
        val repositorio = repositorio(listOf(llenadoReal(0)), listOf(enHora(24)))
        ejecutar { repositorio.registrarSinLlegada(enHora(27)) }
        assertEquals(ConfirmacionEstimacion.CONFIRMADA, reservaDe(repositorio).confirmacion)
    }

    @Test // CA-23
    fun sinIntervalosNoHayLitrosPorHabitanteDia() {
        assertNull(ejecutar { repositorio(listOf(llenadoReal(0))).litrosPorHabitanteDia() })
    }

    @Test // CA-05
    fun conDosLlenadosHayLitrosPorHabitanteDia() {
        // 1000 L en 96 h son 250 L por día; entre 4 personas, 62,5.
        val repositorio = repositorio(listOf(llenadoReal(0), llenadoReal(96)), ahora = 100)
        val indicador = assertNotNull(ejecutar { repositorio.litrosPorHabitanteDia() })
        assertEquals(62.5, indicador.valor, absoluteTolerance = 0.001)
    }
}
