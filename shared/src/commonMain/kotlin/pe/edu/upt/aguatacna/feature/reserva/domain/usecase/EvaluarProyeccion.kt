package pe.edu.upt.aguatacna.feature.reserva.domain.usecase

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EstadoProyeccion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.horasEntre

class EvaluarProyeccion {

    /** `null` si el sector no tiene cronograma: sin horario no se puede juzgar si alcanza. */
    operator fun invoke(reserva: Reserva, proximoAbastecimiento: LocalDateTime?): EstadoProyeccion? {
        if (proximoAbastecimiento == null) return null
        val sobrante = horasEntre(proximoAbastecimiento, reserva.agotamientoProyectado())
        return when {
            sobrante < 0.0 -> EstadoProyeccion.NO_ALCANZA
            sobrante < UMBRAL_MARGEN_HORAS -> EstadoProyeccion.AJUSTADA
            else -> EstadoProyeccion.COMODA
        }
    }

    private companion object {
        // Valor inicial; el Figma muestra "Reserva cómoda" sin definir el límite (spec, decisiones abiertas).
        const val UMBRAL_MARGEN_HORAS = 2.0
    }
}
