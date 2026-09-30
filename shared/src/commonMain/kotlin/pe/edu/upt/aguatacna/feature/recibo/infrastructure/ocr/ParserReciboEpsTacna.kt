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

    private val PERIODO = Regex("""CONSUMO\s*[:.]?\s*([A-Z]+[- /]\d{4})""")
    private val PERIODO_SUELTO = Regex("""\b([A-Z]{4,10}[- /]\d{4})\b""")
    // "(?![A-Z])" evita leer "MEDIDOR" o "MES" como la unidad m³; el "³" a veces sale como ², ª, ° o '.
    private val VOLUMEN_FACTURADO = Regex("""VOLUMEN\s*FAC[A-Z.]*\s*(?:M(?![A-Z]))?[^\d]{0,4}?(\d{1,4})""")
    private val PROMEDIO_M3 = Regex("""PROMEDIO\s*M(?![A-Z])[^\d]{0,4}?(\d{1,4})""")
    private val CONSUMO_FACTURADO = Regex("""CONSUMO\s*FACTURADO\s*(?:M(?![A-Z]))?[^\d]{0,4}?(\d{1,4})""")
    private val TOTAL = Regex("""TOTAL\s*(?:A\s*PAGAR|MES)?\s*[:.]?\s*(?:S/|S/\.)?\s*(\d+[.,]\d{2})""")
    private val SOLES = Regex("""(?:S/|S/\.)\s*(\d+[.,]\d{2})""")
    private val TIPO_CONSUMO = Regex("""TIPO\s*CONSUMO\s*[:.]?\s*([A-Z]+)""")
    private val EMISION = Regex("""(?:FECHA\s*DE\s*)?EMISION\s*[:.]?\s*(\d{2}[/-]\d{2}[/-]\d{4})""")
    private val VENCIMIENTO = Regex("""(?:FECHA\s*DE\s*)?VENCIMIENTO\s*[:.]?\s*(\d{2}[/-]\d{2}[/-]\d{4})""")
    private val LECTURA_ANTERIOR = Regex("""LECTURA\s*ANTERIOR\s*[:.]?\s*(\d+)""")
    private val LECTURA_ACTUAL = Regex("""LECTURA\s*ACTUAL\s*[:.]?\s*(\d+)""")
    private val MEDIDOR = Regex("""MEDIDOR\s*(?:>|:|\b)?\s*(?:NUMERO)?\s*[:.]?\s*([A-Z0-9]{7,12})""")
    private val NUMERO_RECIBO = Regex("""(?:N[º°]|NUMERO)?\s*REC(?:IBO)?\s*[:.]?\s*([A-Z0-9]+-[A-Z0-9]+)""")

    // Se lee dos veces: el texto plano de ML Kit y el texto rearmado por filas. Por cada campo manda el
    // texto plano (lo que ya funcionaba), salvo que las filas den un valor que falta o más confiable.
    override fun parsear(texto: TextoReconocido): ResultadoParseo {
        if (texto.estaVacio) return ResultadoParseo.NoLegible("El texto del recibo está vacío.")
        val plano = extraer(texto.textoPlano)
        val borrador = if (texto.lineas.isEmpty()) plano else combinar(plano, extraer(reconstruirFilas(texto.lineas)))
        if (borrador.consumoM3.valor == null && borrador.importeTotal.valor == null) {
            return ResultadoParseo.NoLegible("No pudimos leer el consumo ni el importe total de tu recibo.")
        }
        return ResultadoParseo.Exito(borrador)
    }

    override fun parsearTexto(textoOriginal: String): ResultadoParseo = parsear(TextoReconocido(textoOriginal))

    // Limpieza y normalización de texto antes de extraer campos.
    fun normalizar(texto: String): String {
        return texto.uppercase()
            .replace('Á', 'A')
            .replace('É', 'E')
            .replace('Í', 'I')
            .replace('Ó', 'O')
            .replace('Ú', 'U')
            .replace("M 3", "M³")
            .replace("M3", "M³")
            .replace("*", "")
    }

    private fun extraer(textoOriginal: String): ReciboBorrador {
        val t = normalizar(textoOriginal)
        return ReciboBorrador(
            periodoConsumo = buscar(PERIODO, t, 0.95f, PeriodoConsumo::parsear)
                ?: PERIODO_SUELTO.findAll(t).firstNotNullOfOrNull { campo(it.groupValues[1], 0.70f, PeriodoConsumo::parsear) }
                ?: Campo(),
            consumoM3 = buscarEntero(VOLUMEN_FACTURADO, t, 0.95f)
                ?: buscarEntero(PROMEDIO_M3, t, 0.90f)
                ?: buscarEntero(CONSUMO_FACTURADO, t, 0.75f)
                ?: Campo(),
            importeTotal = buscar(TOTAL, t, 0.95f, Dinero::parsear)
                ?: SOLES.findAll(t).lastOrNull()?.let { campo(it.groupValues[1], 0.65f, Dinero::parsear) }
                ?: Campo(),
            tipoConsumo = buscar(TIPO_CONSUMO, t, 0.90f, TipoConsumo::parsear)
                ?: Campo(TipoConsumo.DESCONOCIDO, confianza = 0.40f),
            fechaEmision = buscarFecha(EMISION, t) ?: Campo(),
            fechaVencimiento = buscarFecha(VENCIMIENTO, t) ?: Campo(),
            // Sin lecturas es lo normal en un recibo por PROMEDIO: no se marcan como dudosas.
            lecturaAnteriorM3 = buscarEntero(LECTURA_ANTERIOR, t, 0.90f) ?: Campo(confianza = 1f),
            lecturaActualM3 = buscarEntero(LECTURA_ACTUAL, t, 0.90f) ?: Campo(confianza = 1f),
            numeroMedidor = buscarTexto(MEDIDOR, t, 0.90f) ?: Campo(),
            numeroRecibo = buscarTexto(NUMERO_RECIBO, t, 0.95f) ?: Campo(),
            origen = OrigenDatos.ESCANEADO
        )
    }

    private fun combinar(plano: ReciboBorrador, filas: ReciboBorrador) = ReciboBorrador(
        periodoConsumo = elegir(plano.periodoConsumo, filas.periodoConsumo),
        consumoM3 = elegir(plano.consumoM3, filas.consumoM3),
        importeTotal = elegir(plano.importeTotal, filas.importeTotal),
        fechaEmision = elegir(plano.fechaEmision, filas.fechaEmision),
        fechaVencimiento = elegir(plano.fechaVencimiento, filas.fechaVencimiento),
        tipoConsumo = elegir(plano.tipoConsumo, filas.tipoConsumo),
        lecturaAnteriorM3 = elegir(plano.lecturaAnteriorM3, filas.lecturaAnteriorM3),
        lecturaActualM3 = elegir(plano.lecturaActualM3, filas.lecturaActualM3),
        numeroMedidor = elegir(plano.numeroMedidor, filas.numeroMedidor),
        numeroRecibo = elegir(plano.numeroRecibo, filas.numeroRecibo),
        origen = OrigenDatos.ESCANEADO
    )

    private fun <T> elegir(plano: Campo<T>, filas: Campo<T>): Campo<T> =
        if (filas.valor != null && (plano.valor == null || filas.confianza > plano.confianza)) filas else plano

    private fun <T> campo(texto: String, confianza: Float, convertir: (String) -> T?): Campo<T>? =
        convertir(texto)?.let { Campo(it, confianza) }

    private fun <T> buscar(regex: Regex, texto: String, confianza: Float, convertir: (String) -> T?): Campo<T>? =
        regex.find(texto)?.let { campo(it.groupValues[1], confianza, convertir) }

    private fun buscarEntero(regex: Regex, texto: String, confianza: Float): Campo<Int>? =
        buscar(regex, texto, confianza, String::toIntOrNull)

    private fun buscarTexto(regex: Regex, texto: String, confianza: Float): Campo<String>? =
        buscar(regex, texto, confianza) { it }

    private fun buscarFecha(regex: Regex, texto: String): Campo<LocalDate>? =
        buscar(regex, texto, 0.95f, ::parsearFecha)

    private fun parsearFecha(texto: String): LocalDate? {
        val (dia, mes, anio) = texto.split('/', '-').mapNotNull { it.toIntOrNull() }.takeIf { it.size == 3 } ?: return null
        return try {
            LocalDate(anio, mes, dia)
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}
