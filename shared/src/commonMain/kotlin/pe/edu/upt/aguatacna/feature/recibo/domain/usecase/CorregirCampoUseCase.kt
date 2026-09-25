package pe.edu.upt.aguatacna.feature.recibo.domain.usecase

import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador

// Corrige un campo del borrador y lo marca como corregidoPorUsuario = true.
class CorregirCampoUseCase {

    sealed class CampoEditable {
        data class ConsumoM3(val valor: Int) : CampoEditable()
        data class LecturaAnterior(val valor: Int) : CampoEditable()
        data class LecturaActual(val valor: Int) : CampoEditable()
        data class Importe(val valor: Long) : CampoEditable() // céntimos
        data class Periodo(val valor: PeriodoConsumo) : CampoEditable()
    }

    operator fun invoke(borrador: ReciboBorrador, campo: CampoEditable): ReciboBorrador {
        return when (campo) {
            is CampoEditable.ConsumoM3 -> borrador.copy(
                consumoM3 = borrador.consumoM3.copy(
                    valor = campo.valor,
                    confianza = 1f,
                    corregidoPorUsuario = true
                )
            )
            is CampoEditable.LecturaAnterior -> borrador.copy(
                lecturaAnteriorM3 = borrador.lecturaAnteriorM3.copy(
                    valor = campo.valor,
                    confianza = 1f,
                    corregidoPorUsuario = true
                )
            )
            is CampoEditable.LecturaActual -> borrador.copy(
                lecturaActualM3 = borrador.lecturaActualM3.copy(
                    valor = campo.valor,
                    confianza = 1f,
                    corregidoPorUsuario = true
                )
            )
            is CampoEditable.Importe -> borrador.copy(
                importeTotal = borrador.importeTotal.copy(
                    valor = Dinero(campo.valor),
                    confianza = 1f,
                    corregidoPorUsuario = true
                )
            )
            is CampoEditable.Periodo -> borrador.copy(
                periodoConsumo = borrador.periodoConsumo.copy(
                    valor = campo.valor,
                    confianza = 1f,
                    corregidoPorUsuario = true
                )
            )
        }
    }
}
