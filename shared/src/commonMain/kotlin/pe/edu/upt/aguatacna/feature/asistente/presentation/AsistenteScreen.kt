package pe.edu.upt.aguatacna.feature.asistente.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.feature.asistente.presentation.componentes.BarraEntradaAsistente
import pe.edu.upt.aguatacna.feature.asistente.presentation.componentes.BurbujaAsistente
import pe.edu.upt.aguatacna.feature.asistente.presentation.componentes.BurbujaUsuario
import pe.edu.upt.aguatacna.feature.asistente.presentation.componentes.IndicadorEscribiendo

private val ColorCabeceraFondo = Color(0xFF03444C)
private val ColorCabeceraCirculo = Color(0xFF0A6774)

/**
 * Pantalla principal del Asistente Hídrico conectada con el flujo de n8n.
 * Proporciona un chat interactivo, reactivo y responsivo con el asistente inteligente.
 */
@Composable
fun AsistenteScreen(
    viewModel: AsistenteViewModel = remember { AsistenteViewModel.desdeInyeccion() }
) {
    IconosClarosEnBarraDeEstado(claros = true)

    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberLazyListState()

    // Auto-scroll hacia el último mensaje cuando llega una respuesta o se escribe
    LaunchedEffect(uiState.mensajes.size, uiState.estaEscribiendo) {
        val totalElementos = uiState.mensajes.size + if (uiState.estaEscribiendo) 1 else 0
        if (totalElementos > 0) {
            scrollState.animateScrollToItem(totalElementos - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .imePadding()
    ) {
        // ── Cabecera con degradado y esquinas redondeadas ──
        CabeceraAsistente(
            onReiniciarChat = { viewModel.reiniciarConversacion() }
        )

        // ── Lista reactiva de mensajes del chat ──
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(
                items = uiState.mensajes,
                key = { it.id }
            ) { mensaje ->
                if (mensaje.esUsuario) {
                    BurbujaUsuario(texto = mensaje.texto)
                } else {
                    BurbujaAsistente(
                        texto = mensaje.texto,
                        esError = mensaje.esError
                    )
                }
            }

            // Indicador de "escribiendo / pensando"
            if (uiState.estaEscribiendo) {
                item(key = "indicador_escribiendo") {
                    IndicadorEscribiendo()
                }
            }
        }

        // ── Barra inferior para escribir pregunta ──
        BarraEntradaAsistente(
            texto = uiState.textoEntrada,
            onTextoCambiado = viewModel::onTextoEntradaCambiado,
            onEnviar = { viewModel.enviarMensaje() },
            estaEscribiendo = uiState.estaEscribiendo
        )
    }
}

// ──────────────────────────────────────────────────────────────────────
// Cabecera superior
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun CabeceraAsistente(
    onReiniciarChat: () -> Unit
) {
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
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

            // Botón para reiniciar conversación
            IconButton(
                onClick = onReiniciarChat,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Blanco.copy(alpha = 0.12f))
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "Nueva conversación",
                    tint = Blanco,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
