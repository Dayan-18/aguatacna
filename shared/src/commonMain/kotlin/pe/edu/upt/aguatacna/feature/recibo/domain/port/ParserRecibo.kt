package pe.edu.upt.aguatacna.feature.recibo.domain.port

import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TextoReconocido

// Resultado de analizar el texto OCR de un recibo de agua.
sealed interface ResultadoParseo {
    data class Exito(val borrador: ReciboBorrador) : ResultadoParseo
    data class NoLegible(val motivo: String) : ResultadoParseo
}

// Puerto de dominio: analiza el texto reconocido y arma un borrador de recibo.
interface ParserRecibo {
    fun parsear(texto: TextoReconocido): ResultadoParseo
    fun parsearTexto(textoOriginal: String): ResultadoParseo
}
