package pe.edu.upt.aguatacna.feature.sector.domain.usecase

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.sector.domain.model.Cronograma

class ObtenerCronogramaVigente {

    fun obtener(cronogramas: List<Cronograma>, ahora: LocalDateTime): Cronograma? =
        cronogramas.firstOrNull { it.contiene(ahora) }
}
