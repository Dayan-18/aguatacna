package pe.edu.upt.aguatacna.feature.recibo.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue

private val Cursor = Color(0xFF0992A5)

// Casillas estilo medidor con lo que el usuario va escribiendo, el error y el valor detectado.
@Composable
fun DisplayDigitosMedidor(campo: TipoCampoEdicion, digitos: String, valorDetectado: String, errorMensaje: String?) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 24.dp)
            .tarjeta(radio = 24.dp, relleno = 20.dp, borde = null),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "${campo.nombre} en ${campo.unidad}".uppercase(),
            style = estilo(11.sp, FontWeight.SemiBold, TintaTenue),
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            digitos.forEach { caracter ->
                if (caracter == ',') Coma() else CajaDigito(caracter.toString())
            }
            if (digitos.length < campo.maxDigitos) CajaDigito(null)
        }
        Spacer(Modifier.height(20.dp))
        if (errorMensaje != null) {
            Text(errorMensaje, style = estilo(12.sp, color = Ocre), textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Valor detectado por OCR", style = estilo(12.sp, color = TintaSuave))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(valorDetectado, style = estilo(12.sp, FontWeight.SemiBold, DigitoTexto, numeros = true))
                Spacer(Modifier.width(3.dp))
                Text(campo.unidad, style = estilo(10.sp, FontWeight.Medium, TintaSuave))
            }
        }
    }
}

@Composable
private fun Coma() {
    Box(Modifier.size(width = 20.dp, height = 56.dp).padding(bottom = 8.dp), contentAlignment = Alignment.BottomCenter) {
        Text(",", style = estilo(28.sp, FontWeight.Bold, DigitoTexto, numeros = true))
    }
}

// Casilla de un dígito; sin dígito es la casilla activa con el cursor.
@Composable
private fun CajaDigito(digito: String?) {
    val forma = RoundedCornerShape(16.dp)
    val activa = digito == null
    Box(
        modifier = Modifier.size(width = 52.dp, height = 56.dp).clip(forma)
            .background(if (activa) Blanco else DigitoFondo)
            .then(if (activa) Modifier.border(2.dp, Cursor, forma) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (digito != null) {
            Text(digito, style = estilo(24.sp, FontWeight.Bold, DigitoTexto, numeros = true))
        } else {
            Box(Modifier.width(2.dp).height(24.dp).clip(RoundedCornerShape(50)).background(Cursor))
        }
    }
}
