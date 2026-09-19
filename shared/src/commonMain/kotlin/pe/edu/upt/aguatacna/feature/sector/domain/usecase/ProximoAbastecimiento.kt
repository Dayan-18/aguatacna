package pe.edu.upt.aguatacna.feature.sector.domain.usecase

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.sector.domain.model.Cronograma

// Contrato que consume feature/reserva para calcular el déficit:
// el inicio del cronograma devuelto es "cuándo vuelve el agua".
class ProximoAbastecimiento {

    fun calcular(cronogramas: List<Cronograma>, ahora: LocalDateTime): Cronograma? =
        cronogramas
            .filter { it.inicio > ahora }
            .minByOrNull { it.inicio }
}
