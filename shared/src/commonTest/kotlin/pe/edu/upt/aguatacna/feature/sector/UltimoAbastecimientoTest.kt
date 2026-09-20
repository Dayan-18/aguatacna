package pe.edu.upt.aguatacna.feature.sector

import pe.edu.upt.aguatacna.feature.sector.domain.usecase.UltimoAbastecimiento
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UltimoAbastecimientoTest {

    private val ultimo = UltimoAbastecimiento()
    private val semana = listOf(
        cronograma(AYER, 5, 9),
        cronograma(HOY, 5, 9),
        cronograma(MANANA, 5, 9)
    )

    @Test // CA-19
    fun devuelveElAbastecimientoMasRecienteYaEmpezado() {
        val resultado = ultimo.calcular(semana, ahora = momento(HOY, 20))
        assertEquals(momento(HOY, 5), resultado?.inicio)
    }

    @Test // CA-20
    fun siElAguaEstaLlegandoDevuelveElDeAhora() {
        val resultado = ultimo.calcular(semana, ahora = momento(HOY, 6))
        assertEquals(HOY, resultado?.fecha)
    }

    @Test // CA-21
    fun antesDelHorarioDeHoyDevuelveElDeAyer() {
        val resultado = ultimo.calcular(semana, ahora = momento(HOY, 4))
        assertEquals(AYER, resultado?.fecha)
    }

    @Test // CA-22
    fun sinAbastecimientosPreviosDevuelveNull() {
        assertNull(ultimo.calcular(semana, ahora = momento(AYER, 4)))
        assertNull(ultimo.calcular(emptyList(), ahora = momento(HOY, 10)))
    }
}
