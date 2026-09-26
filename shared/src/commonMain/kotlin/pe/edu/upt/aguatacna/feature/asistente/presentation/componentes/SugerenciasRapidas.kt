package pe.edu.upt.aguatacna.feature.asistente.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto

private val ColorBordeSugerencia = Divisor.copy(alpha = 0.8f)
private val ColorTextoSugerencia = Color(0xFF098A98)

/**
 * Fila horizontal con sugerencias de preguntas frecuentes.
 */
@Composable
fun SugerenciasRapidas(
    sugerencias: List<String>,
    onSugerenciaSeleccionada: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sugerencias.forEach { sugerencia ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Blanco)
                    .border(1.dp, ColorBordeSugerencia, RoundedCornerShape(20.dp))
                    .clickable { onSugerenciaSeleccionada(sugerencia) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = sugerencia,
                    fontFamily = FuenteTexto,
                    fontSize = 12.5.sp,
                    color = ColorTextoSugerencia
                )
            }
        }
    }
}
