package pe.edu.upt.aguatacna.feature.sector.domain.usecase

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import pe.edu.upt.aguatacna.feature.sector.domain.model.ConfirmacionHorario
import pe.edu.upt.aguatacna.feature.sector.domain.model.Cronograma
import pe.edu.upt.aguatacna.feature.sector.domain.model.FuenteCronograma
import pe.edu.upt.aguatacna.feature.sector.domain.model.TipoConfirmacion
import pe.edu.upt.aguatacna.feature.sector.domain.model.TipoCronograma

class ConsolidarCronogramaColaborativo {

    fun consolidar(
        sectorId: String,
        fecha: LocalDate,
        confirmaciones: List<ConfirmacionHorario>
    ): Cronograma? {
        val delDia = confirmaciones.filter { it.sectorId == sectorId && it.momento.date == fecha }
        val llegadas = delDia.filter { it.tipo == TipoConfirmacion.LLEGADA }
        val cortes = delDia.filter { it.tipo == TipoConfirmacion.CORTE }

        if (llegadas.size < MINIMO_CONFIRMACIONES || cortes.size < MINIMO_CONFIRMACIONES) return null

        val inicio = mediana(llegadas)
        val fin = mediana(cortes)
        if (inicio >= fin) return null

        return Cronograma(
            id = "colaborativo-$sectorId-$fecha",
            sectorId = sectorId,
            fecha = fecha,
            horaInicio = inicio,
            horaFin = fin,
            tipo = TipoCronograma.PROGRAMADO,
            fuente = FuenteCronograma.COLABORATIVA
        )
    }

    // Mediana y no promedio: si un vecino marca por error las 3 p.m.,
    // el promedio se desplaza horas; la mediana casi no se mueve.
    private fun mediana(confirmaciones: List<ConfirmacionHorario>): LocalTime {
        val segundos = confirmaciones.map { it.momento.time.toSecondOfDay() }.sorted()
        val medio = segundos.size / 2
        val valor = if (segundos.size % 2 == 0) (segundos[medio - 1] + segundos[medio]) / 2 else segundos[medio]
        return LocalTime.fromSecondOfDay(valor)
    }

    companion object {
        const val MINIMO_CONFIRMACIONES = 3
    }
}
