package pe.edu.upt.aguatacna.feature.reserva.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import pe.edu.upt.aguatacna.feature.reserva.data.mapper.aDatosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.CapacidadLitros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfiguracionHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Habitantes
import pe.edu.upt.aguatacna.feature.reserva.domain.model.HabitosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.IntervaloConsumo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.LitrosPorHabitanteDia
import pe.edu.upt.aguatacna.feature.reserva.domain.model.PerfilHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoReservorio
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.ReservaRepository
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.ArmarReserva
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.CalcularLitrosPorHabitanteDia
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.DeclararSinAgua
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.HistorialReserva

// Implementación temporal para que feature/retos y la interfaz avancen
// sin esperar a Room ni al servicio de datos (constitución, artículo VII).
// Guarda todo en memoria, pero usa los casos de uso reales del dominio.
class FakeReservaRepository(
    perfilInicial: PerfilHogar?,
    eventosIniciales: List<EventoLlenado>,
    private val iniciosDeAbastecimiento: List<LocalDateTime>,
    private val ahora: () -> LocalDateTime
) : ReservaRepository {

    private val perfil = MutableStateFlow(perfilInicial)
    private val eventos = eventosIniciales.toMutableList()
    private val sinLlegada = mutableListOf<LocalDateTime>()
    private val observados = mutableListOf<IntervaloConsumo>()
    private var agotadaEn: LocalDateTime? = null
    private var consumoVigente: ConsumoHorario? = null

    private val estado = MutableStateFlow(construirReserva())

    override fun observarPerfil(): Flow<PerfilHogar?> = perfil.asStateFlow()

    override suspend fun guardarPerfil(
        configuracion: ConfiguracionHogar,
        consumoPorHabitos: ConsumoHorario?
    ): Result<Unit> = runCatching {
        perfil.value = PerfilHogar(
            USUARIO_DE_PRUEBA, configuracion.tipoReservorio, configuracion.capacidad,
            configuracion.habitantes, configuracion.habitos, consumoPorHabitos
        )
        consumoVigente = null
        publicar()
    }

    override fun observarReserva(): Flow<Reserva?> = estado.asStateFlow()

    override suspend fun litrosPorHabitanteDia(): LitrosPorHabitanteDia? {
        val hogar = perfil.value?.aDatosDelHogar() ?: return null
        return CalcularLitrosPorHabitanteDia()(historial().intervalos(hogar), hogar.habitantes)
    }

    override suspend fun registrarLlenado(momento: LocalDateTime, tipo: TipoLlenado): Result<Unit> =
        runCatching {
            require(momento <= ahora()) { "No se puede registrar un llenado en el futuro" }
            eventos += EventoLlenado(momento, tipo)
            agotadaEn = null
            consumoVigente = null
            publicar()
        }

    override suspend fun registrarSinLlegada(momento: LocalDateTime): Result<Unit> =
        runCatching {
            require(momento <= ahora()) { "No se puede reportar en el futuro" }
            sinLlegada += momento
            publicar()
        }

    override suspend fun declararSinAgua(momento: LocalDateTime): Result<Unit> =
        runCatching {
            val hogar = checkNotNull(perfil.value?.aDatosDelHogar()) { "Configura tu hogar primero" }
            val reserva = checkNotNull(estado.value) { "Aún no hay una reserva que declarar sin agua" }
            require(momento <= ahora()) { "No se puede declarar en el futuro" }
            val resultado = DeclararSinAgua()(reserva, historial().intervalos(hogar), momento)
            resultado.intervaloObservado?.let { observados += it }
            agotadaEn = momento
            consumoVigente = resultado.reserva.consumo
            publicar()
        }

    private fun historial() = HistorialReserva(
        llenados = eventos.toList(),
        sinLlegada = sinLlegada.toList(),
        observados = observados.toList(),
        agotadaEn = agotadaEn,
        consumoVigente = consumoVigente
    )

    private fun publicar() {
        estado.value = construirReserva()
    }

    private fun construirReserva(): Reserva? {
        val hogar = perfil.value?.aDatosDelHogar() ?: return null
        return ArmarReserva()(hogar, historial(), iniciosDeAbastecimiento, ahora())
    }

    companion object {
        private const val USUARIO_DE_PRUEBA = "invitado"

        /** El hogar del Figma: 1 100 L, 4 personas, 82 L/h y llenado a las 5:15 de hoy. */
        fun conDatosDeEjemplo(ahora: () -> LocalDateTime): FakeReservaRepository {
            val perfil = PerfilHogar(
                usuarioId = USUARIO_DE_PRUEBA,
                tipoReservorio = TipoReservorio.TANQUE_ELEVADO,
                capacidad = CapacidadLitros.deLitros(1100.0),
                habitantes = Habitantes(4),
                habitos = HabitosDelHogar(duchasPorDia = 2, usaLavadora = true, riegaJardin = false),
                consumoPorHabitos = ConsumoHorario(82.0)
            )
            val llenado = EventoLlenado(LocalDateTime(ahora().date, LocalTime(5, 15)), TipoLlenado.COMPLETO)
            return FakeReservaRepository(perfil, listOf(llenado), emptyList(), ahora)
        }
    }
}
