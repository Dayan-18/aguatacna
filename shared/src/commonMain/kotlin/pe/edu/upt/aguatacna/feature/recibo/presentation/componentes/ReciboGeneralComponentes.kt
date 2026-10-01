// Componentes visuales usados únicamente en la pantalla General (ReciboScreen): barra superior,
// tarjeta del recibo activo, tarjetas de escaneo y herramientas.
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.presentation.ReciboUiState

// ── Colores privados ──────────────────────────────────────────────────────────

private val TealClaro = Color(0xFFE4F3F4)
private val TealBorde = Color(0x33087E8B)
private val RojoFondo = Color(0xFFFEF2F2)
private val Rojo = Color(0xFFDC2626)

// ── EstiloEstado ──────────────────────────────────────────────────────────────

// Estilo visual del estado de consumo: Ocre si es alto consumo (> 100 m³), AguaMedia en cualquier otro caso.
data class EstiloEstado(
    val chipTexto: String,
    val chipColorTexto: Color,
    val chipColorFondo: Color,
    val chipColorBorde: Color
) {
    companion object {
        private val ALTO_CONSUMO = EstiloEstado("Alto consumo", Ocre, Color(0xFFFEF2E6), Color(0xFFFDE0B5))
        private val NORMAL = EstiloEstado("Normal", AguaMedia, Color(0xFFE4F3F4), Color(0xFFCAEBED))

        fun desde(estado: EstadoConsumo): EstiloEstado =
            if (estado == EstadoConsumo.ALTO_CONSUMO) ALTO_CONSUMO else NORMAL
    }
}

// ── BadgeEstado ───────────────────────────────────────────────────────────────

// Badge reutilizable que muestra el estado de consumo según el EstiloEstado (Normal, Alto, Registro, Por promedio).
@Composable
fun BadgeEstado(
    estilo: EstiloEstado,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(estilo.chipColorFondo)
            .border(1.dp, estilo.chipColorBorde, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(estilo.chipColorTexto)
        )
        Text(
            text = estilo.chipTexto,
            fontFamily = FuenteTexto,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = estilo.chipColorTexto,
            maxLines = 1,
            softWrap = false
        )
    }
}

// ── ReciboBarraSuperior ──────────────────────────────────────────────────────

// Barra superior estándar del módulo Recibo: retroceso, título, subtítulo y slot opcional a la derecha.
@Composable
fun ReciboBarraSuperior(
    titulo: String,
    subtitulo: String,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            IconButton(
                onClick = onVolver,
                modifier = Modifier
                    .size(40.dp)
                    .sombraSuave(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Blanco.copy(alpha = 0.9f))
                    .border(1.dp, Divisor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
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
                    text = titulo,
                    fontFamily = FuenteTexto,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Tinta
                )
                Text(
                    text = subtitulo,
                    fontFamily = FuenteTexto,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TintaTenue
                )
            }
        }
        trailingContent?.invoke()
    }
}

// ── TarjetaReciboActivo ──────────────────────────────────────────────────────

// Tarjeta hero del recibo activo: importe, vencimiento, consumo en m³, variación y acciones dinámicas.
@Composable
fun TarjetaReciboActivo(
    state: ReciboUiState.ConDatos,
    estilo: EstiloEstado,
    onVerHistorial: () -> Unit,
    onRevisarLectura: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sombraSuave(26.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Blanco)
            .border(1.dp, Divisor.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
            .padding(20.dp)
    ) {
        // Fila superior: Mes + badge estado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TealClaro),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ReceiptLong,
                        contentDescription = null,
                        tint = AguaMedia,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        "RECIBO ACTIVO",
                        fontFamily = FuenteTexto,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TintaTenue,
                        letterSpacing = 1.sp
                    )
                    Text(
                        state.mes,
                        fontFamily = FuenteTexto,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Tinta
                    )
                }
            }
            BadgeEstado(estilo)
        }

        Spacer(Modifier.height(12.dp))

        // Precio y vencimiento
        HorizontalDivider(color = Divisor)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    "Total a pagar",
                    fontFamily = FuenteTexto,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TintaTenue
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("S/", fontFamily = FuenteNumeros, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TintaSuave)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        state.importeDisplay,
                        fontFamily = FuenteNumeros,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Tinta
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "Vencimiento",
                    fontFamily = FuenteTexto,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TintaTenue
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    state.fechaVencimiento,
                    fontFamily = FuenteTexto,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Rojo,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RojoFondo)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
        HorizontalDivider(color = Divisor)

        Spacer(Modifier.height(10.dp))

        // Métrica: Consumo facturado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Fondo)
                .border(1.dp, Divisor, RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            Text("Consumo facturado", fontFamily = FuenteTexto, fontSize = 11.sp, color = TintaSuave)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    state.recibo.consumoM3.toString(),
                    fontFamily = FuenteNumeros,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Tinta
                )
                Spacer(Modifier.width(4.dp))
                Text("m³", fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Botón principal: naranja si hay alto consumo, teal si no
        Button(
            onClick = onVerHistorial,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = estilo.chipColorTexto)
        ) {
            if (state.esAltoConsumo) {
                Icon(Icons.Outlined.ErrorOutline, contentDescription = null, modifier = Modifier.size(18.dp), tint = Blanco)
                Spacer(Modifier.width(8.dp))
            }
            Text(
                "Ver histórico de consumo",
                fontFamily = FuenteTexto,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.height(8.dp))

        // Botón "Revisar lectura y datos detectados"
        OutlinedButton(
            onClick = onRevisarLectura,
            modifier = Modifier.fillMaxWidth().height(40.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Outlined.Visibility, contentDescription = null, modifier = Modifier.size(16.dp), tint = AguaMedia)
            Spacer(Modifier.width(6.dp))
            Text(
                "Revisar lectura y datos detectados",
                fontFamily = FuenteTexto,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = TintaSuave
            )
        }
    }
}

