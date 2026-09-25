package pe.edu.upt.aguatacna.feature.recibo.infrastructure.ocr

import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TextoReconocido
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TipoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.port.ParserRecibo
import pe.edu.upt.aguatacna.feature.recibo.domain.port.ResultadoParseo

// Parser de texto OCR para recibos de EPS Tacna: normaliza, extrae campos por regex/palabras
// clave y valida los mínimos. Nunca extrae datos personales (nombre, DNI, dirección, contraseñas).
object ParserReciboEpsTacna : ParserRecibo {

    override fun parsear(texto: TextoReconocido): ResultadoParseo =
        parsearTexto(texto.textoPlano)

    override fun parsearTexto(textoOriginal: String): ResultadoParseo {
        if (textoOriginal.isBlank()) {
            return ResultadoParseo.NoLegible("El texto del recibo está vacío.")
        }

        val normalizado = normalizar(textoOriginal)

        // Extracción campo por campo
        val periodo = extraerPeriodoConsumo(normalizado)
        val consumo = extraerConsumoM3(normalizado)
        val importe = extraerImporteTotal(normalizado)
        val tipoConsumo = extraerTipoConsumo(normalizado)
        val emision = extraerFechaEmision(normalizado)
        val vencimiento = extraerFechaVencimiento(normalizado)
        val lecturaAnterior = extraerLecturaAnterior(normalizado)
        val lecturaActual = extraerLecturaActual(normalizado)
        val medidor = extraerNumeroMedidor(normalizado)
        val numRecibo = extraerNumeroRecibo(normalizado)

        val borrador = ReciboBorrador(
            periodoConsumo = periodo,
            consumoM3 = consumo,
            importeTotal = importe,
            fechaEmision = emision,
            fechaVencimiento = vencimiento,
            tipoConsumo = tipoConsumo,
            lecturaAnteriorM3 = lecturaAnterior,
            lecturaActualM3 = lecturaActual,
            numeroMedidor = medidor,
            numeroRecibo = numRecibo,
            origen = OrigenDatos.ESCANEADO
        )

        // Validación de campos clave mínimos
        if (consumo.valor == null && importe.valor == null) {
            return ResultadoParseo.NoLegible("No pudimos leer el consumo ni el importe total de tu recibo.")
        }

        return ResultadoParseo.Exito(borrador)
    }

    // Limpieza y normalización de texto antes de extraer campos.
    fun normalizar(texto: String): String {
        return texto.uppercase()
            .replace('Á', 'A')
            .replace('É', 'E')
            .replace('Í', 'I')
            .replace('Ó', 'O')
            .replace('Ú', 'U')
            .replace("M3", "M³")
            .replace("*", "")
    }

    // ─────────────────────────────────────────────────────────────
    // Extractores individuales
    // ─────────────────────────────────────────────────────────────

    private fun extraerPeriodoConsumo(texto: String): Campo<PeriodoConsumo> {
        // Ejemplo: "CONSUMO: AGOSTO-2026" o "CONSUMO AGOSTO 2026"
        val regex = Regex("""CONSUMO\s*[:.]?\s*([A-Z]+[- /]\d{4})""")
        val match = regex.find(texto)
        if (match != null) {
            val periodo = PeriodoConsumo.parsear(match.groupValues[1])
            if (periodo != null) {
                return Campo(valor = periodo, confianza = 0.95f)
            }
        }

        // Fallback: buscar cualquier "MES-AÑO" presente en el texto
        val fallbackRegex = Regex("""\b([A-Z]{4,10}[- /]\d{4})\b""")
        for (m in fallbackRegex.findAll(texto)) {
            val periodo = PeriodoConsumo.parsear(m.groupValues[1])
            if (periodo != null) {
                return Campo(valor = periodo, confianza = 0.70f)
            }
        }

        return Campo(valor = null, confianza = 0f)
    }

    private fun extraerConsumoM3(texto: String): Campo<Int> {
        // Ejemplo: "VOLUMEN FAC M³ 23" o "VOLUMEN FAC: 23" o "VOLUMEN FACTURADO 23"
        val regex = Regex("""VOLUMEN\s*FAC(?:TURADO)?\s*(?:M³)?\s*[:.]?\s*(\d+)""")
        val match = regex.find(texto)
        if (match != null) {
            val valor = match.groupValues[1].toIntOrNull()
            if (valor != null) return Campo(valor = valor, confianza = 0.95f)
        }

        // Fallback secundario: "CONSUMO FACTURADO: 23" o "PROMEDIO M³ 23"
        val fallbackRegex = Regex("""(?:CONSUMO\s*FACTURADO|PROMEDIO\s*M³)\s*[:.]?\s*(\d+)""")
        val matchFallback = fallbackRegex.find(texto)
        if (matchFallback != null) {
            val valor = matchFallback.groupValues[1].toIntOrNull()
            if (valor != null) return Campo(valor = valor, confianza = 0.75f)
        }

        return Campo(valor = null, confianza = 0f)
    }

