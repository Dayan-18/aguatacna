package pe.edu.upt.aguatacna.feature.asistente.presentation.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto

private val ColorBurbujaUsuario = Color(0xFF098A98)
private val ColorPlaceholder = Color(0xFF94A3B8)
private val ColorTextoInput = Color(0xFF1E293B)

/**
 * Barra inferior interactiva para redactar y enviar preguntas al Asistente Hídrico / n8n.
 */
@Composable
fun BarraEntradaAsistente(
    texto: String,
    onTextoCambiado: (String) -> Unit,
    onEnviar: () -> Unit,
    estaEscribiendo: Boolean,
    modifier: Modifier = Modifier
) {
    val botonHabilitado = texto.isNotBlank() && !estaEscribiendo

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
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
            // Campo de entrada de texto
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (texto.isEmpty()) {
                    Text(
                        text = "Escribe un mensaje",
                        fontFamily = FuenteTexto,
                        fontSize = 14.sp,
                        color = ColorPlaceholder
                    )
                }

                BasicTextField(
                    value = texto,
                    onValueChange = onTextoCambiado,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontFamily = FuenteTexto,
                        fontSize = 14.sp,
                        color = ColorTextoInput
                    ),
                    cursorBrush = SolidColor(ColorBurbujaUsuario),
                    singleLine = true,
                    enabled = !estaEscribiendo,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (botonHabilitado) {
                                onEnviar()
                            }
                        }
                    )
                )
            }

            // Botón circular enviar con flecha arriba
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (botonHabilitado) ColorBurbujaUsuario else ColorBurbujaUsuario.copy(alpha = 0.45f)
                    )
                    .clickable(enabled = botonHabilitado) {
                        onEnviar()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (estaEscribiendo) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Blanco,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Filled.ArrowUpward,
                        contentDescription = "Enviar mensaje",
                        tint = Blanco,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
