package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.core.util.RelojDelSistema
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave
import pe.edu.upt.aguatacna.feature.recibo.data.BorradorReciboStore
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.CorregirCampoUseCase

// Colores específicos del diseño manual medidor
private val FondoPantalla = Color(0xFFF1F6F8)
private val DigitoFondo = Color(0xFFF0F6F8)
private val DigitoTexto = Color(0xFF14232C)
private val CursorColor = Color(0xFF0992A5)
private val CursorBorde = Color(0xFF0992A5)
private val TeclaFondo = Blanco
private val BotonGuardar = Color(0xFF098093)

enum class TipoCampoEdicion(val nombre: String, val unidad: String) {
    CONSUMO_M3("consumo", "m³"),
    LECTURA_ANTERIOR("lectura anterior", "m³"),
    LECTURA_ACTUAL("lectura actual", "m³"),
    IMPORTE("importe total", "S/"),
    PERIODO("período de consumo", "")
}

/**
 * Pantalla de corrección manual de campos del recibo (Fase 9).
 * Permite corregir consumo, lecturas, importe y período (mes/año).
 */
@Composable
fun ReciboManualMedidorScreen(
    onVolver: () -> Unit,
    campo: TipoCampoEdicion = TipoCampoEdicion.CONSUMO_M3,
    borradorStore: BorradorReciboStore = KoinPlatform.getKoin().get(),
    corregirUseCase: CorregirCampoUseCase = CorregirCampoUseCase()
) {
    val ahora = remember {
        RelojDelSistema().ahora()
    }
    val periodoActual = remember(ahora) {
        PeriodoConsumo(ahora.year, ahora.monthNumber)
    }
    var borrador = borradorStore.borrador.value
    if (borrador == null) {
        val nuevo = pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador(
            periodoConsumo = pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo(
                periodoActual,
                confianza = 1f
            ),
            consumoM3 = pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo(null, 1f),
            importeTotal = pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo(null, 1f),
            origen = pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos.MANUAL
        )
        borradorStore.guardar(nuevo)
        borrador = nuevo
    }

    val valorDetectado = remember(campo, borrador) {
        when (campo) {
            TipoCampoEdicion.CONSUMO_M3 -> borrador.consumoM3.valor?.let { if (it == 0) "" else it.toString() } ?: ""
            TipoCampoEdicion.LECTURA_ANTERIOR -> borrador.lecturaAnteriorM3.valor?.let { if (it == 0) "" else it.toString() } ?: ""
            TipoCampoEdicion.LECTURA_ACTUAL -> borrador.lecturaActualM3.valor?.let { if (it == 0) "" else it.toString() } ?: ""
            TipoCampoEdicion.IMPORTE -> borrador.importeTotal.valor?.let {
                val soles = it.centimos / 100
                val cent = it.centimos % 100
                if (it.centimos == 0L) "" else "$soles,${cent.toString().padStart(2, '0')}"
            } ?: ""
            TipoCampoEdicion.PERIODO -> borrador.periodoConsumo.valor?.displayCompleto ?: ""
        }
    }

    var entrada by remember(valorDetectado) {
        mutableStateOf(if (valorDetectado == "0" || valorDetectado == "0,00") "" else valorDetectado)
    }
    var periodoSeleccionado by remember(borrador) {
        mutableStateOf(borrador.periodoConsumo.valor ?: periodoActual)
    }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    val permiteComa = campo == TipoCampoEdicion.IMPORTE
    val maxDigitos = when (campo) {
        TipoCampoEdicion.CONSUMO_M3 -> 3 // 0..999 m³
        TipoCampoEdicion.IMPORTE -> 7 // Ej: 1234,56
        else -> 6 // Lecturas de medidor hasta 999999
    }

    IconosClarosEnBarraDeEstado(claros = false)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPantalla)
            .statusBarsPadding()
    ) {
        // ── Parte superior: Header + Tarjeta de entrada ──
        Column(modifier = Modifier.weight(1f)) {
            EncabezadoCorreccion(
                onVolver = onVolver,
                titulo = if (campo == TipoCampoEdicion.PERIODO) "Seleccionar período" else "Corregir ${campo.nombre}",
                subtitulo = if (campo == TipoCampoEdicion.PERIODO) {
                    "Recibo para: ${periodoSeleccionado.displayCompleto}"
                } else {
                    "Valor actual: ${if (valorDetectado.isBlank()) "Sin datos" else "$valorDetectado ${campo.unidad}"}"
                }
            )

            if (campo == TipoCampoEdicion.PERIODO) {
                SelectorPeriodo(
                    periodoSeleccionado = periodoSeleccionado,
                    onSeleccionarPeriodo = { periodoSeleccionado = it }
                )
            } else {
                TarjetaDigitos(
                    campoEtiqueta = "${campo.nombre} en ${campo.unidad}".uppercase(),
                    digitos = entrada,
                    unidad = campo.unidad,
                    valorDetectado = if (valorDetectado.isBlank()) "0" else valorDetectado,
                    errorMensaje = errorMensaje,
                    maxDigitos = maxDigitos
                )
            }
        }

        // ── Parte inferior: Teclado numérico (o espacio) + Botón guardar ──
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            if (campo != TipoCampoEdicion.PERIODO) {
                TecladoNumerico(
                    permiteComa = permiteComa,
                    onDigitoPulsado = { tecla ->
                        errorMensaje = null
                        if (tecla == "←") {
                            if (entrada.isNotEmpty()) {
                                entrada = entrada.dropLast(1)
                            }
                        } else if (tecla == ",") {
                            if (permiteComa && !entrada.contains(',')) {
                                entrada = if (entrada.isEmpty()) "0," else entrada + ","
                            }
                        } else {
                            // Si ya tiene coma en importe, permitir máx 2 decimales
                            if (permiteComa && entrada.contains(',')) {
                                val partes = entrada.split(',')
                                if (partes.size > 1 && partes[1].length >= 2) {
                                    return@TecladoNumerico
                                }
                            }
                            if (entrada == "0" && tecla != ",") {
                                entrada = tecla
                            } else if (entrada.length < maxDigitos) {
                                entrada += tecla
                            }
                        }
                    }
                )
                Spacer(Modifier.height(20.dp))
            }

            BotonGuardar(
                onGuardar = {
                    when (campo) {
                        TipoCampoEdicion.PERIODO -> {
                            borradorStore.actualizar { borradorActual ->
                                corregirUseCase(borradorActual, CorregirCampoUseCase.CampoEditable.Periodo(periodoSeleccionado))
                            }
                            onVolver()
                        }
                        TipoCampoEdicion.CONSUMO_M3 -> {
                            val numero = entrada.toIntOrNull()
                            if (numero == null || numero !in 0..999) {
                                errorMensaje = "Ingresa un consumo válido entre 0 y 999 m³"
                            } else {
                                borradorStore.actualizar { borradorActual ->
                                    corregirUseCase(borradorActual, CorregirCampoUseCase.CampoEditable.ConsumoM3(numero))
                                }
                                onVolver()
                            }
                        }
                        TipoCampoEdicion.LECTURA_ANTERIOR -> {
                            val numero = entrada.toIntOrNull()
                            if (numero == null || numero < 0) {
                                errorMensaje = "Ingresa una lectura válida"
                            } else {
                                val actual = borrador.lecturaActualM3.valor
                                if (actual != null && numero > actual) {
                                    errorMensaje = "La lectura anterior no puede superar la actual ($actual m³)"
                                } else {
                                    borradorStore.actualizar { borradorActual ->
                                        corregirUseCase(borradorActual, CorregirCampoUseCase.CampoEditable.LecturaAnterior(numero))
                                    }
                                    onVolver()
                                }
                            }
                        }
                        TipoCampoEdicion.LECTURA_ACTUAL -> {
                            val numero = entrada.toIntOrNull()
                            if (numero == null || numero < 0) {
                                errorMensaje = "Ingresa una lectura válida"
                            } else {
                                val anterior = borrador.lecturaAnteriorM3.valor
                                if (anterior != null && numero < anterior) {
                                    errorMensaje = "La lectura actual debe ser mayor o igual a la anterior ($anterior m³)"
                                } else {
                                    borradorStore.actualizar { borradorActual ->
                                        corregirUseCase(borradorActual, CorregirCampoUseCase.CampoEditable.LecturaActual(numero))
                                    }
                                    onVolver()
                                }
                            }
                        }
                        TipoCampoEdicion.IMPORTE -> {
                            val normalizado = entrada.replace(',', '.')
                            val importeDouble = normalizado.toDoubleOrNull()
                            if (importeDouble == null || importeDouble <= 0 || importeDouble > 99999.0) {
                                errorMensaje = "Ingresa un importe válido mayor a S/ 0"
                            } else {
                                val centimos = (importeDouble * 100).toLong()
                                borradorStore.actualizar { borradorActual ->
                                    corregirUseCase(borradorActual, CorregirCampoUseCase.CampoEditable.Importe(centimos))
                                }
                                onVolver()
                            }
                        }
                    }
                }
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Header
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun EncabezadoCorreccion(
    onVolver: () -> Unit,
    titulo: String,
    subtitulo: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(
                onClick = onVolver,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Blanco.copy(alpha = 0.8f))
                    .border(1.dp, Divisor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier.size(20.dp),
                    tint = Tinta
                )
            }
            Column {
                Text(
                    titulo,
                    fontFamily = FuenteTexto,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DigitoTexto
                )
                Text(
                    subtitulo,
                    fontFamily = FuenteTexto,
                    fontSize = 12.sp,
                    color = TintaTenue
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Tarjeta de lectura con dígitos interactivos (T-9.3)
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun TarjetaDigitos(
    campoEtiqueta: String,
    digitos: String,
    unidad: String,
    valorDetectado: String,
    errorMensaje: String?,
    maxDigitos: Int = 4
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp)
            .sombraSuave(24.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Blanco)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Etiqueta
        Text(
            campoEtiqueta,
            fontFamily = FuenteTexto,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TintaTenue,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))

        // Dígitos responsivos (T-9.3)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Render de cada carácter escrito
            for (char in digitos) {
                if (char == ',') {
                    Box(
                        modifier = Modifier
                            .size(width = 20.dp, height = 56.dp)
                            .padding(bottom = 8.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            ",",
                            fontFamily = FuenteNumeros,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = DigitoTexto
                        )
                    }
                } else {
                    CajaDigito(digito = char.toString(), activa = false)
                }
            }
            // Slot activo con cursor
            if (digitos.length < maxDigitos) {
                CajaDigito(digito = null, activa = true)
            }
        }

        Spacer(Modifier.height(20.dp))

        if (errorMensaje != null) {
            Text(
                errorMensaje,
                fontFamily = FuenteTexto,
                fontSize = 12.sp,
                color = Ocre,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
        }

        // Valor detectado original (T-9.2)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Valor detectado por OCR",
                fontFamily = FuenteTexto,
                fontSize = 12.sp,
                color = TintaSuave
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(valorDetectado, fontFamily = FuenteNumeros, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DigitoTexto)
                Spacer(Modifier.width(3.dp))
                Text(unidad, fontFamily = FuenteTexto, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
            }
        }
    }
}

