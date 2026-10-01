package pe.edu.upt.aguatacna.feature.recibo.domain.usecase

import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador

sealed interface ResultadoCorreccion {
    data class Aplicada(val borrador: ReciboBorrador) : ResultadoCorreccion
    data class Invalida(val mensaje: String) : ResultadoCorreccion
}

// Valida y corrige un campo del borrador, marcándolo como corregidoPorUsuario = true.
// Un valor null significa que el usuario escribió algo que no se pudo interpretar.
class CorregirCampoUseCase {

    sealed class CampoEditable {
        data class ConsumoM3(val valor: Int?) : CampoEditable()
        data class LecturaAnterior(val valor: Int?) : CampoEditable()
        data class LecturaActual(val valor: Int?) : CampoEditable()
        data class Importe(val valor: Dinero?) : CampoEditable()
        data class Periodo(val valor: PeriodoConsumo) : CampoEditable()
    }

    operator fun invoke(borrador: ReciboBorrador, campo: CampoEditable): ResultadoCorreccion {
        val error = validar(borrador, campo)
        if (error != null) return ResultadoCorreccion.Invalida(error)
        val corregido = when (campo) {
            is CampoEditable.ConsumoM3 -> borrador.copy(consumoM3 = borrador.consumoM3.corregir(campo.valor!!))
            is CampoEditable.LecturaAnterior ->
                borrador.copy(lecturaAnteriorM3 = borrador.lecturaAnteriorM3.corregir(campo.valor!!)).conConsumoDeLecturas()
            is CampoEditable.LecturaActual ->
                borrador.copy(lecturaActualM3 = borrador.lecturaActualM3.corregir(campo.valor!!)).conConsumoDeLecturas()
            is CampoEditable.Importe -> borrador.copy(importeTotal = borrador.importeTotal.corregir(campo.valor!!))
            is CampoEditable.Periodo -> borrador.copy(periodoConsumo = borrador.periodoConsumo.corregir(campo.valor))
        }
        return ResultadoCorreccion.Aplicada(corregido)
    }

    // Al corregir una lectura, el consumo pasa a ser la diferencia entre ambas para que no queden desfasados.
    private fun ReciboBorrador.conConsumoDeLecturas(): ReciboBorrador {
        val anterior = lecturaAnteriorM3.valor ?: return this
        val actual = lecturaActualM3.valor ?: return this
        val diferencia = actual - anterior
        return if (diferencia in 0..CONSUMO_MAX) copy(consumoM3 = consumoM3.corregir(diferencia)) else this
    }

    private fun validar(borrador: ReciboBorrador, campo: CampoEditable): String? = when (campo) {
        is CampoEditable.ConsumoM3 ->
            if (campo.valor == null || campo.valor !in 0..CONSUMO_MAX) "Ingresa un consumo válido entre 0 y $CONSUMO_MAX m³" else null
        is CampoEditable.LecturaAnterior -> {
            val actual = borrador.lecturaActualM3.valor
            when {
                campo.valor == null || campo.valor < 0 -> "Ingresa una lectura válida"
                actual != null && campo.valor > actual -> "La lectura anterior no puede superar la actual ($actual m³)"
                else -> null
            }
        }
        is CampoEditable.LecturaActual -> {
            val anterior = borrador.lecturaAnteriorM3.valor
            when {
                campo.valor == null || campo.valor < 0 -> "Ingresa una lectura válida"
                anterior != null && campo.valor < anterior ->
                    "La lectura actual debe ser mayor o igual a la anterior ($anterior m³)"
                else -> null
            }
        }
        is CampoEditable.Importe ->
            if (campo.valor == null || campo.valor <= Dinero.CERO || campo.valor > IMPORTE_MAX) {
                "Ingresa un importe válido mayor a S/ 0"
            } else null
        is CampoEditable.Periodo -> null
    }

    private companion object {
        const val CONSUMO_MAX = 999
        val IMPORTE_MAX = Dinero(9_999_900L)
    }
}
