package pe.edu.upt.aguatacna.feature.reserva.domain.model

import kotlin.jvm.JvmInline

@JvmInline
value class CapacidadLitros(val litros: Litros) {
    init {
        require(litros > Litros.CERO) { "La capacidad del reservorio debe ser mayor a 0 litros" }
    }

    companion object {
        fun deLitros(valor: Double): CapacidadLitros = CapacidadLitros(Litros(valor))
    }
}
