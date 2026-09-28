package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RetosContenido(estado: RetosUiState, completar: (String) -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Retos de esta semana", style = MaterialTheme.typography.headlineSmall)
        Text("Racha actual: ${estado.racha.dias} días")
        estado.mensaje?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        estado.retos.forEach { reto -> TarjetaReto(reto, reto.id in estado.cumplidos, completar) }
    }
}

@Composable
private fun TarjetaReto(reto: pe.edu.upt.aguatacna.feature.retos.domain.model.Reto, cumplido: Boolean, completar: (String) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(reto.titulo, style = MaterialTheme.typography.titleMedium)
            Text(reto.descripcion)
            Text("Ahorro estimado: ${reto.litrosMeta} L")
            if (cumplido) Text("Completado") else Button(onClick = { completar(reto.id) }) { Text("Marcar como cumplido") }
        }
    }
}
