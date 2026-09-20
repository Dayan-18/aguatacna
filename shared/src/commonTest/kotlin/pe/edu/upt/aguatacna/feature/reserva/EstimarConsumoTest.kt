package pe.edu.upt.aguatacna.feature.reserva

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ClaseIntervalo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.EstimarConsumo
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.OrigenEstimacion
import kotlin.test.Test
import kotlin.test.assertEquals

class EstimarConsumoTest {
    private val estimar = EstimarConsumo()

    @Test // CA-15
    fun usaLaMedianaDeLosIntervalos() {
        // 1000 L en 20 h, 25 h y 40 h dan 50, 40 y 25 L/h: la mediana es 40.
        val estimacion = estimar(intervalosDe(20, 25, 40), CAPACIDAD_1000)
        assertEquals(40.0, estimacion.consumo.litrosPorHora, absoluteTolerance = 0.001)
        assertEquals(OrigenEstimacion.HISTORICO, estimacion.origen)
    }

    @Test // CA-15
    fun unIntervaloViejoYaNoInfluye() {
        // El más antiguo consume 100 L/h; los cinco recientes, 1000/24 L/h.
        val intervalos = listOf(intervalo(0, 10)) + intervalosDesde(10, 24, 24, 24, 24, 24)
        val estimacion = estimar(intervalos, CAPACIDAD_1000)
        assertEquals(1000.0 / 24, estimacion.consumo.litrosPorHora, absoluteTolerance = 0.001)
    }

    @Test // CA-16
    fun conUnSoloIntervaloInferidoUsaLosHabitos() {
        val estimacion = estimar(intervalosDe(24), CAPACIDAD_1000, porHabitos = ConsumoHorario(60.0))
        assertEquals(60.0, estimacion.consumo.litrosPorHora, absoluteTolerance = 0.001)
        assertEquals(OrigenEstimacion.HABITOS, estimacion.origen)
    }

    @Test // CA-17
    fun sinNadaUsaLaCapacidadEntreDosDias() {
        val estimacion = estimar(emptyList(), CAPACIDAD_1000)
        assertEquals(1000.0 / 48, estimacion.consumo.litrosPorHora, absoluteTolerance = 0.001)
        assertEquals(OrigenEstimacion.ESTIMACION_INICIAL, estimacion.origen)
    }

    @Test // CA-28
    fun unSoloIntervaloObservadoAlcanza() {
        val observado = intervalo(48, 60, clase = ClaseIntervalo.OBSERVADO)
        val estimacion = estimar(intervalosDe(24, 24) + observado, CAPACIDAD_1000)
        assertEquals(1000.0 / 12, estimacion.consumo.litrosPorHora, absoluteTolerance = 0.001)
        assertEquals(OrigenEstimacion.HISTORICO, estimacion.origen)
    }
}
