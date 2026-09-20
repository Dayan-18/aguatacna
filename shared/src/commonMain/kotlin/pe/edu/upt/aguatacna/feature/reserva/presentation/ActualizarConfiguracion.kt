package pe.edu.upt.aguatacna.feature.reserva.presentation

import pe.edu.upt.aguatacna.feature.reserva.domain.model.Habitantes
import pe.edu.upt.aguatacna.feature.reserva.domain.model.HabitosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.EstimadorPorHabitos
import kotlin.math.roundToInt

/** Aplica un cambio del formulario y recalcula el consumo estimado que se muestra al usuario. */
class ActualizarConfiguracion(private val estimador: EstimadorPorHabitos) {

    operator fun invoke(estado: ConfiguracionUiState, evento: ConfiguracionEvent): ConfiguracionUiState {
        val nuevo = when (evento) {
            is ConfiguracionEvent.ElegirTipo -> estado.copy(tipo = evento.tipo)
            is ConfiguracionEvent.CambiarCapacidad ->
                estado.copy(capacidadLitros = evento.litros.coerceIn(CAPACIDAD_MINIMA_LITROS, CAPACIDAD_MAXIMA_LITROS))
            is ConfiguracionEvent.CambiarHabitantes ->
                estado.copy(habitantes = (estado.habitantes + evento.diferencia).coerceIn(1, HABITANTES_MAXIMOS))
            is ConfiguracionEvent.CambiarDuchas ->
                estado.copy(duchasPorDia = (estado.duchasPorDia + evento.diferencia).coerceIn(0, DUCHAS_MAXIMAS))
            ConfiguracionEvent.AlternarLavadora -> estado.copy(usaLavadora = !estado.usaLavadora)
            ConfiguracionEvent.AlternarRiego -> estado.copy(riegaJardin = !estado.riegaJardin)
            ConfiguracionEvent.DescartarError -> estado.copy(error = null)
            ConfiguracionEvent.Continuar -> estado
        }
        return nuevo.copy(consumoEstimadoLitrosPorHora = estimarConsumo(nuevo))
    }

    fun conEstimacion(estado: ConfiguracionUiState): ConfiguracionUiState =
        estado.copy(consumoEstimadoLitrosPorHora = estimarConsumo(estado))

    private fun estimarConsumo(estado: ConfiguracionUiState): Int? =
        estimador.estimar(
            HabitosDelHogar(estado.duchasPorDia, estado.usaLavadora, estado.riegaJardin),
            Habitantes(estado.habitantes)
        )?.litrosPorHora?.roundToInt()
}
