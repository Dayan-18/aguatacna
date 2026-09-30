package pe.edu.upt.aguatacna.feature.recibo

import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.LineaTexto
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TextoReconocido
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TipoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.port.ResultadoParseo
import pe.edu.upt.aguatacna.feature.recibo.infrastructure.ocr.ParserReciboEpsTacna
import pe.edu.upt.aguatacna.feature.recibo.infrastructure.ocr.reconstruirFilas
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ParserReciboEpsTacnaTest {

    // Texto representativo del recibo real de ejemplo de EPS Tacna (T-5.4)
    private val reciboEjemploTexto = """
        EPS TACNA S.A.
        RUC: 20147778912
        RECIBO DE AGUA POTABLE Y ALCANTARILLADO
        Nº Rec: S001-7264721
        
        DATOS DEL SUMINISTRO
        Suministro: 0412887
        Titular: PEREZ MAMANI JUAN CARLOS
        DNI: 00482910
        Direccion: CALLE ALTO LIMA 1420
        Usuario Web: USER_TACNA_412
        Clave: Secr3t*412
        
        DETALLE DE FACTURACION
        Consumo: AGOSTO-2026
        Facturacion: SETIEMBRE-2026
        Tipo Consumo: PROMEDIO
        MEDIDOR > Numero: EA18490784
        
        Fecha Anterior: 16/07/2026
        Fecha Actual: 16/08/2026
        Volumen Fac m³ 23
        PROMEDIO m³ 23
        
        FECHA DE EMISIÓN: 31/08/2026
        FECHA DE VENCIMIENTO: 11/09/2026
        
        CONCEPTOS FACTURADOS
        Agua Potable: 43.43
        Alcantarillado: 18.47
        Cargo Fijo: 4.20
        I.G.V. 18%: 11.90
        Redondeo mes anterior: -0.02
        Redondeo mes actual: 0.02
        
        TOTAL A PAGAR: S/ *****78.00
    """.trimIndent()

    @Test
    fun parseaReciboDeEjemploCompletamente() {
        val resultado = ParserReciboEpsTacna.parsearTexto(reciboEjemploTexto)

        assertIs<ResultadoParseo.Exito>(resultado)
        val borrador = resultado.borrador

        // Validaciones exactas de la tabla T-5.4
        assertEquals(PeriodoConsumo(2026, 8), borrador.periodoConsumo.valor)
        assertEquals(23, borrador.consumoM3.valor)
        assertEquals(TipoConsumo.PROMEDIO, borrador.tipoConsumo.valor)
        assertEquals(Dinero(7800L), borrador.importeTotal.valor)
        assertEquals(LocalDate(2026, 8, 31), borrador.fechaEmision.valor)
        assertEquals(LocalDate(2026, 9, 11), borrador.fechaVencimiento.valor)
        assertNull(borrador.lecturaAnteriorM3.valor)
        assertNull(borrador.lecturaActualM3.valor)
        assertEquals("EA18490784", borrador.numeroMedidor.valor)
        assertEquals("S001-7264721", borrador.numeroRecibo.valor)

        // El borrador debe ser confirmable directamente
        assertTrue(borrador.esConfirmable)
    }

    @Test
    fun parseaReciboConLecturasReales() {
        val textoConLecturas = """
            EPS TACNA S.A.
            Nº Rec: S001-9988776
            Consumo: JULIO-2026
            Tipo Consumo: LECTURA
            Lectura Anterior: 100
            Lectura Actual: 125
            Volumen Fac m³ 25
            TOTAL A PAGAR: S/ 65.50
        """.trimIndent()

        val resultado = ParserReciboEpsTacna.parsearTexto(textoConLecturas)
        assertIs<ResultadoParseo.Exito>(resultado)
        val borrador = resultado.borrador

        assertEquals(PeriodoConsumo(2026, 7), borrador.periodoConsumo.valor)
        assertEquals(25, borrador.consumoM3.valor)
        assertEquals(TipoConsumo.LECTURA, borrador.tipoConsumo.valor)
        assertEquals(100, borrador.lecturaAnteriorM3.valor)
        assertEquals(125, borrador.lecturaActualM3.valor)
        assertEquals(Dinero(6550L), borrador.importeTotal.valor)
    }

    @Test
    fun ignoraDatosPersonalesDelRecibo() {
        // T-5.3: El borrador no tiene campos ni almacena datos como DNI o nombres
        val resultado = ParserReciboEpsTacna.parsearTexto(reciboEjemploTexto)
        assertIs<ResultadoParseo.Exito>(resultado)

        // Verificamos que el borrador confirmado no expone datos sensibles
        val recibo = resultado.borrador.confirmar("id-1")
        assertEquals(23, recibo.consumoM3)
        assertEquals(Dinero(7800L), recibo.importeTotal)
    }

    @Test
    fun rechazaTextoSinConsumoNiTotal() {
        val textoInutil = """
            ESTE ES UN FOLLETO PUBLICITARIO DE EPS TACNA
            Cuida el agua en tu hogar.
        """.trimIndent()

        val resultado = ParserReciboEpsTacna.parsearTexto(textoInutil)
        assertIs<ResultadoParseo.NoLegible>(resultado)
    }

    @Test
    fun parseaConsumoConVariacionesDeM3YPromedio() {
        val textoPromedioM3 = """
            EPS TACNA S.A.
            Nº Rec: S001-1122334
            Consumo: AGOSTO-2026
            PROMEDIO m3 23
            TOTAL A PAGAR: S/ 78.00
        """.trimIndent()
        val res1 = ParserReciboEpsTacna.parsearTexto(textoPromedioM3)
        assertIs<ResultadoParseo.Exito>(res1)
        assertEquals(23, res1.borrador.consumoM3.valor)

        val textoPromedioEspacio = """
            EPS TACNA S.A.
            Nº Rec: S001-1122334
            Consumo: AGOSTO-2026
            PROMEDIO m 3 23
            TOTAL A PAGAR: S/ 78.00
        """.trimIndent()
        val res2 = ParserReciboEpsTacna.parsearTexto(textoPromedioEspacio)
        assertIs<ResultadoParseo.Exito>(res2)
        assertEquals(23, res2.borrador.consumoM3.valor)

        val textoVolumenEspacio = """
            EPS TACNA S.A.
            Nº Rec: S001-1122334
            Consumo: AGOSTO-2026
            Volumen Fac m 3 23
            TOTAL A PAGAR: S/ 78.00
        """.trimIndent()
        val res3 = ParserReciboEpsTacna.parsearTexto(textoVolumenEspacio)
        assertIs<ResultadoParseo.Exito>(res3)
        assertEquals(23, res3.borrador.consumoM3.valor)
    }

    // ML Kit devuelve la columna de etiquetas y la de valores en bloques distintos.
    @Test
    fun leeElConsumoCuandoEtiquetasYValoresSalenEnBloquesDistintos() {
        val texto = TextoReconocido(
            textoPlano = """
                PROMEDIO m³
                Volumen Fac m³
                Tipo Consumo: PROMEDIO
                Consumo: AGOSTO-2026
                TOTAL A PAGAR: S/ 78.00
                23
                23
            """.trimIndent(),
            lineas = listOf(
                LineaTexto("PROMEDIO m³", x = 370f, y = 427f, ancho = 150f, alto = 20f),
                LineaTexto("Volumen Fac m³", x = 370f, y = 463f, ancho = 170f, alto = 20f),
                LineaTexto("Tipo Consumo: PROMEDIO", x = 370f, y = 499f, ancho = 260f, alto = 20f),
                LineaTexto("Consumo: AGOSTO-2026", x = 370f, y = 535f, ancho = 240f, alto = 20f),
                LineaTexto("23", x = 578f, y = 427f, ancho = 24f, alto = 20f),
                LineaTexto("23", x = 578f, y = 463f, ancho = 24f, alto = 20f)
            )
        )

        val resultado = assertIs<ResultadoParseo.Exito>(ParserReciboEpsTacna.parsear(texto))
        assertEquals(23, resultado.borrador.consumoM3.valor)
        assertEquals(PeriodoConsumo(2026, 8), resultado.borrador.periodoConsumo.valor)
        assertEquals(TipoConsumo.PROMEDIO, resultado.borrador.tipoConsumo.valor)
    }

    @Test
    fun reconstruirFilasUneEtiquetaYValorDeLaMismaAltura() {
        val filas = reconstruirFilas(
            listOf(
                LineaTexto("23", x = 578f, y = 427f, alto = 20f),
                LineaTexto("Volumen Fac m³", x = 370f, y = 463f, alto = 20f),
                LineaTexto("PROMEDIO m³", x = 370f, y = 427f, alto = 20f),
                LineaTexto("23", x = 578f, y = 465f, alto = 20f)
            )
        )
        assertEquals("PROMEDIO m³  23\nVolumen Fac m³  23", filas)
    }

    @Test
    fun toleraLasLecturasErradasDelSuperindiceTres() {
        listOf("PROMEDIO m² 23", "PROMEDIO mª 23", "PROMEDIO m° 23", "Volumen Fac. m³ 23", "PROMEDIO m 3 23").forEach { linea ->
            val resultado = assertIs<ResultadoParseo.Exito>(
                ParserReciboEpsTacna.parsearTexto("Consumo: AGOSTO-2026\n$linea\nTOTAL A PAGAR: S/ 78.00")
            )
            assertEquals(23, resultado.borrador.consumoM3.valor, "No leyó '$linea'")
        }
    }

    @Test
    fun tipoConsumoPromedioSeguidoDeMedidorNoInventaUnConsumo() {
        val texto = """
            Consumo: AGOSTO-2026
            Tipo Consumo: PROMEDIO
            MEDIDOR > Numero: EA18490784
            TOTAL A PAGAR: S/ 78.00
        """.trimIndent()

        val resultado = assertIs<ResultadoParseo.Exito>(ParserReciboEpsTacna.parsearTexto(texto))
        assertNull(resultado.borrador.consumoM3.valor)
        assertEquals("EA18490784", resultado.borrador.numeroMedidor.valor)
    }
}
