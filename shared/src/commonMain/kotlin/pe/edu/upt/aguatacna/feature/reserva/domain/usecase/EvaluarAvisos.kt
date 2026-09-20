package pe.edu.upt.aguatacna.feature.reserva.domain.usecase

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Aviso
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfirmacionEstimacion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva

class EvaluarAvisos(private val calcularDeficit: CalcularDeficit = CalcularDeficit()) {

    /**
     * Pedir confirmación va primero: mientras el llenado es asumido, la proyección no es fiable
     * y avisar de un agotamiento sobre ella podría alarmar sin motivo.
     */
    operator fun invoke(reserva: Reserva?, proximoAbastecimiento: LocalDateTime?, ahora: LocalDateTime): Aviso? {
        if (reserva == null) return null
        if (reserva.confirmacion == ConfirmacionEstimacion.NO_CONFIRMADA) {
            return Aviso.ConfirmarLlenado(reserva.llenado.momento)
        }
        val deficit = calcularDeficit(reserva, proximoAbastecimiento) ?: return null
        val agotamiento = reserva.agotamientoProyectado()
        // Si ya se agotó, el usuario lo está viviendo: un aviso llegaría tarde.
        if (!deficit.hayDeficit || agotamiento <= ahora) return null
        return Aviso.AgotamientoAntesDelAbastecimiento(agotamiento, deficit)
    }
}
