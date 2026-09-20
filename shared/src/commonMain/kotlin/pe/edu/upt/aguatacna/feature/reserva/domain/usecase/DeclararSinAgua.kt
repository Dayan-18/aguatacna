package pe.edu.upt.aguatacna.feature.reserva.domain.usecase

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ClaseIntervalo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.IntervaloConsumo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.OrigenLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva

/** Lo que cambia cuando el usuario declara que se quedó sin agua. */
data class ResultadoSinAgua(val reserva: Reserva, val intervaloObservado: IntervaloConsumo?)

class DeclararSinAgua(private val estimar: EstimarConsumo = EstimarConsumo()) {

    /**
     * La reserva queda vacía en `momento`. Si el llenado era real, el tanque se vació en
     * un tiempo conocido y eso es un intervalo `OBSERVADO`; si era asumido no se sabe
     * cuándo empezó a gastarse, así que no se aprende de él.
     */
    operator fun invoke(
        reserva: Reserva,
        intervalos: List<IntervaloConsumo>,
        momento: LocalDateTime
    ): ResultadoSinAgua {
        require(momento > reserva.llenado.momento) { "Solo se puede quedar sin agua después del último llenado" }
        val observado = observadoHasta(reserva, momento)
        val consumo = observado?.let { recalcular(reserva, intervalos + it) } ?: reserva.consumo
        return ResultadoSinAgua(reserva.copy(consumo = consumo, agotadaEn = momento), observado)
    }

    private fun observadoHasta(reserva: Reserva, momento: LocalDateTime): IntervaloConsumo? {
        if (reserva.llenado.origen != OrigenLlenado.REAL) return null
        return IntervaloConsumo(
            inicio = reserva.llenado.momento,
            fin = momento,
            litrosConsumidos = reserva.nivelTrasLlenado.litros,
            clase = ClaseIntervalo.OBSERVADO
        )
    }

    // Una sola declaración no mueve la estimación más allá del tope: un error de dedo no la desordena.
    private fun recalcular(reserva: Reserva, intervalos: List<IntervaloConsumo>): ConsumoHorario {
        val nuevo = estimar(intervalos, reserva.capacidad).consumo.litrosPorHora
        val anterior = reserva.consumo.litrosPorHora
        val minimo = anterior * (1 - ParametrosConsumo.TOPE_CAMBIO_POR_DECLARACION)
        val maximo = anterior * (1 + ParametrosConsumo.TOPE_CAMBIO_POR_DECLARACION)
        return ConsumoHorario(nuevo.coerceIn(minimo, maximo))
    }
}
