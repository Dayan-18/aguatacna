package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Coral
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.sector.domain.model.CisternaCercana
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada

// Diálogo a pantalla completa: tapa la barra inferior y el botón "atrás"
// del teléfono lo cierra sin código adicional.
@Composable
fun MapaCompleto(
    casa: Coordenada,
    cisternas: List<CisternaCercana>,
    onVolver: () -> Unit
) {
    Dialog(
        onDismissRequest = onVolver,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false)
    ) {
        Column(Modifier.fillMaxSize().background(Blanco)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onVolver) { Text("← Volver") }
                Text(
                    "Puntos de cisterna",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            MapaCisternas(casa, cisternas, interactivo = true, modifier = Modifier.weight(1f).fillMaxWidth())
            Leyenda(cantidad = cisternas.size)
        }
    }
}

@Composable
private fun Leyenda(cantidad: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Etiqueta("Tu casa", fondo = AguaMedia, colorTexto = Blanco)
        Etiqueta("$cantidad cisternas", fondo = Coral, colorTexto = Blanco)
        Text("Pellizca para acercar", color = TintaSuave, style = MaterialTheme.typography.bodySmall)
    }
}
