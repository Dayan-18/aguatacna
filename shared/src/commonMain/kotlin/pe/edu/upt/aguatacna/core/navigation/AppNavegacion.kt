package pe.edu.upt.aguatacna.core.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upt.aguatacna.feature.reserva.presentation.ReservaScreen
import pe.edu.upt.aguatacna.feature.sector.presentation.SectorScreen

// Navegación provisional por pestañas, sin librería de navegación.
// Pendiente de revisión del custodio del core (Cristhian).
@Composable
fun AppNavegacion() {
    var destinoActual by rememberSaveable { mutableStateOf(Destino.RESERVA) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            BarraInferior(destinoActual, onSeleccionar = { destinoActual = it })
        }
    ) { espacio ->
        Box(Modifier.fillMaxSize().padding(espacio)) {
            when (destinoActual) {
                Destino.RESERVA -> ReservaScreen(onVerCisternas = { destinoActual = Destino.SECTOR })
                Destino.SECTOR -> SectorScreen()
                else -> PantallaPendiente(destinoActual)
            }
        }
    }
}
