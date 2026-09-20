package pe.edu.upt.aguatacna.feature.reserva

import pe.edu.upt.aguatacna.feature.reserva.domain.model.Litros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.NivelReserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NivelReservaTest {

    @Test // CA-06
    fun unLlenadoCompletoDejaLaCapacidadCompleta() {
        val nivel = NivelReserva.trasLlenado(CAPACIDAD_1000, TipoLlenado.COMPLETO)
        assertEquals(Litros(1000.0), nivel.litros)
        assertEquals(100.0, nivel.porcentaje, absoluteTolerance = 0.001)
    }

    @Test // CA-24
    fun llenarALaMitadDejaElCincuentaPorCiento() {
        val nivel = NivelReserva.trasLlenado(CAPACIDAD_1000, TipoLlenado.MITAD)
        assertEquals(Litros(500.0), nivel.litros)
        assertEquals(50.0, nivel.porcentaje, absoluteTolerance = 0.001)
    }

    @Test
    fun elNivelNoPuedeSuperarLaCapacidad() {
        assertFailsWith<IllegalArgumentException> { NivelReserva(Litros(1200.0), CAPACIDAD_1000) }
    }
}
