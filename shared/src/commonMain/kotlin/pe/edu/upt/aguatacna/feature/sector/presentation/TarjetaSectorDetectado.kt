package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave

/** La tarjeta de resumen del sector que se detectó para el domicilio. */
@Composable
fun TarjetaSectorDetectado(
    sectorDetectado: String,
    distrito: String,
    continuidad: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            FilaDato("Sector detectado", sectorDetectado, AguaMedia, FuenteNumeros)
            FilaDato("Distrito", distrito, Tinta, FuenteTexto)
            FilaDato("Continuidad del servicio", continuidad, Ocre, FuenteNumeros)
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String, colorValor: Color, fuenteValor: FontFamily) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(etiqueta, fontFamily = FuenteTexto, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
        Text(valor, fontFamily = fuenteValor, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorValor)
    }
}
