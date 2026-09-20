package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aguatacna.shared.generated.resources.Res
import aguatacna.shared.generated.resources.ic_alerta
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.painterResource
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tinta

private val FONDO_AVISO = Color(0xFFFCEFD9)
private val TEXTO_AVISO = Color(0xFF6B3F08)

/** Pantalla 21 del Figma: el sector abasteció y no se registró el llenado. */
@Composable
fun TarjetaConfirmarLlenado(
    horaAsumida: String,
    onConfirmar: () -> Unit,
    onCorregirHora: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(FONDO_AVISO).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(Res.drawable.ic_alerta), contentDescription = null, modifier = Modifier.size(18.dp), tint = Ocre)
            Text("No registraste el llenado", fontFamily = FuenteTexto, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TEXTO_AVISO)
        }
        Text(
            "Asumimos que tu tanque se llenó hoy a las $horaAsumida, el horario de tu sector. Si fue a otra hora, la proyección se corre.",
            fontFamily = FuenteTexto, fontSize = 12.5.sp, lineHeight = 18.sp, color = TEXTO_AVISO
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BotonPrincipal("Sí, se llenó", onConfirmar, Modifier.weight(1f), alto = 46)
            BotonSecundario("Corregir hora", onCorregirHora, Modifier.weight(1f), alto = 46, color = Tinta, borde = Divisor)
        }
    }
}

/** Pide una hora con el selector del sistema; la usan "Corregir hora" y "Se acabó antes". */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoDeHora(
    titulo: String,
    horaInicial: LocalTime,
    onConfirmar: (LocalTime) -> Unit,
    onCancelar: () -> Unit
) {
    val estado = rememberTimePickerState(horaInicial.hour, horaInicial.minute, is24Hour = false)
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo, fontFamily = FuenteTexto) },
        text = { TimePicker(estado) },
        confirmButton = { TextButton({ onConfirmar(LocalTime(estado.hour, estado.minute)) }) { Text("Aceptar", color = AguaMedia) } },
        dismissButton = { TextButton(onCancelar) { Text("Cancelar", color = AguaMedia) } }
    )
}
