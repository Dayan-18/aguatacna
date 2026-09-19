package pe.edu.upt.aguatacna.feature.sector

import pe.edu.upt.aguatacna.feature.sector.domain.usecase.ObtenerCronogramaVigente
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ObtenerCronogramaVigenteTest {

    private val vigente = ObtenerCronogramaVigente()
    private val cronogramas = listOf(cronograma(HOY, 5, 9), cronograma(MANANA, 5, 9))

    @Test // CA-08
    fun devuelveElCronogramaEnCurso() {
        assertEquals(HOY, vigente.obtener(cronogramas, momento(HOY, 7))?.fecha)
    }

    @Test // CA-08
    fun fueraDeHorarioDevuelveNull() {
        assertNull(vigente.obtener(cronogramas, momento(HOY, 14)))
    }
}
