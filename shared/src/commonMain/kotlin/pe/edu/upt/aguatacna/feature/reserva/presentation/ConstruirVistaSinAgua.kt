package pe.edu.upt.aguatacna.feature.reserva.presentation

import pe.edu.upt.aguatacna.feature.reserva.domain.model.PrevisualizacionSinAgua
import kotlin.math.abs
import kotlin.math.roundToInt

private const val MINUTO_EN_HORAS = 1.0 / 60

class ConstruirVistaSinAgua {

    operator fun invoke(previsualizacion: PrevisualizacionSinAgua): SinAguaVista {
        val adelanto = previsualizacion.horasAntesDeLoPrevisto
        return SinAguaVista(
            textoProyectado = describirHora(previsualizacion.agotamientoProyectado, previsualizacion.momento),
            textoSeAcabo = describirHora(previsualizacion.momento, previsualizacion.momento),
            textoDiferencia = describirDiferencia(adelanto),
            seAcaboAntes = adelanto >= MINUTO_EN_HORAS,
            textoConsumo = describirConsumo(previsualizacion)
        )
    }

    private fun describirDiferencia(horas: Double): String = when {
        abs(horas) < MINUTO_EN_HORAS -> "justo a tiempo"
        horas > 0 -> "${formatearDuracion(horas)} antes"
        else -> "${formatearDuracion(-horas)} después"
    }

    private fun describirConsumo(previsualizacion: PrevisualizacionSinAgua): String {
        val antes = previsualizacion.consumoActual.litrosPorHora.roundToInt()
        val despues = previsualizacion.consumoNuevo.litrosPorHora.roundToInt()
        return if (antes == despues) "$antes L/h" else "$antes → $despues L/h"
    }
}
