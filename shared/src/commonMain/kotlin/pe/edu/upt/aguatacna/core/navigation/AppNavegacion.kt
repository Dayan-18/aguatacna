package pe.edu.upt.aguatacna.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import pe.edu.upt.aguatacna.feature.sector.presentation.SectorScreen

// Navegación provisional por pestañas, sin librería de navegación.
// Pendiente de revisión del custodio del core (Cristhian).
@Composable
fun AppNavegacion() {
    var destinoActual by rememberSaveable { mutableStateOf(Destino.RESERVA) }

    Scaffold(
        bottomBar = {
            BarraInferior(destinoActual, onSeleccionar = { destinoActual = it })
        }
    ) { espacio ->
        Box(Modifier.fillMaxSize().padding(espacio)) {
            when (destinoActual) {
                Destino.SECTOR -> SectorScreen()
                else -> PantallaPendiente(destinoActual)
            }
        }
    }
}

@Composable
private fun BarraInferior(actual: Destino, onSeleccionar: (Destino) -> Unit) {
    NavigationBar {
        Destino.entries.forEach { destino ->
            NavigationBarItem(
                selected = destino == actual,
                onClick = { onSeleccionar(destino) },
                icon = { Icon(painterResource(destino.icono), contentDescription = destino.etiqueta) },
                label = { Text(destino.etiqueta) }
            )
        }
    }
}
