package pe.edu.upt.aguatacna.feature.reserva

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfirmacionEstimacion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.OrigenLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import kotlin.test.Test
import kotlin.test.assertEquals

class ReservaTest {

    private fun reserva(
        consumo: Double = 50.0,
        tipo: TipoLlenado = TipoLlenado.COMPLETO,
        origen: OrigenLlenado = OrigenLlenado.REAL
    ) = Reserva(CAPACIDAD_1000, ConsumoHorario(consumo), EventoLlenado(enHora(6), tipo, origen))

    @Test // CA-07
    fun elNivelBajaDeFormaLinealConElConsumo() {
        assertEquals(500.0, reserva().nivelEn(enHora(16)).litros.valor, absoluteTolerance = 0.001)
    }

    @Test // CA-07
    fun alMomentoDelLlenadoElNivelEsElInicial() {
        assertEquals(1000.0, reserva().nivelEn(enHora(6)).litros.valor, absoluteTolerance = 0.001)
    }

    @Test // CA-08
    fun elNivelNuncaEsMenorQueCero() {
        assertEquals(0.0, reserva().nivelEn(enHora(6 + 100)).litros.valor, absoluteTolerance = 0.001)
    }

    @Test // CA-09
    fun elAgotamientoEsElLlenadoMasLaCapacidadEntreElConsumo() {
        // 1000 L a 50 L/h duran 20 h: llenado a las 06:00, se agota a las 02:00 del día siguiente.
        assertEquals(enHora(26), reserva().agotamientoProyectado())
    }

    @Test // CA-09
    fun conLlenadoALaMitadDuraLaMitad() {
        assertEquals(enHora(16), reserva(tipo = TipoLlenado.MITAD).agotamientoProyectado())
    }

    @Test // CA-09
    fun elAgotamientoAdmiteHorasConFraccion() {
        // 1000 L a 300 L/h duran 3 h 20 min.
        val agotamiento = reserva(consumo = 300.0).agotamientoProyectado()
        assertEquals(enHora(9).date, agotamiento.date)
        assertEquals(9 * 3600 + 20 * 60, agotamiento.time.toSecondOfDay())
    }

    @Test // CA-13
    fun unLlenadoRealEstaConfirmado() {
        assertEquals(ConfirmacionEstimacion.CONFIRMADA, reserva().confirmacion)
    }

    @Test // CA-13
    fun unLlenadoAsumidoNoEstaConfirmado() {
        assertEquals(ConfirmacionEstimacion.NO_CONFIRMADA, reserva(origen = OrigenLlenado.ASUMIDO).confirmacion)
    }
}
