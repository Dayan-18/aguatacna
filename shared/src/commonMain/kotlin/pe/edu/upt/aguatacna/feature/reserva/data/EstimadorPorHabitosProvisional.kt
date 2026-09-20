package pe.edu.upt.aguatacna.feature.reserva.data

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Habitantes
import pe.edu.upt.aguatacna.feature.reserva.domain.model.HabitosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.EstimadorPorHabitos

// Provisional: los coeficientes son órdenes de magnitud propios, no una fuente. Se reemplaza por el
// estimador de feature/recibo (Iker) cuando tenga los coeficientes por actividad.
class EstimadorPorHabitosProvisional : EstimadorPorHabitos {

    override fun estimar(habitos: HabitosDelHogar, habitantes: Habitantes): ConsumoHorario {
        val porPersona = LITROS_BASE_POR_PERSONA + habitos.duchasPorDia * LITROS_POR_DUCHA
        val diario = habitantes.cantidad * porPersona +
            (if (habitos.usaLavadora) LITROS_LAVADORA else 0.0) +
            (if (habitos.riegaJardin) LITROS_RIEGO else 0.0)
        return ConsumoHorario(diario / HORAS_DE_USO_AL_DIA)
    }

    private companion object {
        const val LITROS_BASE_POR_PERSONA = 60.0
        const val LITROS_POR_DUCHA = 30.0
        const val LITROS_LAVADORA = 100.0
        const val LITROS_RIEGO = 150.0
        const val HORAS_DE_USO_AL_DIA = 12.0
    }
}