@Composable
private fun SelectorPeriodo(
    periodoSeleccionado: PeriodoConsumo,
    onSeleccionarPeriodo: (PeriodoConsumo) -> Unit
) {
    val ahora = remember {
        RelojDelSistema().ahora()
    }
    val ultimos12Meses = remember(ahora) {
        var p = PeriodoConsumo(ahora.year, ahora.monthNumber)
        buildList {
            repeat(12) {
                add(p)
                p = p.anterior()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp)
            .sombraSuave(24.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Blanco)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "SELECCIONA EL PERÍODO (ÚLTIMOS 12 MESES)",
            fontFamily = FuenteTexto,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TintaTenue,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        // Grid de 12 meses: 4 filas x 3 columnas
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (fila in 0 until 4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (col in 0 until 3) {
                        val index = fila * 3 + col
                        val periodo = ultimos12Meses[index]
                        val esSeleccionado = periodo == periodoSeleccionado

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (esSeleccionado) BotonGuardar else DigitoFondo)
                                .clickable { onSeleccionarPeriodo(periodo) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = periodo.mesCorto,
                                    fontFamily = FuenteTexto,
                                    fontSize = 13.sp,
                                    fontWeight = if (esSeleccionado) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (esSeleccionado) Blanco else DigitoTexto
                                )
                                Text(
                                    text = periodo.anio.toString(),
                                    fontFamily = FuenteNumeros,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = if (esSeleccionado) Blanco.copy(alpha = 0.85f) else TintaTenue
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CajaDigito(digito: String?, activa: Boolean) {
    Box(
        modifier = Modifier
            .size(width = 52.dp, height = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (activa) Blanco else DigitoFondo)
            .then(
                if (activa) Modifier.border(2.dp, CursorBorde, RoundedCornerShape(16.dp))
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (digito != null) {
            Text(
                digito,
                fontFamily = FuenteNumeros,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DigitoTexto
            )
        } else {
            // Cursor
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(50))
                    .background(CursorColor)
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Teclado numérico interactivo
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun TecladoNumerico(
    permiteComa: Boolean = false,
    onDigitoPulsado: (String) -> Unit
) {
    val teclas = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(",", "0", "←")
    )

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        teclas.forEach { fila ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                fila.forEach { tecla ->
                    val esComa = tecla == ","
                    val habilitada = !esComa || permiteComa

                    OutlinedButton(
                        onClick = { if (habilitada) onDigitoPulsado(tecla) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = habilitada,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (habilitada) TeclaFondo else TeclaFondo.copy(alpha = 0.4f),
                            disabledContainerColor = TeclaFondo.copy(alpha = 0.3f)
                        ),
                        border = null
                    ) {
                        if (tecla == "←") {
                            Icon(
                                Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Borrar",
                                tint = DigitoTexto,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                tecla,
                                fontFamily = FuenteNumeros,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (habilitada) DigitoTexto else DigitoTexto.copy(alpha = 0.25f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Botón guardar
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun BotonGuardar(onGuardar: () -> Unit) {
    Button(
        onClick = onGuardar,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BotonGuardar)
    ) {
        Text(
            "Guardar corrección",
            fontFamily = FuenteTexto,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}
