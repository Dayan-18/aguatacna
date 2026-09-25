// Componentes visuales usados únicamente en la pantalla General (ReciboScreen): barra superior,
// tarjeta del recibo activo, tarjetas de escaneo, banner Sunass y herramientas.
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Shield
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
private val InfoFondo = Color(0xCCDCF0F2) // 80% opacidad
private val InfoBorde = Color(0x33087E8B)

// ── EstiloEstado ──────────────────────────────────────────────────────────────

// Estilo visual por estado de consumo, con los colores del tema: Ocre (atípico), AguaMedia (normal),
// TintaSuave (sin historial / por promedio).
data class EstiloEstado(
    val colorPrincipal: Color,
    val colorFondo: Color,
    val colorBorde: Color,
    val chipTexto: String,
    val chipColorTexto: Color,
    val chipColorFondo: Color,
    val chipColorBorde: Color,
    val variacionColor: Color,
    val botonHistorialTexto: String,
    val botonHistorialColor: Color
) {
    companion object {
        private val OcreFondo = Color(0xFFFDF2E7)
        private val OcreBorde = Color(0x33E18228)
        private val TealFondo = Color(0xFFE4F3F4)
        private val TealBorde = Color(0x33087E8B)
        private val NeutroFondo = Color(0xFFF0F4F5)
        private val NeutroBorde = Color(0x33667788)

        fun desde(estado: EstadoConsumo): EstiloEstado = when (estado) {
            is EstadoConsumo.Atipico -> EstiloEstado(
                colorPrincipal = Ocre,
                colorFondo = OcreFondo,
                colorBorde = OcreBorde,
                chipTexto = "Alto consumo",
                chipColorTexto = Ocre,
                chipColorFondo = Color(0xFFFEF2E6),
                chipColorBorde = Color(0xFFFDE0B5),
                variacionColor = Ocre,
                botonHistorialTexto = "Ver histórico y cómo reclamar",
                botonHistorialColor = Ocre
            )
            is EstadoConsumo.Normal -> EstiloEstado(
                colorPrincipal = AguaMedia,
                colorFondo = TealFondo,
                colorBorde = TealBorde,
                chipTexto = "Normal",
                chipColorTexto = AguaMedia,
                chipColorFondo = Color(0xFFE4F3F4),
                chipColorBorde = Color(0xFFCAEBED),
                variacionColor = AguaMedia,
                botonHistorialTexto = "Ver histórico de consumo",
                botonHistorialColor = AguaMedia
            )
            is EstadoConsumo.SinHistorial -> EstiloEstado(
                colorPrincipal = AguaMedia,
                colorFondo = TealFondo,
                colorBorde = TealBorde,
                chipTexto = "Registro",
                chipColorTexto = AguaMedia,
                chipColorFondo = Color(0xFFE4F3F4),
                chipColorBorde = Color(0xFFCAEBED),
                variacionColor = AguaMedia,
                botonHistorialTexto = "Ver histórico de consumo",
                botonHistorialColor = AguaMedia
            )
            is EstadoConsumo.FacturadoPorPromedio -> EstiloEstado(
                colorPrincipal = TintaSuave,
                colorFondo = NeutroFondo,
                colorBorde = NeutroBorde,
                chipTexto = "Por promedio",
                chipColorTexto = TintaSuave,
                chipColorFondo = Color(0xFFE8EEF0),
                chipColorBorde = Color(0xFFD0DCE0),
                variacionColor = TintaSuave,
                botonHistorialTexto = "Ver histórico de consumo",
                botonHistorialColor = AguaMedia
            )
        }
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
            color = estilo.chipColorTexto
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

        // Métricas: Consumo facturado y Variación Sunass
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Fondo)
                    .border(1.dp, Divisor, RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Text("Consumo facturado", fontFamily = FuenteTexto, fontSize = 11.sp, color = TintaSuave)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        state.consumoM3.toString(),
                        fontFamily = FuenteNumeros,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Tinta
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("m³", fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(estilo.colorFondo)
                    .border(1.dp, estilo.colorBorde, RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Text("Variación Sunass", fontFamily = FuenteTexto, fontSize = 11.sp, color = estilo.variacionColor)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        state.variacionTexto,
                        fontFamily = FuenteNumeros,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = estilo.variacionColor
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "vs prom.",
                        fontFamily = FuenteTexto,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = estilo.variacionColor.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Botón principal dinámico (naranja si atípico, teal si normal)
        Button(
            onClick = onVerHistorial,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = estilo.botonHistorialColor)
        ) {
            if (state.esAtipico) {
                Icon(Icons.Outlined.ErrorOutline, contentDescription = null, modifier = Modifier.size(18.dp), tint = Blanco)
                Spacer(Modifier.width(8.dp))
            }
            Text(
                estilo.botonHistorialTexto,
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

// ── BannerSunass ─────────────────────────────────────────────────────────────

// Banner que explica la protección legal de Sunass ante consumos que exceden el 100% del promedio.
@Composable
fun BannerSunass(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(InfoFondo)
            .border(1.dp, InfoBorde, RoundedCornerShape(20.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(AguaMedia.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Info,
                contentDescription = null,
                tint = AguaMedia,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = "Norma Sunass: Si tu consumo supera el 100% del promedio histórico, la EPS Tacna debe inspeccionar tu predio antes de cualquier corte.",
            fontFamily = FuenteTexto,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TintaSuave,
            lineHeight = 16.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

// ── SeccionHerramientas / TarjetaHerramienta ─────────────────────────────────

// Bloque de herramientas del recibo: histórico de 6 meses, reclamos Sunass y descarga de comprobantes en PDF.
@Composable
fun SeccionHerramientas(
    promedioHistorico: Int,
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
            subtitulo = "Promedio regular: $promedioHistorico m³",
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

        // 2. Tus reclamos Sunass
        TarjetaHerramienta(
            iconoVector = Icons.Outlined.Shield,
            iconoColor = Ocre,
            iconoFondo = Color(0xFFFFF7ED),
            titulo = "Tus reclamos Sunass",
            subtitulo = "Paso 1: Inspección técnica domiciliaria",
            badgeTexto = "1 ACTIVO",
            trailingContent = {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TintaTenue,
                    modifier = Modifier.size(20.dp)
                )
            }
        )

        // 3. Descargar recibo oficial
        TarjetaHerramienta(
            iconoVector = Icons.Outlined.FileDownload,
            iconoColor = TintaSuave,
            iconoFondo = Color(0xFFF1F5F9),
            titulo = "Descargar recibo oficial",
            subtitulo = "PDF con validez legal EPS Tacna",
            trailingContent = {
                Text(
                    "PDF 420 KB",
                    fontFamily = FuenteTexto,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AguaMedia,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TealClaro)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
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
    badgeTexto: String? = null,
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        titulo,
                        fontFamily = FuenteTexto,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Tinta
                    )
                    if (badgeTexto != null) {
                        Text(
                            badgeTexto,
                            fontFamily = FuenteTexto,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Ocre,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Ocre.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(subtitulo, fontFamily = FuenteTexto, fontSize = 11.sp, color = TintaSuave)
            }
        }
        trailingContent()
    }
}