    private fun extraerImporteTotal(texto: String): Campo<Dinero> {
        // Ejemplo: "TOTAL A PAGAR: S/ 78.00" o "TOTAL A PAGAR S/ 78.00" o "TOTAL MES S/ 78.00"
        val regex = Regex("""TOTAL\s*(?:A\s*PAGAR|MES)?\s*[:.]?\s*(?:S/|S/\.)?\s*(\d+[.,]\d{2})""")
        val match = regex.find(texto)
        if (match != null) {
            val dinero = Dinero.parsear(match.groupValues[1])
            if (dinero != null) return Campo(valor = dinero, confianza = 0.95f)
        }

        // Fallback: buscar "S/ XX.XX" cerca del final
        val fallbackRegex = Regex("""(?:S/|S/\.)\s*(\d+[.,]\d{2})""")
        val matches = fallbackRegex.findAll(texto).toList()
        if (matches.isNotEmpty()) {
            val ultimo = matches.last()
            val dinero = Dinero.parsear(ultimo.groupValues[1])
            if (dinero != null) return Campo(valor = dinero, confianza = 0.65f)
        }

        return Campo(valor = null, confianza = 0f)
    }

    private fun extraerTipoConsumo(texto: String): Campo<TipoConsumo> {
        // Ejemplo: "TIPO CONSUMO: PROMEDIO"
        val regex = Regex("""TIPO\s*CONSUMO\s*[:.]?\s*([A-Z]+)""")
        val match = regex.find(texto)
        if (match != null) {
            val tipo = TipoConsumo.parsear(match.groupValues[1])
            return Campo(valor = tipo, confianza = 0.90f)
        }

        return Campo(valor = TipoConsumo.DESCONOCIDO, confianza = 0.40f)
    }

    private fun extraerFechaEmision(texto: String): Campo<LocalDate> {
        // Ejemplo: "FECHA DE EMISION 31/08/2026"
        val regex = Regex("""(?:FECHA\s*DE\s*)?EMISION\s*[:.]?\s*(\d{2}[/-]\d{2}[/-]\d{4})""")
        val match = regex.find(texto)
        if (match != null) {
            val fecha = parsearFecha(match.groupValues[1])
            if (fecha != null) return Campo(valor = fecha, confianza = 0.95f)
        }
        return Campo(valor = null, confianza = 0f)
    }

    private fun extraerFechaVencimiento(texto: String): Campo<LocalDate> {
        // Ejemplo: "FECHA DE VENCIMIENTO 11/09/2026"
        val regex = Regex("""(?:FECHA\s*DE\s*)?VENCIMIENTO\s*[:.]?\s*(\d{2}[/-]\d{2}[/-]\d{4})""")
        val match = regex.find(texto)
        if (match != null) {
            val fecha = parsearFecha(match.groupValues[1])
            if (fecha != null) return Campo(valor = fecha, confianza = 0.95f)
        }
        return Campo(valor = null, confianza = 0f)
    }

    private fun extraerLecturaAnterior(texto: String): Campo<Int> {
        val regex = Regex("""LECTURA\s*ANTERIOR\s*[:.]?\s*(\d+)""")
        val match = regex.find(texto)
        if (match != null) {
            val valor = match.groupValues[1].toIntOrNull()
            if (valor != null) return Campo(valor = valor, confianza = 0.90f)
        }
        return Campo(valor = null, confianza = 1f) // Opcional (null es normal si es PROMEDIO)
    }

    private fun extraerLecturaActual(texto: String): Campo<Int> {
        val regex = Regex("""LECTURA\s*ACTUAL\s*[:.]?\s*(\d+)""")
        val match = regex.find(texto)
        if (match != null) {
            val valor = match.groupValues[1].toIntOrNull()
            if (valor != null) return Campo(valor = valor, confianza = 0.90f)
        }
        return Campo(valor = null, confianza = 1f) // Opcional
    }

    private fun extraerNumeroMedidor(texto: String): Campo<String> {
        // Ejemplo: "MEDIDOR: EA18490784" o "NUMERO DE MEDIDOR EA18490784"
        val regex = Regex("""MEDIDOR\s*(?:>|:|\b)?\s*(?:NUMERO)?\s*[:.]?\s*([A-Z0-9]{7,12})""")
        val match = regex.find(texto)
        if (match != null) {
            return Campo(valor = match.groupValues[1], confianza = 0.90f)
        }
        return Campo(valor = null, confianza = 0f)
    }

    private fun extraerNumeroRecibo(texto: String): Campo<String> {
        // Ejemplo: "Nº REC S001-7264721" o "RECIBO: S001-7264721"
        val regex = Regex("""(?:N[º°]|NUMERO)?\s*REC(?:IBO)?\s*[:.]?\s*([A-Z0-9]+-[A-Z0-9]+)""")
        val match = regex.find(texto)
        if (match != null) {
            return Campo(valor = match.groupValues[1], confianza = 0.95f)
        }
        return Campo(valor = null, confianza = 0f)
    }

    private fun parsearFecha(texto: String): LocalDate? {
        val partes = texto.split('/', '-')
        if (partes.size != 3) return null
        val dia = partes[0].toIntOrNull() ?: return null
        val mes = partes[1].toIntOrNull() ?: return null
        val anio = partes[2].toIntOrNull() ?: return null

        return try {
            LocalDate(anio, mes, dia)
        } catch (_: Exception) {
            null
        }
    }
}
