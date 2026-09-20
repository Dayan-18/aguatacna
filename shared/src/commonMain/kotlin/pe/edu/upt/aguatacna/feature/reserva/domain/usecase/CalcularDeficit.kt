package pe.edu.upt.aguatacna.feature.reserva.domain.usecase

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Deficit
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.horasEntre

class CalcularDeficit {

    /**
     * Horas entre el agotamiento y el próximo abastecimiento del sector.
     * Devuelve `null` si el sector no tiene cronograma: sin horario no hay déficit que calcular.
     */
    operator fun invoke(reserva: Reserva, proximoAbastecimiento: LocalDateTime?): Deficit? {
        if (proximoAbastecimiento == null) return null
        val horasSinAgua = horasEntre(reserva.agotamientoProyectado(), proximoAbastecimiento)
        return Deficit(horasSinAgua.coerceAtLeast(0.0))
    }
}
