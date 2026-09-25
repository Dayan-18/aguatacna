package pe.edu.upt.aguatacna.feature.recibo.domain.service

import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo

// Valida la coherencia de un recibo; las advertencias no bloquean la confirmación.
object ValidadorRecibo {

    data class Advertencia(
        val campo: String,
        val mensaje: String
    )

    private const val CONSUMO_MAX = 999
    private const val CONSUMO_MIN = 0

    fun validar(recibo: Recibo): List<Advertencia> {
        val advertencias = mutableListOf<Advertencia>()

        // Consumo dentro de rango 0..999
        if (recibo.consumoM3 !in CONSUMO_MIN..CONSUMO_MAX) {
            advertencias += Advertencia(
                "consumoM3",
                "Consumo fuera de rango plausible: ${recibo.consumoM3} m³ (esperado 0–$CONSUMO_MAX)"
            )
        }

        // Vencimiento ≥ emisión
        val emision = recibo.fechaEmision
        val vencimiento = recibo.fechaVencimiento
        if (emision != null && vencimiento != null && vencimiento < emision) {
            advertencias += Advertencia(
                "fechaVencimiento",
                "La fecha de vencimiento es anterior a la de emisión"
            )
        }

        // Si hay lecturas: actual - anterior = consumo
        val anterior = recibo.lecturaAnteriorM3
        val actual = recibo.lecturaActualM3
        if (anterior != null && actual != null) {
            val diferencia = actual - anterior
            if (diferencia != recibo.consumoM3) {
                advertencias += Advertencia(
                    "consumoM3",
                    "La diferencia de lecturas ($diferencia m³) no coincide con el consumo facturado (${recibo.consumoM3} m³)"
                )
            }
            if (actual < anterior) {
                advertencias += Advertencia(
                    "lecturaActualM3",
                    "La lectura actual es menor que la anterior"
                )
            }
        }

        return advertencias
    }
}
