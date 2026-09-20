package pe.edu.upt.aguatacna.feature.reserva.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.data.local.NovedadReservaEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.ReservaDao
import pe.edu.upt.aguatacna.feature.reserva.data.mapper.aDatosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.data.mapper.aDominio
import pe.edu.upt.aguatacna.feature.reserva.data.mapper.aEntidad
import pe.edu.upt.aguatacna.feature.reserva.data.mapper.aHistorial
import pe.edu.upt.aguatacna.feature.reserva.data.mapper.comoNovedad
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfiguracionHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.DatosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.LitrosPorHabitanteDia
import pe.edu.upt.aguatacna.feature.reserva.domain.model.PerfilHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.PrevisualizacionSinAgua
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.AbastecimientosDelSector
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.ReservaRepository
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.ArmarReserva
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.CalcularLitrosPorHabitanteDia
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.DeclararSinAgua
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.HistorialReserva
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.ResultadoSinAgua

/** Lee y escribe solo en Room: la base local es la fuente de verdad (constitución, artículo II). */
class ReservaRepositoryImpl(
    private val dao: ReservaDao,
    private val usuarioId: String,
    private val abastecimientos: AbastecimientosDelSector,
    private val ahora: () -> LocalDateTime,
    private val nuevoId: () -> String
) : ReservaRepository {

    private class Estado(val perfil: PerfilHogar, val historial: HistorialReserva) {
        val hogar: DatosDelHogar get() = perfil.aDatosDelHogar()
    }

    override fun observarPerfil(): Flow<PerfilHogar?> = dao.observarPerfil(usuarioId).map { it?.aDominio() }

    override suspend fun guardarPerfil(configuracion: ConfiguracionHogar, consumoPorHabitos: ConsumoHorario?): Result<Unit> =
        runCatching {
            val perfil = PerfilHogar(
                usuarioId, configuracion.tipoReservorio, configuracion.capacidad,
                configuracion.habitantes, configuracion.habitos, consumoPorHabitos
            )
            dao.guardarPerfil(perfil.aEntidad())
        }

    override fun observarReserva(): Flow<Reserva?> =
        combine(
            dao.observarPerfil(usuarioId),
            dao.observarLlenados(usuarioId),
            dao.observarNovedades(usuarioId)
        ) { perfil, llenados, novedades ->
            perfil?.aDominio()?.let { armar(Estado(it, aHistorial(it, llenados, novedades))) }
        }

    override suspend fun litrosPorHabitanteDia(): LitrosPorHabitanteDia? {
        val estado = cargarEstado() ?: return null
        return CalcularLitrosPorHabitanteDia()(estado.historial.intervalos(estado.hogar), estado.hogar.habitantes)
    }

    override suspend fun registrarLlenado(momento: LocalDateTime, tipo: TipoLlenado): Result<Unit> =
        runCatching {
            require(momento <= ahora()) { "No se puede registrar un llenado en el futuro" }
            val estado = requireNotNull(cargarEstado()) { SIN_PERFIL }
            dao.guardarLlenado(EventoLlenado(momento, tipo).aEntidad(nuevoId(), usuarioId))
            dao.guardarPerfil(estado.perfil.copy(consumoVigente = null).aEntidad())
        }

    override suspend fun registrarSinLlegada(momento: LocalDateTime): Result<Unit> =
        runCatching {
            require(momento <= ahora()) { "No se puede reportar en el futuro" }
            dao.guardarNovedad(NovedadReservaEntity(nuevoId(), usuarioId, momento.toString(), NovedadReservaEntity.SIN_LLEGADA))
        }

    override suspend fun declararSinAgua(momento: LocalDateTime): Result<Unit> =
        runCatching {
            val simulacion = simularSinAgua(momento)
            val resultado = simulacion.resultado
            dao.guardarNovedad(resultado.intervaloObservado.comoNovedad(nuevoId(), usuarioId, momento))
            dao.guardarPerfil(simulacion.estado.perfil.copy(consumoVigente = resultado.reserva.consumo).aEntidad())
        }

    override suspend fun previsualizarSinAgua(momento: LocalDateTime): Result<PrevisualizacionSinAgua> =
        runCatching {
            val simulacion = simularSinAgua(momento)
            PrevisualizacionSinAgua(
                agotamientoProyectado = simulacion.reserva.agotamientoProyectado(),
                momento = momento,
                consumoActual = simulacion.reserva.consumo,
                consumoNuevo = simulacion.resultado.reserva.consumo
            )
        }

    private class Simulacion(val estado: Estado, val reserva: Reserva, val resultado: ResultadoSinAgua)

    private suspend fun simularSinAgua(momento: LocalDateTime): Simulacion {
        require(momento <= ahora()) { "No se puede declarar en el futuro" }
        val estado = requireNotNull(cargarEstado()) { SIN_PERFIL }
        val reserva = checkNotNull(armar(estado)) { "Aún no hay una reserva que declarar sin agua" }
        val resultado = DeclararSinAgua()(reserva, estado.historial.intervalos(estado.hogar), momento)
        return Simulacion(estado, reserva, resultado)
    }

    private suspend fun cargarEstado(): Estado? {
        val perfil = dao.observarPerfil(usuarioId).first()?.aDominio() ?: return null
        val llenados = dao.observarLlenados(usuarioId).first()
        val novedades = dao.observarNovedades(usuarioId).first()
        return Estado(perfil, aHistorial(perfil, llenados, novedades))
    }

    private suspend fun armar(estado: Estado): Reserva? {
        val inicios = abastecimientos.iniciosHasta(ahora())
        return ArmarReserva()(estado.hogar, estado.historial, inicios, ahora())
    }

    private companion object {
        const val SIN_PERFIL = "Configura tu hogar antes de registrar movimientos"
    }
}
