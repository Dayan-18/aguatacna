package pe.edu.upt.aguatacna.feature.sector

import kotlinx.datetime.LocalTime
import pe.edu.upt.aguatacna.feature.sector.domain.model.FuenteCronograma
import pe.edu.upt.aguatacna.feature.sector.domain.model.TipoConfirmacion.CORTE
import pe.edu.upt.aguatacna.feature.sector.domain.model.TipoConfirmacion.LLEGADA
import pe.edu.upt.aguatacna.feature.sector.domain.usecase.ConsolidarCronogramaColaborativo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ConsolidarCronogramaColaborativoTest {

    private val consolidar = ConsolidarCronogramaColaborativo()

    private val tresLlegadas = listOf(
        confirmacion(LLEGADA, 5, 8),
        confirmacion(LLEGADA, 5, 12),
        confirmacion(LLEGADA, 5, 15)
    )
    private val tresCortes = listOf(
        confirmacion(CORTE, 8, 55),
        confirmacion(CORTE, 9, 0),
        confirmacion(CORTE, 9, 5)
    )

    @Test // CA-12
    fun conPocasConfirmacionesNoEstimaHorario() {
        val soloDosCortes = tresLlegadas + tresCortes.take(2)
        assertNull(consolidar.consolidar("CN-04", HOY, soloDosCortes))
    }

    @Test // CA-13
    fun unReporteErroneoNoDesplazaElHorario() {
        val conError = tresLlegadas + confirmacion(LLEGADA, 15, 0) + tresCortes
        val resultado = consolidar.consolidar("CN-04", HOY, conError)
        // Mediana de 5:08, 5:12, 5:15 y 15:00 = 5:13:30. El promedio daría 7:38.
        assertEquals(LocalTime(5, 13, 30), resultado?.horaInicio)
        assertEquals(LocalTime(9, 0), resultado?.horaFin)
    }

    @Test // CA-14
    fun ignoraConfirmacionesDeOtroSectorYOtraFecha() {
        val ruido = listOf(
            confirmacion(LLEGADA, 11, 0, sectorId = "AA-02"),
            confirmacion(LLEGADA, 11, 0, fecha = MANANA)
        )
        val resultado = consolidar.consolidar("CN-04", HOY, tresLlegadas + tresCortes + ruido)
        assertEquals(LocalTime(5, 12), resultado?.horaInicio)
    }

    @Test // CA-15
    fun marcaElHorarioComoColaborativo() {
        val resultado = consolidar.consolidar("CN-04", HOY, tresLlegadas + tresCortes)
        assertEquals(FuenteCronograma.COLABORATIVA, resultado?.fuente)
    }

    @Test // CA-16
    fun siLaLlegadaNoEsAnteriorAlCorteNoEstimaHorario() {
        val invertido = listOf(
            confirmacion(LLEGADA, 10, 0),
            confirmacion(LLEGADA, 10, 5),
            confirmacion(LLEGADA, 10, 10),
            confirmacion(CORTE, 6, 0),
            confirmacion(CORTE, 6, 5),
            confirmacion(CORTE, 6, 10)
        )
        assertNull(consolidar.consolidar("CN-04", HOY, invertido))
    }
}
