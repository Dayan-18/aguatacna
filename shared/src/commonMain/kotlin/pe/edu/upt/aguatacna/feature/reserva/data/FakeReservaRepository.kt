package pe.edu.upt.aguatacna.feature.reserva.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.CapacidadLitros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.DatosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Habitantes
import pe.edu.upt.aguatacna.feature.reserva.domain.model.IntervaloConsumo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.LitrosPorHabitanteDia
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.ReservaRepository
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.CalcularLitrosPorHabitanteDia
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.ConstruirIntervalos
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.DeclararSinAgua
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.EstimarConsumo
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.ResolverLlenadoVigente

// Implementación temporal para que feature/retos y la interfaz avancen
// sin esperar a Room ni al servicio de datos (constitución, artículo VII).
// Guarda todo en memoria, pero usa los casos de uso reales del dominio.
class FakeReservaRepository(
    private val hogar: DatosDelHogar,
    eventosIniciales: List<EventoLlenado>,
    private val iniciosDeAbastecimiento: List<LocalDateTime>,
    private val ahora: () -> LocalDateTime
) : ReservaRepository {

    private val eventos = eventosIniciales.toMutableList()
    private val sinLlegada = mutableListOf<LocalDateTime>()
    private val observados = mutableListOf<IntervaloConsumo>()
    private var agotadaEn: LocalDateTime? = null
    private var consumoVigente: ConsumoHorario? = null

    private val estado = MutableStateFlow(construirReserva())

    override fun observarReserva(): Flow<Reserva?> = estado.asStateFlow()

    override suspend fun litrosPorHabitanteDia(): LitrosPorHabitanteDia? =
        CalcularLitrosPorHabitanteDia()(intervalos(), hogar.habitantes)

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
            val reserva = checkNotNull(estado.value) { "Aún no hay una reserva que declarar sin agua" }
            require(momento <= ahora()) { "No se puede declarar en el futuro" }
            val resultado = DeclararSinAgua()(reserva, intervalos(), momento)
            resultado.intervaloObservado?.let { observados += it }
            agotadaEn = momento
            consumoVigente = resultado.reserva.consumo
            publicar()
        }

    private fun intervalos(): List<IntervaloConsumo> =
        ConstruirIntervalos()(eventos, hogar.capacidad) + observados

    private fun publicar() {
        estado.value = construirReserva()
    }

    private fun construirReserva(): Reserva? {
        val llenado = ResolverLlenadoVigente()(eventos, iniciosDeAbastecimiento, sinLlegada, ahora()) ?: return null
        val consumo = consumoVigente
            ?: EstimarConsumo()(intervalos(), hogar.capacidad, hogar.consumoPorHabitos).consumo
        val vaciadaDespues = agotadaEn?.takeIf { it > llenado.momento }
        return Reserva(hogar.capacidad, consumo, llenado, vaciadaDespues)
    }

    companion object {
        /** El hogar del Figma: 1 100 L, 4 personas, 82 L/h y llenado a las 5:15 de hoy. */
        fun conDatosDeEjemplo(ahora: () -> LocalDateTime): FakeReservaRepository {
            val hogar = DatosDelHogar(CapacidadLitros.deLitros(1100.0), Habitantes(4), ConsumoHorario(82.0))
            val llenado = EventoLlenado(LocalDateTime(ahora().date, LocalTime(5, 15)), TipoLlenado.COMPLETO)
            return FakeReservaRepository(hogar, listOf(llenado), emptyList(), ahora)
        }
    }
}
