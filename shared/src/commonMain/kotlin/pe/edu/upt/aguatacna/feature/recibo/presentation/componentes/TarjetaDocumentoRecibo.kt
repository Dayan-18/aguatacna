package pe.edu.upt.aguatacna.feature.recibo.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador

private val LineaClara = Color(0xFFCFECEB)

// Tarjeta del recibo en revisión: dibujo de documento, período y número de medidor (o de recibo).
@Composable
fun TarjetaDocumentoRecibo(borrador: ReciboBorrador?) {
    val mes = borrador?.periodoConsumo?.valor?.displayCompleto ?: "Seleccionar período"
    val suministro = borrador?.numeroMedidor?.valor?.let { "Medidor $it" }
        ?: borrador?.numeroRecibo?.valor?.let { "Recibo N° $it" }
        ?: "Sin número de medidor"

    Row(
        modifier = Modifier.fillMaxWidth().tarjeta(radio = 16.dp, relleno = 16.dp, borde = Divisor.copy(alpha = 0.3f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DibujoDocumento()
        Column {
            Text("Recibo EPS Tacna", style = estilo(12.sp, FontWeight.Medium, TintaTenue))
            Text(mes, style = estilo(20.sp, FontWeight.ExtraBold, Tinta))
            Text(suministro, style = estilo(12.sp, color = TintaTenue, numeros = true), letterSpacing = 1.sp)
        }
    }
}

@Composable
private fun DibujoDocumento() {
    Column(
        modifier = Modifier.size(width = 64.dp, height = 72.dp)
            .caja(12.dp, Color(0xFFF5FBFB), Color(0xFFD6EDEC)).padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Linea(28.dp, 4.dp, Color(0xFFB6DFDD))
            Linea(36.dp, 4.dp, LineaClara)
            Linea(20.dp, 4.dp, LineaClara)
        }
        Linea(36.dp, 6.dp, TealTexto)
    }
}

@Composable
private fun Linea(ancho: Dp, alto: Dp, color: Color) {
    Box(Modifier.width(ancho).height(alto).clip(RoundedCornerShape(50)).background(color))
}
