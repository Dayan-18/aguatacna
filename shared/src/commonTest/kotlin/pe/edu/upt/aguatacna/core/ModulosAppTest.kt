package pe.edu.upt.aguatacna.core

import org.koin.core.context.stopKoin
import pe.edu.upt.aguatacna.core.di.iniciarKoin
import pe.edu.upt.aguatacna.core.di.modulosApp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ModulosAppTest {

    @Test
    fun elCoreYCadaFeatureTienenSuModuloDeInyeccion() {
        assertEquals(6, modulosApp.size)
    }

    @Test
    fun laInyeccionArrancaConLosModulosDeLasFeatures() {
        val aplicacion = iniciarKoin()
        try {
            assertNotNull(aplicacion.koin)
        } finally {
            stopKoin()
        }
    }
}
