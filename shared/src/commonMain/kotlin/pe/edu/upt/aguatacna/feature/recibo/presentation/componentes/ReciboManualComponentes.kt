// Componentes visuales usados únicamente en la pantalla de ingreso manual (ReciboManualMedidorScreen):
// dígitos estilo medidor, teclado numérico y selector de período.
package pe.edu.upt.aguatacna.feature.recibo.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave
import pe.edu.upt.aguatacna.core.util.RelojDelSistema
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo

// ── Colores privados ──────────────────────────────────────────────────────────

private val DigitoFondo = Color(0xFFF0F6F8)
private val DigitoTexto = Color(0xFF14232C)
private val CursorColor = Color(0xFF0992A5)
private val CursorBorde = Color(0xFF0992A5)
private val BotonGuardar = Color(0xFF098093)
private val TeclaFondo = Blanco

// ── TipoCampoEdicion ─────────────────────────────────────────────────────────

// Tipos de campos del recibo que el usuario puede editar o corregir manualmente.
enum class TipoCampoEdicion(val nombre: String, val unidad: String) {
    CONSUMO_M3("consumo", "m³"),
    LECTURA_ANTERIOR("lectura anterior", "m³"),
    LECTURA_ACTUAL("lectura actual", "m³"),
    IMPORTE("importe total", "S/"),
    PERIODO("período de consumo", "")
}

// ── DisplayDigitosMedidor / CajaDigito ───────────────────────────────────────

// Casillas de dígitos estilo medidor, con cursor de posición activa, para la digitación manual.
@Composable
fun DisplayDigitosMedidor(
    campoEtiqueta: String,
    digitos: String,
    unidad: String,
    valorDetectado: String,
    errorMensaje: String?,
    modifier: Modifier = Modifier,
    maxDigitos: Int = 4
) {
    Column(
        modifier = modifier
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
            text = campoEtiqueta,
            fontFamily = FuenteTexto,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TintaTenue,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (char in digitos) {
                if (char == ',') {
                    Box(
                        modifier = Modifier
                            .size(width = 20.dp, height = 56.dp)
                            .padding(bottom = 8.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = ",",
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
            if (digitos.length < maxDigitos) {
                CajaDigito(digito = null, activa = true)
            }
        }

        Spacer(Modifier.height(20.dp))

        if (errorMensaje != null) {
            Text(
                text = errorMensaje,
                fontFamily = FuenteTexto,
                fontSize = 12.sp,
                color = Ocre,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Valor detectado por OCR",
                fontFamily = FuenteTexto,
                fontSize = 12.sp,
                color = TintaSuave
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = valorDetectado,
                    fontFamily = FuenteNumeros,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DigitoTexto
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text = unidad,
                    fontFamily = FuenteTexto,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = TintaSuave
                )
            }
        }
    }
}

@Composable
fun CajaDigito(
    digito: String?,
    activa: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
                text = digito,
                fontFamily = FuenteNumeros,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DigitoTexto
            )
        } else {
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

// ── TecladoNumericoMedidor ───────────────────────────────────────────────────

// Teclado numérico en pantalla (0-9, coma decimal opcional y backspace) para digitar el medidor.
@Composable
fun TecladoNumericoMedidor(
    permiteComa: Boolean = false,
    onDigitoPulsado: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val teclas = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(",", "0", "←")
    )

    Column(
        modifier = modifier.padding(horizontal = 20.dp),
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
                                text = tecla,
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

// ── SelectorPeriodoMeses ─────────────────────────────────────────────────────

// Cuadrícula de 12 meses (4x3) para elegir el período de consumo del recibo.
@Composable
fun SelectorPeriodoMeses(
    periodoSeleccionado: PeriodoConsumo,
    onSeleccionarPeriodo: (PeriodoConsumo) -> Unit,
    modifier: Modifier = Modifier
) {
    val ahora = remember { RelojDelSistema().ahora() }
    val ultimos12Meses = remember(ahora) {
        var p = PeriodoConsumo(ahora.year, ahora.monthNumber)
        buildList {
            repeat(12) {
                add(p)
                p = p.anterior()
            }
        }.reversed()
    }

    Column(
        modifier = modifier
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
            text = "SELECCIONA EL PERÍODO (ÚLTIMOS 12 MESES)",
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
