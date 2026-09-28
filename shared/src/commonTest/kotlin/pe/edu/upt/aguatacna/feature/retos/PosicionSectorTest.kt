package pe.edu.upt.aguatacna.feature.retos

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pe.edu.upt.aguatacna.feature.retos.domain.model.LitrosPorHabitanteDia
import pe.edu.upt.aguatacna.feature.retos.domain.model.PosicionSector

class PosicionSectorTest {
    @Test
    fun identifica_cuando_el_hogar_ahorra_mas_que_el_promedio() {
        val posicion = PosicionSector(LitrosPorHabitanteDia(92.0), LitrosPorHabitanteDia(118.0))

        assertTrue(posicion.ahorraMasQueElPromedio)
        assertEquals(-26.0, posicion.diferencia)
    }
}
