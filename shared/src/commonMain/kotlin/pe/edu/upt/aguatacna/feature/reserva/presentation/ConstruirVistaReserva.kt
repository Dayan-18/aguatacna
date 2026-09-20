package pe.edu.upt.aguatacna.feature.reserva.presentation

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.LitrosPorHabitanteDia
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.CalcularDeficit
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.EvaluarProyeccion
import kotlin.math.roundToInt

/** Convierte la reserva del dominio en lo que muestra la pantalla; el ViewModel solo lo invoca. */
class ConstruirVistaReserva(
    private val calcularDeficit: CalcularDeficit = CalcularDeficit(),
    private val evaluarProyeccion: EvaluarProyeccion = EvaluarProyeccion()
) {
    operator fun invoke(
        reserva: Reserva,
        proximoAbastecimiento: LocalDateTime?,
        litrosPorHabitanteDia: LitrosPorHabitanteDia?,
        ahora: LocalDateTime
    ): ReservaVista {
        val nivel = reserva.nivelEn(ahora)
        val deficit = calcularDeficit(reserva, proximoAbastecimiento)
        return ReservaVista(
            nivelLitros = nivel.litros.valor.roundToInt(),
            capacidadLitros = reserva.capacidad.litros.valor.roundToInt(),
            porcentaje = nivel.porcentaje.roundToInt(),
            estado = evaluarProyeccion(reserva, proximoAbastecimiento),
            confirmacion = reserva.confirmacion,
            textoAgotamiento = describirMomento(reserva.agotamientoProyectado(), ahora),
            textoVuelveElAgua = proximoAbastecimiento?.let { describirMomento(it, ahora) },
            textoDeficit = deficit?.let { formatearDuracion(it.horas) },
            consumoLitrosPorHora = reserva.consumo.litrosPorHora.roundToInt(),
            litrosPorHabitanteDia = litrosPorHabitanteDia?.valor?.roundToInt()
        )
    }
}
