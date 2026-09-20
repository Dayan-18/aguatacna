package pe.edu.upt.aguatacna.feature.reserva

import pe.edu.upt.aguatacna.feature.reserva.data.mapper.aDominio
import pe.edu.upt.aguatacna.feature.reserva.data.mapper.aEntidad
import pe.edu.upt.aguatacna.feature.reserva.domain.model.CapacidadLitros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Habitantes
import pe.edu.upt.aguatacna.feature.reserva.domain.model.HabitosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.PerfilHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoReservorio
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ReservaMapperTest {

    private val perfil = PerfilHogar(
        usuarioId = "u-1",
        tipoReservorio = TipoReservorio.TANQUE_ELEVADO,
        capacidad = CapacidadLitros.deLitros(1100.0),
        habitantes = Habitantes(4),
        habitos = HabitosDelHogar(duchasPorDia = 2, usaLavadora = true, riegaJardin = false),
        consumoPorHabitos = ConsumoHorario(82.0)
    )

    @Test
    fun elPerfilSobreviveAIdaYVuelta() {
        assertEquals(perfil, perfil.aEntidad().aDominio())
    }

    @Test
    fun sinConsumoEstimadoLasColumnasQuedanNulas() {
        val entidad = perfil.copy(consumoPorHabitos = null).aEntidad()
        assertNull(entidad.consumoPorHabitosLitrosHora)
        assertNull(entidad.consumoVigenteLitrosHora)
    }

    @Test
    fun elConsumoVigenteSeConserva() {
        val conVigente = perfil.copy(consumoVigente = ConsumoHorario(65.0))
        assertEquals(ConsumoHorario(65.0), conVigente.aEntidad().aDominio().consumoVigente)
    }

    @Test
    fun elLlenadoSobreviveAIdaYVuelta() {
        val llenado = EventoLlenado(LocalDateTime(2026, 9, 19, 5, 15), TipoLlenado.MITAD)
        assertEquals(llenado, llenado.aEntidad("e-1", "u-1").aDominio())
    }

    @Test
    fun elMomentoSeGuardaEnFormatoOrdenable() {
        val temprano = EventoLlenado(LocalDateTime(2026, 9, 19, 5, 15), TipoLlenado.COMPLETO).aEntidad("a", "u")
        val tarde = EventoLlenado(LocalDateTime(2026, 9, 19, 18, 40), TipoLlenado.COMPLETO).aEntidad("b", "u")
        assertEquals(true, temprano.momento < tarde.momento)
    }

    @Test
    fun rechazaDuchasNegativas() {
        assertFailsWith<IllegalArgumentException> { HabitosDelHogar(-1, usaLavadora = false, riegaJardin = false) }
    }
}
