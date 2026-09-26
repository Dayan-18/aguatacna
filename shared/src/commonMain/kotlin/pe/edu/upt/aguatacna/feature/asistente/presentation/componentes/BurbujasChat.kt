package pe.edu.upt.aguatacna.feature.asistente.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave

private val ColorBurbujaUsuario = Color(0xFF098A98)
private val ColorTextoAsistente = Color(0xFF1E293B)
private val ColorFondoError = Color(0xFFFFF1F2)
private val ColorTextoError = Color(0xFFBE123C)

/**
 * Burbuja para los mensajes emitidos por el Asistente Hídrico (IA / n8n).
 */
@Composable
fun BurbujaAsistente(
    texto: String,
    esError: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .fillMaxWidth(0.85f)
                .sombraSuave(12.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 18.dp,
                        bottomEnd = 18.dp,
                        bottomStart = 18.dp
                    )
                )
                .background(if (esError) ColorFondoError else Blanco)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = texto,
                fontFamily = FuenteTexto,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Normal,
                color = if (esError) ColorTextoError else ColorTextoAsistente,
                lineHeight = 20.sp
            )
        }
    }
}

/**
 * Burbuja para los mensajes escritos por el usuario.
 */
@Composable
fun BurbujaUsuario(
    texto: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 290.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 4.dp,
                        bottomEnd = 18.dp,
                        bottomStart = 18.dp
                    )
                )
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
