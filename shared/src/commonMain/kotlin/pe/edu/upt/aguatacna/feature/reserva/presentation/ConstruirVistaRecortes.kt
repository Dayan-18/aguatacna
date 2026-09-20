package pe.edu.upt.aguatacna.feature.reserva.presentation

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.HabitosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Recomendacion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.CalcularDeficit
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.SimularRecortes
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.SugerirRecortes
import kotlin.math.roundToInt

class ConstruirVistaRecortes(
    private val calcularDeficit: CalcularDeficit = CalcularDeficit(),
    private val sugerir: SugerirRecortes = SugerirRecortes(),
    private val simular: SimularRecortes = SimularRecortes()
) {
    /** `null` si no hay déficit, o si el sector no tiene horario y no hay contra qué comparar. */
    operator fun invoke(
        reserva: Reserva,
        habitos: HabitosDelHogar,
        proximoAbastecimiento: LocalDateTime?,
        desmarcadas: Set<Recomendacion>,
        ahora: LocalDateTime
    ): QueRecortarVista? {
        val proximo = proximoAbastecimiento ?: return null
        val deficit = calcularDeficit(reserva, proximo) ?: return null
        val sugeridas = sugerir(deficit, habitos)
        if (sugeridas.isEmpty()) return null
        val elegidas = sugeridas.filterNot { it in desmarcadas }.toSet()
        val resultado = simular(deficit, reserva.consumo, elegidas)
        return QueRecortarVista(
            textoDeficit = formatearDuracion(deficit.horas),
            textoAgotamiento = describirMomento(reserva.agotamientoProyectado(), ahora),
            textoVuelveElAgua = describirMomento(proximo, ahora),
            opciones = sugeridas.map { OpcionDeRecorte(it, it.litrosQueAhorra.valor.roundToInt(), it in elegidas) },
            ahorroLitros = resultado.ahorro.valor.roundToInt(),
            textoGanas = formatearDuracion(resultado.horasGanadas),
            textoFaltan = if (resultado.cubreElDeficit) "Ya te alcanza" else formatearDuracion(resultado.horasQueFaltan),
            cubreElDeficit = resultado.cubreElDeficit
        )
    }
}
