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

    @Test // CA-35
    fun descartaElIntervaloEntreDosLlenadosMuySeguidos() {
        val normales = intervalosDe(24, 24)
        assertEquals(normales, seleccionar(normales + intervalo(48, 53)))
    }

    @Test // CA-35
    fun conservaUnIntervaloInferidoDeSeisHoras() {
        assertEquals(1, seleccionar(listOf(intervalo(0, 6))).size)
    }

    @Test // CA-35
    fun unObservadoCortoSiCuenta() {
        val observado = intervalo(0, 2, clase = ClaseIntervalo.OBSERVADO)
        assertEquals(listOf(observado), seleccionar(listOf(observado)))
    }

    @Test // CA-36
    fun unInferidoSoloNoAlcanzaPeroDosSi() {
        assertEquals(false, seleccionar.alcanzanParaEstimar(intervalosDe(24)))
        assertEquals(true, seleccionar.alcanzanParaEstimar(intervalosDe(24, 24)))
        assertEquals(true, seleccionar.alcanzanParaEstimar(listOf(intervalo(0, 24, clase = ClaseIntervalo.OBSERVADO))))
    }

    @Test
    fun sinIntervalosDevuelveVacio() {
        assertEquals(emptyList(), seleccionar(emptyList()))
    }
}
