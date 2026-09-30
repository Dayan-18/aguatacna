package pe.edu.upt.aguatacna.feature.recibo.domain.service

import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador

// Valida la coherencia de un borrador con los datos que tenga; las advertencias no bloquean la confirmación.
object ValidadorRecibo {

    data class Advertencia(
        val campo: String,
        val mensaje: String
    )

    private const val CONSUMO_MAX = 999
    private const val CONSUMO_MIN = 0

    fun validar(borrador: ReciboBorrador): List<Advertencia> = buildList {
        val consumo = borrador.consumoM3.valor
        if (consumo != null && consumo !in CONSUMO_MIN..CONSUMO_MAX) {
            add(Advertencia("consumoM3", "Consumo fuera de rango plausible: $consumo m³ (esperado 0–$CONSUMO_MAX)"))
        }

        val emision = borrador.fechaEmision.valor
        val vencimiento = borrador.fechaVencimiento.valor
        if (emision != null && vencimiento != null && vencimiento < emision) {
            add(Advertencia("fechaVencimiento", "La fecha de vencimiento es anterior a la de emisión"))
        }

        val anterior = borrador.lecturaAnteriorM3.valor ?: return@buildList
        val actual = borrador.lecturaActualM3.valor ?: return@buildList
        val diferencia = actual - anterior
        if (consumo != null && diferencia != consumo) {
            add(
                Advertencia(
                    "consumoM3",
                    "La diferencia de lecturas ($diferencia m³) no coincide con el consumo facturado ($consumo m³)"
                )
            )
        }
        if (actual < anterior) {
            add(Advertencia("lecturaActualM3", "La lectura actual es menor que la anterior"))
        }
    }
}
