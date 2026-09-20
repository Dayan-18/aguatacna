package pe.edu.upt.aguatacna.feature.sector.domain.usecase

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.sector.domain.model.Cronograma

// Contrato que consume feature/reserva: cuando el usuario no registra el llenado,
// se asume que el tanque se llenó al inicio de este abastecimiento y la
// estimación queda marcada como no confirmada (anteproyecto §11).
class UltimoAbastecimiento {

    fun calcular(cronogramas: List<Cronograma>, ahora: LocalDateTime): Cronograma? =
        cronogramas
            .filter { it.inicio <= ahora }
            .maxByOrNull { it.inicio }
}
