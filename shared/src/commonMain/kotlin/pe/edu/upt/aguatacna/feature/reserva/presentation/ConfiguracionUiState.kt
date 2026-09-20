package pe.edu.upt.aguatacna.feature.reserva.presentation

import pe.edu.upt.aguatacna.feature.reserva.domain.model.CapacidadLitros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfiguracionHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Habitantes
import pe.edu.upt.aguatacna.feature.reserva.domain.model.HabitosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoReservorio

const val CAPACIDAD_MINIMA_LITROS = 200
const val CAPACIDAD_MAXIMA_LITROS = 5000
const val PASO_CAPACIDAD_LITROS = 50
const val HABITANTES_MAXIMOS = 12
const val DUCHAS_MAXIMAS = 6

data class ConfiguracionUiState(
    val tipo: TipoReservorio = TipoReservorio.TANQUE_ELEVADO,
    val capacidadLitros: Int = 1000,
    val habitantes: Int = 4,
    val duchasPorDia: Int = 2,
    val usaLavadora: Boolean = true,
    val riegaJardin: Boolean = false,
    val consumoEstimadoLitrosPorHora: Int? = null,
    val guardando: Boolean = false,
    val guardado: Boolean = false,
    val error: String? = null
) {
    fun aConfiguracion() = ConfiguracionHogar(
        tipoReservorio = tipo,
        capacidad = CapacidadLitros.deLitros(capacidadLitros.toDouble()),
        habitantes = Habitantes(habitantes),
        habitos = HabitosDelHogar(duchasPorDia, usaLavadora, riegaJardin)
    )

    companion object {
        fun desde(configuracion: ConfiguracionHogar) = ConfiguracionUiState(
            tipo = configuracion.tipoReservorio,
            capacidadLitros = configuracion.capacidad.litros.valor.toInt(),
            habitantes = configuracion.habitantes.cantidad,
            duchasPorDia = configuracion.habitos.duchasPorDia,
            usaLavadora = configuracion.habitos.usaLavadora,
            riegaJardin = configuracion.habitos.riegaJardin
        )
    }
}

sealed interface ConfiguracionEvent {
    data class ElegirTipo(val tipo: TipoReservorio) : ConfiguracionEvent
    data class CambiarCapacidad(val litros: Int) : ConfiguracionEvent
    data class CambiarHabitantes(val diferencia: Int) : ConfiguracionEvent
    data class CambiarDuchas(val diferencia: Int) : ConfiguracionEvent
    data object AlternarLavadora : ConfiguracionEvent
    data object AlternarRiego : ConfiguracionEvent
    data object Continuar : ConfiguracionEvent
    data object DescartarError : ConfiguracionEvent
}
