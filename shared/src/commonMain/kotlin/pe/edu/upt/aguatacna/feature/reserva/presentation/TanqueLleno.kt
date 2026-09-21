package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.AguaClara
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Tenue

// Alturas de las marcas medidas desde arriba en el Figma (35,5, 73 y 110,5 de 150).
private val MARCAS = listOf(35.5f / 150, 73f / 150, 110.5f / 150)

/** El tanque lleno sobre fondo claro (pantalla "Registrar llenado"): 96 × 150 con las marcas en turquesa. */
@Composable
fun TanqueLleno(modifier: Modifier = Modifier) {
    Canvas(modifier.size(width = 96.dp, height = 150.dp)) {
        val borde = 2.dp.toPx()
        val forma = Path().apply { addRoundRect(RoundRect(0f, 0f, size.width, size.height, CornerRadius(18.dp.toPx()))) }
        drawPath(forma, Tenue)
        clipPath(forma) {
            drawRect(Brush.verticalGradient(listOf(AguaClara, Agua)), Offset.Zero, size)
            drawRect(Blanco.copy(alpha = 0.6f), Offset.Zero, Size(size.width, 4.dp.toPx()))
            MARCAS.forEach { marca ->
                drawRect(Agua.copy(alpha = 0.45f), Offset(0f, size.height * marca), Size(9.6.dp.toPx(), 1.5.dp.toPx()))
            }
        }
        val interior = Path().apply {
            addRoundRect(RoundRect(borde / 2, borde / 2, size.width - borde / 2, size.height - borde / 2, CornerRadius(18.dp.toPx() - borde / 2)))
        }
        drawPath(interior, Agua.copy(alpha = 0.35f), style = Stroke(borde))
    }
}
