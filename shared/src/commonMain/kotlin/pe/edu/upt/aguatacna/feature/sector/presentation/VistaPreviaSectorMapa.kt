package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.Tenue

/** Vista previa estilizada del mapa en Registrar domicilio: el recuadro del sector con su pin. */
@Composable
fun VistaPreviaSectorMapa(etiquetaSector: String, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(18.dp)).background(Tenue)
            .drawBehind { dibujarCuadricula() },
        contentAlignment = Alignment.Center
    ) {
        RecuadroSector(etiquetaSector)
    }
}

@Composable
private fun RecuadroSector(etiqueta: String) {
    Box(
        Modifier.fillMaxWidth().padding(horizontal = 44.dp).height(130.dp).drawBehind {
            drawRoundRect(
                color = Agua,
                cornerRadius = CornerRadius(12.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(11f, 9f)))
            )
        },
        contentAlignment = Alignment.Center
    ) {
        Marcador()
        EtiquetaSector(etiqueta, Modifier.align(Alignment.TopCenter).offset(y = (-11).dp))
    }
}

@Composable
private fun Marcador() {
    Box(
        Modifier.size(52.dp).clip(CircleShape).background(Agua.copy(alpha = 0.22f)),
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.size(20.dp).clip(CircleShape).background(AguaMedia))
    }
}

@Composable
private fun EtiquetaSector(texto: String, modifier: Modifier) {
    Text(
        texto,
        modifier = modifier.clip(RoundedCornerShape(8.dp)).background(Blanco)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        fontFamily = FuenteTexto,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = AguaMedia
    )
}

// Líneas tenues de fondo que sugieren la retícula de un mapa.
private fun DrawScope.dibujarCuadricula() {
    val pasoX = size.width / 4
    for (i in 1..3) drawLine(Divisor, Offset(pasoX * i, 0f), Offset(pasoX * i, size.height), 1f)
    val pasoY = size.height / 3
    for (i in 1..2) drawLine(Divisor, Offset(0f, pasoY * i), Offset(size.width, pasoY * i), 1f)
}
