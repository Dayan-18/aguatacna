package pe.edu.upt.aguatacna.feature.reserva.domain.usecase

import pe.edu.upt.aguatacna.feature.reserva.domain.model.Deficit
import pe.edu.upt.aguatacna.feature.reserva.domain.model.HabitosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Recomendacion

class SugerirRecortes {

    /** Sin déficit no hay nada que recortar; con déficit, de la que más ahorra a la que menos. */
    operator fun invoke(deficit: Deficit?, habitos: HabitosDelHogar): List<Recomendacion> {
        if (deficit == null || !deficit.hayDeficit) return emptyList()
        return Recomendacion.entries
            .filter { it.correspondeA(habitos) }
            .sortedByDescending { it.litrosQueAhorra }
    }
}
