package pe.edu.upt.aguatacna.feature.reserva

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ClaseIntervalo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.IntervaloConsumo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Litros
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IntervaloConsumoTest {

    @Test // CA-04
    fun milLitrosEnCuatroDiasSonDoscientosCincuentaPorDia() {
        assertEquals(250.0, intervalo(0, 96).litrosPorDia, absoluteTolerance = 0.001)
    }

    @Test
    fun elConsumoHorarioEsLitrosEntreHoras() {
        assertEquals(1000.0 / 96, intervalo(0, 96).consumoHorario.litrosPorHora, absoluteTolerance = 0.001)
    }

    @Test
    fun rechazaUnIntervaloQueTerminaAntesDeEmpezar() {
        assertFailsWith<IllegalArgumentException> {
            IntervaloConsumo(enHora(10), enHora(5), Litros(100.0), ClaseIntervalo.POR_LLENADO)
        }
    }

    @Test
    fun rechazaUnIntervaloSinConsumo() {
        assertFailsWith<IllegalArgumentException> {
            IntervaloConsumo(enHora(0), enHora(5), Litros.CERO, ClaseIntervalo.POR_LLENADO)
        }
    }
}
