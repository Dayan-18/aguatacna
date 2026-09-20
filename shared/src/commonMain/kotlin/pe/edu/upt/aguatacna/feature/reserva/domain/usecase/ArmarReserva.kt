package pe.edu.upt.aguatacna.feature.reserva.domain.usecase

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.DatosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.IntervaloConsumo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva

/** Todo lo que el hogar ha registrado, sin interpretar. */
data class HistorialReserva(
    val llenados: List<EventoLlenado> = emptyList(),
    val sinLlegada: List<LocalDateTime> = emptyList(),
    val observados: List<IntervaloConsumo> = emptyList(),
    val agotadaEn: LocalDateTime? = null,
    val consumoVigente: ConsumoHorario? = null
) {
    fun intervalos(hogar: DatosDelHogar): List<IntervaloConsumo> =
        ConstruirIntervalos()(llenados, hogar.capacidad) + observados
}

class ArmarReserva(
    private val resolver: ResolverLlenadoVigente = ResolverLlenadoVigente(),
    private val estimar: EstimarConsumo = EstimarConsumo()
) {
    /** `null` mientras no haya ningún llenado real ni abastecimiento del sector. */
    operator fun invoke(
        hogar: DatosDelHogar,
        historial: HistorialReserva,
        iniciosDeAbastecimiento: List<LocalDateTime>,
        ahora: LocalDateTime
    ): Reserva? {
        val llenado = resolver(historial.llenados, iniciosDeAbastecimiento, historial.sinLlegada, ahora)
            ?: return null
        val consumo = historial.consumoVigente
            ?: estimar(historial.intervalos(hogar), hogar.capacidad, hogar.consumoPorHabitos).consumo
        val vaciadaDespues = historial.agotadaEn?.takeIf { it > llenado.momento }
        return Reserva(hogar.capacidad, consumo, llenado, vaciadaDespues)
    }
}
