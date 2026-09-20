package pe.edu.upt.aguatacna.feature.asistente.presentation

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave

private val ColorCabeceraFondo = Color(0xFF03444C)
private val ColorCabeceraCirculo = Color(0xFF0A6774)
private val ColorBurbujaUsuario = Color(0xFF098A98)
private val ColorTextoAsistente = Color(0xFF1E293B)
private val ColorPlaceholder = Color(0xFF94A3B8)

/**
 * Pantalla del Asistente Hídrico.
 * Representa la interfaz visual del chat con el asistente de agua.
 */
@Composable
fun AsistenteScreen() {
    IconosClarosEnBarraDeEstado(claros = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
    ) {
        // ── Cabecera con degradado y esquinas redondeadas ──
        CabeceraAsistente()

        // ── Área de mensajes del chat (scrollable) ──
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Mensaje del asistente (Bienvenida)
            BurbujaAsistente(
                texto = "Hola. Conozco tu hogar: tanque de 1100 L, 4 personas, sector Ciudad Nueva. ¿En qué te ayudo?"
            )

            // 2. Mensaje del usuario
            BurbujaUsuario(
                texto = "Mi recibo salió el doble que siempre, ¿qué hago?"
            )

            // 3. Mensaje del asistente (Diagnóstico consumo)
            BurbujaAsistente(
                texto = "Tu consumo de agosto fue 33 m³ contra un promedio de 16 m³. Supera el 100 %, así que la Sunass lo considera consumo atípico y puedes reclamar."
            )

            // 4. Mensaje del asistente (Instrucción fuga)
            BurbujaAsistente(
                texto = "Antes de ir revisa fugas: cierra todos los caños y mira si el medidor sigue girando."
            )

            // 5. Mensaje del usuario (Pregunta seguimiento)
            BurbujaUsuario(
                texto = "¿Dónde reclamo?"
            )

            Spacer(Modifier.height(8.dp))
        }

        // ── Barra inferior para escribir pregunta ──
        BarraEntradaPregunta()
    }
}

// ──────────────────────────────────────────────────────────────────────
// Cabecera superior
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun CabeceraAsistente() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(ColorCabeceraFondo)
    ) {
        // Efecto visual de curva / resplandor en la esquina superior derecha
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            drawCircle(
                color = ColorCabeceraCirculo.copy(alpha = 0.55f),
                radius = size.width * 0.52f,
                center = Offset(size.width * 0.90f, size.height * 0.08f)
            )
        }

        // Contenido de la cabecera
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Contenedor del ícono de destellos / IA
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Blanco.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = Blanco,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = "Asistente hídrico",
                    fontFamily = FuenteTexto,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blanco,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = "Pregunta en tus palabras",
                    fontFamily = FuenteTexto,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Blanco.copy(alpha = 0.85f)
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Burbujas de mensajes
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun BurbujaAsistente(texto: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .sombraSuave(16.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Blanco)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = texto,
                fontFamily = FuenteTexto,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Normal,
                color = ColorTextoAsistente,
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
private fun BurbujaUsuario(texto: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(ColorBurbujaUsuario)
                .padding(horizontal = 16.dp, vertical = 13.dp)
        ) {
            Text(
                text = texto,
                fontFamily = FuenteTexto,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                color = Blanco,
                lineHeight = 19.sp
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Barra de entrada inferior
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun BarraEntradaPregunta() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(50))
                .background(Blanco)
                .border(1.dp, Divisor.copy(alpha = 0.9f), RoundedCornerShape(50))
                .padding(start = 20.dp, end = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Escribe tu pregunta...",
                fontFamily = FuenteTexto,
                fontSize = 14.sp,
                color = ColorPlaceholder,
                modifier = Modifier.weight(1f)
            )

            // Botón circular enviar con flecha arriba
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ColorBurbujaUsuario),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.ArrowUpward,
                    contentDescription = "Enviar pregunta",
                    tint = Blanco,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
