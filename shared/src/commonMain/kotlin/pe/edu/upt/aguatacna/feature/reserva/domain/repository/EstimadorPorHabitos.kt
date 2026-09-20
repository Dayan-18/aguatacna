package pe.edu.upt.aguatacna.feature.reserva.domain.repository

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Habitantes
import pe.edu.upt.aguatacna.feature.reserva.domain.model.HabitosDelHogar

/** Contrato con feature/recibo: estima el consumo del hogar a partir de sus hábitos declarados. */
interface EstimadorPorHabitos {
    fun estimar(habitos: HabitosDelHogar, habitantes: Habitantes): ConsumoHorario?
}
