package pe.edu.upt.aguatacna.feature.recibo.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave

// Bloque de herramientas de la pantalla principal: acceso al histórico de 6 meses con su promedio.
@Composable
fun SeccionHerramientas(promedioHistorico: Int?) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), Arrangement.SpaceBetween) {
            EtiquetaSeccion("HERRAMIENTAS Y REPORTES")
            Text("EPS Tacna", style = estilo(11.sp, FontWeight.SemiBold, AguaMedia))
        }
        Row(
            modifier = Modifier.fillMaxWidth().tarjeta(radio = 22.dp, relleno = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconoEnCaja(Icons.Outlined.BarChart, AguaMedia, 40.dp, Modifier.caja(12.dp, Color(0xFFF0F8F8)))
                Column {
                    Text("Histórico de 6 meses", style = estilo(12.sp, FontWeight.Bold, Tinta))
                    Text(
                        promedioHistorico?.let { "Promedio regular: $it m³" } ?: "Aún sin meses previos para promediar",
                        style = estilo(11.sp, color = TintaSuave)
                    )
                }
            }
            MiniGrafico()
        }
    }
}

// Seis barritas decorativas; la última, en ocre, representa el mes actual.
@Composable
private fun MiniGrafico() {
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        listOf(14, 12, 14, 16, 16).forEach { alto -> Barrita(alto, AguaMedia.copy(alpha = 0.7f)) }
        Barrita(28, Ocre)
    }
}

@Composable
private fun Barrita(alto: Int, color: Color) {
    Box(Modifier.width(6.dp).height(alto.dp).clip(RoundedCornerShape(50)).background(color))
}
