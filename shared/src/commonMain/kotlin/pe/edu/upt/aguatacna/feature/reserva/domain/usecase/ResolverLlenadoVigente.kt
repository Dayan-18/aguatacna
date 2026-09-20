package pe.edu.upt.aguatacna.feature.reserva.domain.usecase

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.OrigenLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado

/**
 * Decide desde qué llenado se proyecta la reserva. Si el sector abasteció
 * después del último llenado real y el usuario no lo confirmó, se asume un
 * llenado completo al inicio de esa ventana.
 */
class ResolverLlenadoVigente {

    operator fun invoke(
        eventos: List<EventoLlenado>,
        iniciosDeAbastecimiento: List<LocalDateTime>,
        sinLlegada: List<LocalDateTime>,
        ahora: LocalDateTime
    ): EventoLlenado? {
        val ultimoReal = eventos
            .filter { it.origen == OrigenLlenado.REAL && it.momento <= ahora }
            .maxByOrNull { it.momento }
        val ventanas = iniciosDeAbastecimiento.filter { it <= ahora }.sorted()
        val sinConfirmar = ventanas
            .filter { ultimoReal == null || it > ultimoReal.momento }
            .filterNot { llegoSinAgua(it, ventanas, sinLlegada) }
        val asumida = sinConfirmar.lastOrNull() ?: return ultimoReal
        return EventoLlenado(asumida, TipoLlenado.COMPLETO, OrigenLlenado.ASUMIDO)
    }

    // "No llegó" cubre la ventana que estaba vigente en el momento de la declaración.
    private fun llegoSinAgua(
        inicio: LocalDateTime,
        ventanas: List<LocalDateTime>,
        sinLlegada: List<LocalDateTime>
    ): Boolean {
        val siguiente = ventanas.firstOrNull { it > inicio }
        return sinLlegada.any { it >= inicio && (siguiente == null || it < siguiente) }
    }
}
