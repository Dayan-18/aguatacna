package pe.edu.upt.aguatacna.feature.reserva.domain.model

import kotlin.jvm.JvmInline

@JvmInline
value class Litros(val valor: Double) : Comparable<Litros> {
    init {
        require(valor.isFinite() && valor >= 0.0) { "Los litros deben ser un número finito y no negativo: $valor" }
    }

    operator fun plus(otro: Litros): Litros = Litros(valor + otro.valor)

    // Restar más de lo que hay no da litros negativos: el tanque queda vacío.
    operator fun minus(otro: Litros): Litros = Litros((valor - otro.valor).coerceAtLeast(0.0))

    operator fun times(factor: Double): Litros = Litros(valor * factor)

    operator fun div(otro: Litros): Double = valor / otro.valor

    override fun compareTo(other: Litros): Int = valor.compareTo(other.valor)

    companion object {
        val CERO = Litros(0.0)
    }
}
