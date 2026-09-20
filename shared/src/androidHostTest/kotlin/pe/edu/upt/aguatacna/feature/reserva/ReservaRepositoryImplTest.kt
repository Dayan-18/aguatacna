package pe.edu.upt.aguatacna.feature.reserva

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.data.ReservaRepositoryImpl
import pe.edu.upt.aguatacna.feature.reserva.data.local.NovedadReservaEntity
import pe.edu.upt.aguatacna.feature.reserva.data.mapper.aEntidad
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfirmacionEstimacion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Habitantes
import pe.edu.upt.aguatacna.feature.reserva.domain.model.HabitosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Litros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.PerfilHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoReservorio
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.AbastecimientosDelSector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReservaRepositoryImplTest {

    private val dao = FakeReservaDao()
    private var horaActual = 30
    private var inicios = emptyList<LocalDateTime>()
    private var contador = 0

    private val perfil = PerfilHogar(
        usuarioId = "u-1",
        tipoReservorio = TipoReservorio.TANQUE_ELEVADO,
        capacidad = CAPACIDAD_1000,
        habitantes = Habitantes(4),
        habitos = HabitosDelHogar(2, usaLavadora = true, riegaJardin = false),
        consumoPorHabitos = ConsumoHorario(50.0)
    )

    private fun repositorio(nuevo: Boolean = true) = ReservaRepositoryImpl(
        dao = dao,
        usuarioId = "u-1",
        abastecimientos = object : AbastecimientosDelSector {
            override suspend fun iniciosHasta(ahora: LocalDateTime) = inicios
            override suspend fun proximoDesde(ahora: LocalDateTime): LocalDateTime? = null
            override suspend fun nombreDelSector(): String? = null
        },
        ahora = { enHora(horaActual) },
        nuevoId = { "id-${contador++}" }
    ).also { if (nuevo) dao.perfil.value = dao.perfil.value ?: perfil.aEntidad() }

    private fun <T> bloque(cuerpo: suspend () -> T): T = runBlocking { cuerpo() }

    @Test
    fun guardarLaConfiguracionPersisteElPerfilConSuConsumoPorHabitos() = bloque {
        val repositorio = repositorio(nuevo = false)
        assertNull(repositorio.observarPerfil().first())
        val resultado = repositorio.guardarPerfil(perfil.configuracion, ConsumoHorario(58.0))
        assertTrue(resultado.isSuccess)
        val guardado = assertNotNull(repositorio.observarPerfil().first())
        assertEquals(perfil.configuracion, guardado.configuracion)
        assertEquals(ConsumoHorario(58.0), guardado.consumoPorHabitos)
        assertNull(guardado.consumoVigente)
    }

    @Test
    fun sinPerfilNoHayReservaNiSePuedeRegistrar() = bloque {
        val repositorio = repositorio(nuevo = false)
        assertNull(repositorio.observarReserva().first())
        assertTrue(repositorio.registrarLlenado(enHora(10), TipoLlenado.COMPLETO).isFailure)
    }

    @Test
    fun unLlenadoRegistradoSePersisteYProyectaElAgotamiento() = bloque {
        val repositorio = repositorio()
        assertTrue(repositorio.registrarLlenado(enHora(6), TipoLlenado.COMPLETO).isSuccess)
        assertEquals(1, dao.llenados.value.size)
        val reserva = assertNotNull(repositorio.observarReserva().first())
        assertEquals(enHora(26), reserva.agotamientoProyectado())
        assertEquals(ConfirmacionEstimacion.CONFIRMADA, reserva.confirmacion)
    }

    @Test
    fun otraInstanciaVeElMismoEstadoPorqueTodoVienenDeLaBase() = bloque {
        repositorio().registrarLlenado(enHora(6), TipoLlenado.COMPLETO)
        val otra = assertNotNull(repositorio().observarReserva().first())
        assertEquals(enHora(6), otra.llenado.momento)
    }

    @Test
    fun noSePuedeRegistrarUnLlenadoEnElFuturo() = bloque {
        assertTrue(repositorio().registrarLlenado(enHora(40), TipoLlenado.COMPLETO).isFailure)
    }

    @Test // CA-13
    fun siElSectorAbastecioSinConfirmacionLaEstimacionNoEstaConfirmada() = bloque {
        val repositorio = repositorio()
        repositorio.registrarLlenado(enHora(0), TipoLlenado.COMPLETO)
        inicios = listOf(enHora(24))
        val reserva = assertNotNull(repositorio.observarReserva().first())
        assertEquals(ConfirmacionEstimacion.NO_CONFIRMADA, reserva.confirmacion)
    }

    @Test // CA-25
    fun reportarQueNoLlegoSePersisteYVuelveAlLlenadoReal() = bloque {
        val repositorio = repositorio()
        repositorio.registrarLlenado(enHora(0), TipoLlenado.COMPLETO)
        inicios = listOf(enHora(24))
        assertTrue(repositorio.registrarSinLlegada(enHora(27)).isSuccess)
        assertEquals(NovedadReservaEntity.SIN_LLEGADA, dao.novedades.value.single().tipo)
        assertEquals(ConfirmacionEstimacion.CONFIRMADA, assertNotNull(repositorio.observarReserva().first()).confirmacion)
    }

    @Test // CA-18, CA-19, CA-20
    fun declararSinAguaVaciaLaReservaYGuardaLoAprendido() = bloque {
        horaActual = 16
        val repositorio = repositorio()
        repositorio.registrarLlenado(enHora(0), TipoLlenado.COMPLETO)
        assertTrue(repositorio.declararSinAgua(enHora(16)).isSuccess)
        val novedad = dao.novedades.value.single()
        assertEquals(1000.0, novedad.litrosObservados)
        assertEquals(62.5, assertNotNull(dao.perfil.value).consumoVigenteLitrosHora!!, absoluteTolerance = 0.001)
        val reserva = assertNotNull(repositorio.observarReserva().first())
        assertEquals(enHora(16), reserva.agotamientoProyectado())
        assertEquals(Litros.CERO, reserva.nivelEn(enHora(16)).litros)
    }

    @Test
    fun deUnLlenadoAsumidoNoSeGuardaIntervaloObservado() = bloque {
        horaActual = 30
        inicios = listOf(enHora(24))
        val repositorio = repositorio()
        assertTrue(repositorio.declararSinAgua(enHora(28)).isSuccess)
        assertNull(dao.novedades.value.single().inicioObservado)
    }

    @Test
    fun unNuevoLlenadoReiniciaElConsumoVigente() = bloque {
        horaActual = 16
        val repositorio = repositorio()
        repositorio.registrarLlenado(enHora(0), TipoLlenado.COMPLETO)
        repositorio.declararSinAgua(enHora(16))
        horaActual = 30
        repositorio.registrarLlenado(enHora(20), TipoLlenado.COMPLETO)
        assertNull(assertNotNull(dao.perfil.value).consumoVigenteLitrosHora)
    }

    @Test // CA-05, CA-23
    fun litrosPorHabitanteDiaSoloConIntervalos() = bloque {
        horaActual = 100
        val repositorio = repositorio()
        repositorio.registrarLlenado(enHora(0), TipoLlenado.COMPLETO)
        assertNull(repositorio.litrosPorHabitanteDia())
        repositorio.registrarLlenado(enHora(96), TipoLlenado.COMPLETO)
        assertEquals(62.5, assertNotNull(repositorio.litrosPorHabitanteDia()).valor, absoluteTolerance = 0.001)
    }
}
