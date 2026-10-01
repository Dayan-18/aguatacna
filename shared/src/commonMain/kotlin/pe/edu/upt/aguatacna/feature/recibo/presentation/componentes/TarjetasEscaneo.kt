// Tarjetas para escanear un recibo o ingresarlo a mano: la grande (sin recibos) y la compacta.
package pe.edu.upt.aguatacna.feature.recibo.presentation.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave

// Tarjeta destacada para el estado vacío (sin ningún recibo registrado).
@Composable
fun TarjetaEscanearPrincipal(onEscanearRecibo: () -> Unit, onIngresarManual: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().tarjeta(radio = 28.dp, relleno = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconoEnCaja(Icons.Outlined.PhotoCamera, AguaMedia, 72.dp, Modifier.caja(20.dp, TealClaro, TealBorde))
        Text("Escanea tu recibo", style = estilo(18.sp, FontWeight.Bold, Tinta))
        Text(
            "Toma una foto de tu recibo de EPS Tacna y la app leerá los datos automáticamente.",
            style = estilo(13.sp, color = TintaSuave),
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(Modifier.height(4.dp))
        Button(
            onClick = onEscanearRecibo,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AguaMedia)
        ) {
            Icon(Icons.Outlined.PhotoCamera, null, Modifier.size(18.dp), tint = Blanco)
            Spacer(Modifier.width(8.dp))
            Text("Tomar foto del recibo", style = estilo(14.sp, FontWeight.Bold))
        }
        Text(
            "o ingresa los datos a mano",
            style = estilo(13.sp, FontWeight.SemiBold, AguaMedia),
            modifier = Modifier.clickable(onClick = onIngresarManual).padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// Tarjeta compacta (debajo del recibo activo) para escanear un nuevo recibo o registrarlo a mano.
@Composable
fun TarjetaEscanear(onEscanearRecibo: () -> Unit, onIngresarManual: () -> Unit) {
    Row(Modifier.fillMaxWidth().tarjeta(radio = 24.dp, relleno = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        IconoEnCaja(
            Icons.Outlined.PhotoCamera, AguaMedia, 56.dp,
            Modifier.caja(16.dp, TealClaro, TealBorde).clickable(onClick = onEscanearRecibo)
        )
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text("Escanear nuevo recibo", style = estilo(14.sp, FontWeight.Bold, Tinta))
            Text("Sube o toma una foto para digitalizar.", style = estilo(11.sp, color = TintaSuave), maxLines = 1)
            Text(
                "o ingresar otro recibo a mano",
                style = estilo(11.5.sp, FontWeight.SemiBold, AguaMedia),
                modifier = Modifier.clickable(onClick = onIngresarManual).padding(top = 2.dp)
            )
        }
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onEscanearRecibo, modifier = Modifier.size(40.dp)) {
            IconoEnCaja(Icons.Default.Add, Blanco, 32.dp, Modifier.caja(10.dp, AguaMedia), tamanoIcono = 18.dp)
        }
    }
}
