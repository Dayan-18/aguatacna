package pe.edu.upt.aguatacna.feature.reserva.domain.model

import kotlin.jvm.JvmInline

@JvmInline
value class ConsumoHorario(val litrosPorHora: Double) {
    init {
        require(litrosPorHora.isFinite() && litrosPorHora > 0.0) {
            "El consumo horario debe ser mayor a 0: $litrosPorHora"
        }
    }
}

@JvmInline
value class LitrosPorHabitanteDia(val valor: Double) {
    init {
        require(valor.isFinite() && valor >= 0.0) { "Litros por habitante y día inválido: $valor" }
    }
}
