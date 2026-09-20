package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RetosScreen(viewModel: RetosViewModel = viewModel { RetosViewModel.conDatosDePrueba() }) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    if (estado.cargando) {
        CircularProgressIndicator()
    } else {
        RetosContenido(estado, viewModel::completar)
    }
}

@Composable
fun RetosContenido(estado: RetosUiState, completar: (String) -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Retos de esta semana", style = MaterialTheme.typography.headlineSmall)
        Text("Racha actual: ${estado.racha.dias} días")
        estado.mensaje?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        estado.retos.forEach { reto ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(reto.titulo, style = MaterialTheme.typography.titleMedium)
                    Text(reto.descripcion)
                    Text("Ahorro estimado: ${reto.litrosMeta} L")
                    if (reto.id in estado.cumplidos) Text("Completado")
                    else Button(onClick = { completar(reto.id) }) { Text("Marcar como cumplido") }
                }
            }
        }
    }
}
