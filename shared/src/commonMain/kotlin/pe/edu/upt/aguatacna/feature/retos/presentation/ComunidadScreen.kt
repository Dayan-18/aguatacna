package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upt.aguatacna.feature.retos.domain.model.PosicionSector

@Composable
fun ComunidadScreen(posicion: PosicionSector?) {
    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Tu comunidad", style = MaterialTheme.typography.headlineSmall)
        Text("Comparación anónima con hogares de tu sector.")
        if (posicion == null) {
            Text("Aún no hay datos suficientes del sector.")
        } else {
            ResumenConsumo(posicion)
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Privacidad", style = MaterialTheme.typography.titleMedium)
                Text("Solo se muestran promedios del sector; no se comparten nombres ni direcciones.")
            }
        }
    }
}

@Composable
private fun ResumenConsumo(posicion: PosicionSector) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Consumo del hogar: ${posicion.consumoHogar.valor.toInt()} L/persona/día")
            Text("Promedio del sector: ${posicion.promedioSector.valor.toInt()} L/persona/día")
            val texto = if (posicion.ahorraMasQueElPromedio) "Vas por debajo del promedio. ¡Buen trabajo!" else "Estás sobre el promedio; completa retos para reducirlo."
            Text(texto, color = MaterialTheme.colorScheme.primary)
        }
    }
}
