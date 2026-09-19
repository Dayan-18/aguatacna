package pe.edu.upt.aguatacna.feature.reserva

import pe.edu.upt.aguatacna.feature.reserva.domain.model.CapacidadLitros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Habitantes
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Litros
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LitrosTest {

    @Test // CA-01
    fun rechazaUnaCapacidadDeCeroLitros() {
        assertFailsWith<IllegalArgumentException> { CapacidadLitros.deLitros(0.0) }
    }

    @Test // CA-01
    fun aceptaUnaCapacidadPositiva() {
        assertEquals(Litros(1000.0), CapacidadLitros.deLitros(1000.0).litros)
    }

    @Test // CA-02
    fun rechazaLitrosNegativos() {
        assertFailsWith<IllegalArgumentException> { Litros(-1.0) }
    }

    @Test // CA-02
    fun rechazaLitrosQueNoSonUnNumero() {
        assertFailsWith<IllegalArgumentException> { Litros(Double.NaN) }
    }

    @Test // CA-03
    fun rechazaUnHogarSinHabitantes() {
        assertFailsWith<IllegalArgumentException> { Habitantes(0) }
    }

    @Test // CA-08
    fun restarMasDeLoQueHayDejaElTanqueVacio() {
        assertEquals(Litros.CERO, Litros(100.0) - Litros(250.0))
    }
}
