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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.AguaClara
import pe.edu.upt.aguatacna.core.ui.theme.Blanco

private const val DURACION_ANIMACION_MS = 900

// Alturas de las marcas medidas desde arriba en el Figma (47, 96 y 145 de 196).
private val MARCAS = listOf(47f / 196, 96f / 196, 145f / 196)

/** El tanque del Figma (106 × 196) con el agua bajando. */
@Composable
fun IndicadorNivelReservorio(fraccion: Float, modifier: Modifier = Modifier) {
    val nivel by animateFloatAsState(fraccion.coerceIn(0f, 1f), tween(DURACION_ANIMACION_MS))
    Canvas(modifier.size(width = 106.dp, height = 196.dp)) {
        val grosorBorde = 2.dp.toPx()
        val forma = formaDelTanque(size, inset = 0f)
        drawPath(forma, Blanco.copy(alpha = 0.12f))
        clipPath(forma) {
            val alto = size.height * nivel
            val superficie = size.height - alto
            if (alto > 0f) {
                drawRect(Brush.verticalGradient(listOf(AguaClara, Agua), startY = superficie, endY = size.height), Offset(0f, superficie), Size(size.width, alto))
                drawRect(Blanco.copy(alpha = 0.55f), Offset(0f, superficie), Size(size.width, 4.dp.toPx()))
            }
            MARCAS.forEach { marca ->
                drawRect(Blanco.copy(alpha = 0.4f), Offset(0f, size.height * marca), Size(11.dp.toPx(), 1.5.dp.toPx()))
            }
        }
        drawPath(formaDelTanque(size, inset = grosorBorde / 2), Blanco.copy(alpha = 0.35f), style = Stroke(grosorBorde))
    }
}

private fun DrawScope.formaDelTanque(tamano: Size, inset: Float): Path =
    Path().apply {
        addRoundRect(
            RoundRect(inset, inset, tamano.width - inset, tamano.height - inset, CornerRadius(20.dp.toPx() - inset))
        )
    }
