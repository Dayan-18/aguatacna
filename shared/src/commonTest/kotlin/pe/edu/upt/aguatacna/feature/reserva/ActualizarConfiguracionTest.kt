package pe.edu.upt.aguatacna.feature.reserva

import pe.edu.upt.aguatacna.feature.reserva.data.EstimadorPorHabitosProvisional
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoReservorio
import pe.edu.upt.aguatacna.feature.reserva.presentation.ActualizarConfiguracion
import pe.edu.upt.aguatacna.feature.reserva.presentation.CAPACIDAD_MAXIMA_LITROS
import pe.edu.upt.aguatacna.feature.reserva.presentation.CAPACIDAD_MINIMA_LITROS
import pe.edu.upt.aguatacna.feature.reserva.presentation.ConfiguracionEvent
import pe.edu.upt.aguatacna.feature.reserva.presentation.ConfiguracionUiState
import pe.edu.upt.aguatacna.feature.reserva.presentation.DUCHAS_MAXIMAS
import pe.edu.upt.aguatacna.feature.reserva.presentation.HABITANTES_MAXIMOS
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ActualizarConfiguracionTest {
    private val actualizar = ActualizarConfiguracion(EstimadorPorHabitosProvisional())
    private val inicial = ConfiguracionUiState()

    @Test
    fun elegirElTipoLoGuarda() {
        val estado = actualizar(inicial, ConfiguracionEvent.ElegirTipo(TipoReservorio.BIDONES))
        assertEquals(TipoReservorio.BIDONES, estado.tipo)
    }

    @Test
    fun laCapacidadNoSalePorDebajoNiPorEncimaDelRango() {
        assertEquals(CAPACIDAD_MINIMA_LITROS, actualizar(inicial, ConfiguracionEvent.CambiarCapacidad(10)).capacidadLitros)
        assertEquals(CAPACIDAD_MAXIMA_LITROS, actualizar(inicial, ConfiguracionEvent.CambiarCapacidad(99_999)).capacidadLitros)
        assertEquals(1500, actualizar(inicial, ConfiguracionEvent.CambiarCapacidad(1500)).capacidadLitros)
    }

    @Test // CA-03
    fun elHogarSiempreTieneAlMenosUnHabitante() {
        val uno = actualizar(inicial.copy(habitantes = 1), ConfiguracionEvent.CambiarHabitantes(-1))
        assertEquals(1, uno.habitantes)
    }

    @Test
    fun losHabitantesTienenUnMaximo() {
        val lleno = actualizar(inicial.copy(habitantes = HABITANTES_MAXIMOS), ConfiguracionEvent.CambiarHabitantes(1))
        assertEquals(HABITANTES_MAXIMOS, lleno.habitantes)
    }

    @Test
    fun lasDuchasNoSonNegativasNiPasanDelMaximo() {
        assertEquals(0, actualizar(inicial.copy(duchasPorDia = 0), ConfiguracionEvent.CambiarDuchas(-1)).duchasPorDia)
        assertEquals(DUCHAS_MAXIMAS, actualizar(inicial.copy(duchasPorDia = DUCHAS_MAXIMAS), ConfiguracionEvent.CambiarDuchas(1)).duchasPorDia)
    }

    @Test
    fun alternaLosInterruptores() {
        val sinLavadora = actualizar(inicial, ConfiguracionEvent.AlternarLavadora)
        assertEquals(!inicial.usaLavadora, sinLavadora.usaLavadora)
        assertEquals(!inicial.riegaJardin, actualizar(inicial, ConfiguracionEvent.AlternarRiego).riegaJardin)
    }

    @Test // CA-16
    fun cadaCambioRecalculaElConsumoEstimado() {
        val antes = assertNotNull(actualizar(inicial, ConfiguracionEvent.DescartarError).consumoEstimadoLitrosPorHora)
        val despues = assertNotNull(actualizar(inicial, ConfiguracionEvent.CambiarHabitantes(2)).consumoEstimadoLitrosPorHora)
        assertTrue(despues > antes)
    }

    @Test
    fun descartarElErrorLoLimpia() {
        assertNull(actualizar(inicial.copy(error = "falló"), ConfiguracionEvent.DescartarError).error)
    }

    @Test
    fun laConfiguracionSobreviveAIdaYVuelta() {
        val estado = inicial.copy(tipo = TipoReservorio.CISTERNA, capacidadLitros = 2000, habitantes = 6, duchasPorDia = 3)
        assertEquals(estado, ConfiguracionUiState.desde(estado.aConfiguracion()))
    }

    @Test
    fun continuarNoCambiaElFormulario() {
        assertFalse(actualizar(inicial, ConfiguracionEvent.Continuar).guardando)
    }
}
