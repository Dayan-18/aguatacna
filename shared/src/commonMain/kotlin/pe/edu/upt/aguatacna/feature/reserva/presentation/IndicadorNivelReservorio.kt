package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import pe.edu.upt.aguatacna.core.ui.theme.AguaClara
import pe.edu.upt.aguatacna.core.ui.theme.Blanco

private const val DURACION_ANIMACION_MS = 900
private val MARCAS = listOf(0.25f, 0.5f, 0.75f)

/** El tanque con el agua bajando; con línea punteada cuando el nivel es solo una estimación. */
@Composable
fun IndicadorNivelReservorio(
    fraccion: Float,
    modifier: Modifier = Modifier,
    estimado: Boolean = false
) {
    val nivel by animateFloatAsState(fraccion.coerceIn(0f, 1f), tween(DURACION_ANIMACION_MS))
    Canvas(modifier.size(width = 96.dp, height = 176.dp)) {
        val forma = Path().apply {
            addRoundRect(RoundRect(0f, 0f, size.width, size.height, CornerRadius(size.width * 0.28f)))
        }
        drawPath(forma, Blanco.copy(alpha = 0.14f))
        clipPath(forma) {
            val alto = size.height * nivel
            drawRect(AguaClara, Offset(0f, size.height - alto), Size(size.width, alto))
            MARCAS.forEach { marca ->
                val y = size.height * (1 - marca)
                drawLine(Blanco.copy(alpha = 0.45f), Offset(0f, y), Offset(size.width * 0.18f, y), strokeWidth = 2.dp.toPx())
            }
        }
        val punteado = if (estimado) PathEffect.dashPathEffect(floatArrayOf(14f, 10f)) else null
        drawPath(forma, Blanco.copy(alpha = 0.7f), style = Stroke(3.dp.toPx(), pathEffect = punteado))
    }
}
