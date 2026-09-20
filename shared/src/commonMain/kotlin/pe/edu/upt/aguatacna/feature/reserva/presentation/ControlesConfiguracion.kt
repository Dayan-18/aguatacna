package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoReservorio

private val OPCIONES_DE_TIPO = listOf(
    TipoReservorio.TANQUE_ELEVADO to "Tanque elevado",
    TipoReservorio.CISTERNA to "Cisterna",
    TipoReservorio.BIDONES to "Bidones"
)

@Composable
fun SeccionConfiguracion(titulo: String, modifier: Modifier = Modifier, contenido: @Composable () -> Unit) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(titulo.uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TintaSuave)
        contenido()
    }
}

@Composable
fun SelectorDeTipo(seleccionado: TipoReservorio, onElegir: (TipoReservorio) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OPCIONES_DE_TIPO.forEach { (tipo, etiqueta) ->
            val activo = tipo == seleccionado
            OutlinedButton(
                onClick = { onElegir(tipo) },
                modifier = Modifier.weight(1f).height(64.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(if (activo) 2.dp else 1.dp, if (activo) AguaMedia else Tenue),
                contentPadding = PaddingValues(4.dp)
            ) {
                Text(etiqueta, fontWeight = if (activo) FontWeight.Bold else FontWeight.Medium, color = if (activo) AguaMedia else Tinta)
            }
        }
    }
}

@Composable
fun ControlDeCapacidad(litros: Int, onCambiar: (Int) -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Blanco).padding(16.dp)) {
        Text("${formatearMiles(litros)} L", style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, color = AguaMedia)
        val pasos = (CAPACIDAD_MAXIMA_LITROS - CAPACIDAD_MINIMA_LITROS) / PASO_CAPACIDAD_LITROS - 1
        Slider(
            value = litros.toFloat(),
            onValueChange = { onCambiar(it.toInt()) },
            valueRange = CAPACIDAD_MINIMA_LITROS.toFloat()..CAPACIDAD_MAXIMA_LITROS.toFloat(),
            steps = pasos
        )
        Text("típico 1 000 – 2 500 L", style = MaterialTheme.typography.bodySmall, color = TintaSuave)
    }
}

@Composable
fun ControlDeCantidad(valor: Int, onCambiar: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
        BotonRedondo("−") { onCambiar(-1) }
        Text("$valor", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Tinta)
        BotonRedondo("+") { onCambiar(1) }
    }
}

@Composable
private fun BotonRedondo(simbolo: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(44.dp),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Tenue),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Text(simbolo, style = MaterialTheme.typography.titleLarge, color = AguaMedia)
    }
}

@Composable
fun FilaConInterruptor(etiqueta: String, activo: Boolean, onAlternar: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(etiqueta, style = MaterialTheme.typography.bodyLarge, color = Tinta)
        Switch(checked = activo, onCheckedChange = { onAlternar() })
    }
}
