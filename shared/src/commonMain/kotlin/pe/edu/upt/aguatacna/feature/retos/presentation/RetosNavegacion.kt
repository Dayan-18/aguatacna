package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class SeccionRetos(val etiqueta: String) {
    RETOS("Retos"), COMUNIDAD("Comunidad"), REPORTAR("Reportar")
}

@Composable
fun RetosNavegacion(actual: SeccionRetos, onSeleccionar: (SeccionRetos) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SeccionRetos.entries.forEach { seccion ->
            FilterChip(
                selected = actual == seccion,
                onClick = { onSeleccionar(seccion) },
                label = { Text(seccion.etiqueta) }
            )
        }
    }
}
