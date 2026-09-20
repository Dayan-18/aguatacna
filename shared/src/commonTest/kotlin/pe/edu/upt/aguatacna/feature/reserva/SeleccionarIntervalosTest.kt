package pe.edu.upt.aguatacna.feature.reserva

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ClaseIntervalo
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.SeleccionarIntervalos
import kotlin.test.Test
import kotlin.test.assertEquals

class SeleccionarIntervalosTest {
    private val seleccionar = SeleccionarIntervalos()

    @Test // CA-15
    fun usaSoloLosUltimosCincoIntervalos() {
        val seis = intervalosDe(24, 24, 24, 24, 24, 24)
        assertEquals(seis.takeLast(5), seleccionar(seis))
    }

    @Test // CA-14
    fun descartaUnIntervaloMayorAlDobleDeLaMediana() {
        val validos = seleccionar(intervalosDe(24, 24, 24, 72, 72))
        assertEquals(3, validos.size)
    }

    @Test // CA-14
    fun conservaUnIntervaloJustoEnElDobleDeLaMediana() {
        assertEquals(4, seleccionar(intervalosDe(24, 24, 24, 48)).size)
    }

    @Test // CA-28
    fun siHayObservadosUsaSoloEsos() {
        val inferidos = intervalosDe(24, 24)
        val observado = intervalo(48, 60, clase = ClaseIntervalo.OBSERVADO)
        assertEquals(listOf(observado), seleccionar(inferidos + observado))
    }

    @Test
    fun sinIntervalosDevuelveVacio() {
        assertEquals(emptyList(), seleccionar(emptyList()))
    }
}
