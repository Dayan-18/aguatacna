package pe.edu.upt.aguatacna.feature.sector

import pe.edu.upt.aguatacna.feature.sector.domain.usecase.ProximoAbastecimiento
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProximoAbastecimientoTest {

    private val proximo = ProximoAbastecimiento()
    private val semana = listOf(
        cronograma(PASADO, 5, 9),
        cronograma(HOY, 5, 9),
        cronograma(MANANA, 5, 9)
    )

    @Test // CA-04
    fun devuelveElInicioMasCercanoEnElFuturo() {
        val resultado = proximo.calcular(semana, ahora = momento(HOY, 20))
        assertEquals(momento(MANANA, 5), resultado?.inicio)
    }

    @Test // CA-05
    fun siElAguaEstaLlegandoDevuelveElDelDiaSiguiente() {
        val resultado = proximo.calcular(semana, ahora = momento(HOY, 6))
        assertEquals(MANANA, resultado?.fecha)
    }

    @Test // CA-06
    fun antesDelHorarioDeHoyDevuelveElDeHoy() {
        val resultado = proximo.calcular(semana, ahora = momento(HOY, 4))
        assertEquals(HOY, resultado?.fecha)
    }

    @Test // CA-07
    fun sinCronogramasFuturosDevuelveNull() {
        assertNull(proximo.calcular(semana, ahora = momento(PASADO, 10)))
        assertNull(proximo.calcular(emptyList(), ahora = momento(HOY, 10)))
    }
}
