package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Coral
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tenue

@Composable
fun MapaReportes() {
    Box(
        Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(18.dp)).background(Tenue)
    ) {
        Canvas(Modifier.matchParentSize()) {
            val linea = Color(0xFFCEE1E2)
            repeat(4) { indice ->
                val y = size.height * (indice + 1) / 5f
                drawLine(linea, start = androidx.compose.ui.geometry.Offset(18f, y), end = androidx.compose.ui.geometry.Offset(size.width - 18f, y), strokeWidth = 2f)
            }
            repeat(4) { indice ->
                val x = size.width * (indice + 1) / 5f
                drawLine(linea, start = androidx.compose.ui.geometry.Offset(x, 12f), end = androidx.compose.ui.geometry.Offset(x, size.height - 12f), strokeWidth = 2f)
            }
        }
        PinMapa("3", Coral, 70, 48)
        PinMapa("5", Coral, 188, 29)
        PinMapa("1", Agua, 157, 92)
        PinMapa("2", Ocre, 246, 104)
        PinMapa("4", Coral, 101, 124)
    }
}

@Composable
private fun PinMapa(numero: String, color: Color, x: Int, y: Int) {
    Box(
        Modifier.offset(x.dp, y.dp).size(26.dp).clip(CircleShape).background(Blanco),
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.size(20.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
            Text(numero, color = Blanco, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}