// ── TarjetaEscanearPrincipal / TarjetaEscanear ──────────────────────────────

// Tarjeta destacada de escaneo para el estado vacío (sin ningún recibo registrado).
@Composable
fun TarjetaEscanearPrincipal(
    onEscanearRecibo: () -> Unit,
    onIngresarManual: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sombraSuave(24.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Blanco)
            .border(1.dp, Divisor.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(TealClaro)
                .border(1.dp, TealBorde, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.PhotoCamera,
                contentDescription = null,
                tint = AguaMedia,
                modifier = Modifier.size(36.dp)
            )
        }

        Text(
            "Escanea tu recibo",
            fontFamily = FuenteTexto,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Tinta
        )

        Text(
            "Toma una foto de tu recibo de EPS Tacna y la app leerá los datos automáticamente.",
            fontFamily = FuenteTexto,
            fontSize = 13.sp,
            color = TintaSuave,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(Modifier.height(4.dp))

        Button(
            onClick = onEscanearRecibo,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AguaMedia)
        ) {
            Icon(Icons.Outlined.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp), tint = Blanco)
            Spacer(Modifier.width(8.dp))
            Text(
                "Tomar foto del recibo",
                fontFamily = FuenteTexto,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Text(
            text = "o ingresa los datos a mano",
            fontFamily = FuenteTexto,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = AguaMedia,
            modifier = Modifier
                .clickable(onClick = onIngresarManual)
                .padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
        )
    }
}

// Tarjeta compacta (debajo del recibo activo) para escanear un nuevo recibo o registrarlo a mano.
@Composable
fun TarjetaEscanear(
    onEscanearRecibo: () -> Unit,
    onIngresarManual: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .sombraSuave(24.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Blanco)
            .border(1.dp, Divisor.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(TealClaro)
                .border(1.dp, TealBorde, RoundedCornerShape(16.dp))
                .clickable(onClick = onEscanearRecibo),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.PhotoCamera,
                contentDescription = null,
                tint = AguaMedia,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Escanear nuevo recibo",
                fontFamily = FuenteTexto,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Tinta
            )
            Text(
                "Sube o toma una foto para digitalizar.",
                fontFamily = FuenteTexto,
                fontSize = 11.sp,
                color = TintaSuave,
                maxLines = 1
            )
            Text(
                text = "o ingresar otro recibo a mano",
                fontFamily = FuenteTexto,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = AguaMedia,
                modifier = Modifier
                    .clickable(onClick = onIngresarManual)
                    .padding(top = 2.dp)
            )
        }

        Spacer(Modifier.width(8.dp))

        IconButton(
            onClick = onEscanearRecibo,
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AguaMedia),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Escanear", tint = Blanco, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ── SeccionHerramientas / TarjetaHerramienta ─────────────────────────────────

// Bloque de herramientas del recibo: histórico de 6 meses.
@Composable
fun SeccionHerramientas(
    promedioHistorico: Int?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "HERRAMIENTAS Y REPORTES",
                fontFamily = FuenteTexto,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TintaTenue,
                letterSpacing = 1.sp
            )
            Text(
                "EPS Tacna",
                fontFamily = FuenteTexto,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = AguaMedia
            )
        }

        // 1. Histórico de 6 meses
        TarjetaHerramienta(
            iconoVector = Icons.Outlined.BarChart,
            iconoColor = AguaMedia,
            iconoFondo = Color(0xFFF0F8F8),
            titulo = "Histórico de 6 meses",
            subtitulo = promedioHistorico?.let { "Promedio regular: $it m³" } ?: "Aún sin meses previos para promediar",
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    val alturas = listOf(14.dp, 12.dp, 14.dp, 16.dp, 16.dp)
                    alturas.forEach { h ->
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(h)
                                .clip(RoundedCornerShape(50))
                                .background(AguaMedia.copy(alpha = 0.7f))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(28.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Ocre)
                    )
                }
            }
        )
    }
}

@Composable
fun TarjetaHerramienta(
    iconoVector: ImageVector,
    iconoColor: Color,
    iconoFondo: Color,
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    trailingContent: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .sombraSuave(22.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Blanco)
            .border(1.dp, Divisor.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconoFondo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    iconoVector,
                    contentDescription = null,
                    tint = iconoColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    titulo,
                    fontFamily = FuenteTexto,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Tinta
                )
                Text(subtitulo, fontFamily = FuenteTexto, fontSize = 11.sp, color = TintaSuave)
            }
        }
        trailingContent()
    }
}
