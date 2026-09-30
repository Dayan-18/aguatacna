package pe.edu.upt.aguatacna.feature.recibo.infrastructure.ocr

import pe.edu.upt.aguatacna.feature.recibo.domain.model.LineaTexto
import kotlin.math.abs

// ML Kit agrupa el texto por bloques, y en las tablas del recibo la columna de etiquetas
// ("PROMEDIO m³") y la de valores ("23") salen en bloques distintos. Reunir las líneas que
// comparten altura devuelve cada etiqueta junto a su valor: "PROMEDIO m³  23".
internal fun reconstruirFilas(lineas: List<LineaTexto>): String {
    val filas = mutableListOf<MutableList<LineaTexto>>()
    for (linea in lineas.sortedBy { it.centroY }) {
        val fila = filas.lastOrNull()
        if (fila != null && perteneceA(fila, linea)) fila.add(linea) else filas.add(mutableListOf(linea))
    }
    return filas.joinToString("\n") { fila -> fila.sortedBy { it.x }.joinToString("  ") { it.texto } }
}

private val LineaTexto.centroY: Float get() = y + alto / 2

private fun perteneceA(fila: List<LineaTexto>, linea: LineaTexto): Boolean {
    val promedio = fila.map { it.centroY }.average().toFloat()
    val tolerancia = 0.5f * maxOf(linea.alto, fila.maxOf { it.alto })
    return abs(linea.centroY - promedio) <= tolerancia
}
