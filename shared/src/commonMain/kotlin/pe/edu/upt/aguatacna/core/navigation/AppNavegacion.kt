package pe.edu.upt.aguatacna.core.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.feature.recibo.presentation.ReciboScreen
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
                Destino.RECIBO -> ReciboScreen()
                else -> PantallaPendiente(destinoActual)
            }
        }
    }
}

@Composable
private fun BarraInferior(actual: Destino, onSeleccionar: (Destino) -> Unit) {
    Column(Modifier.fillMaxWidth().background(Blanco)) {
        HorizontalDivider(color = Divisor)
        Row(Modifier.navigationBarsPadding().padding(start = 6.dp, end = 6.dp, top = 12.dp, bottom = 10.dp)) {
            Destino.entries.forEach { destino ->
                PestanaInferior(destino, activa = destino == actual, Modifier.weight(1f)) { onSeleccionar(destino) }
            }
        }
    }
}

@Composable
private fun PestanaInferior(destino: Destino, activa: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val color = if (activa) Agua else TintaTenue
    Column(
        modifier = modifier.clickable(onClick = onClick, role = Role.Tab),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(painterResource(destino.icono), contentDescription = null, modifier = Modifier.size(22.dp), tint = color)
        Text(
            destino.etiqueta,
            fontFamily = FuenteTexto,
            fontSize = 9.5.sp,
            fontWeight = if (activa) FontWeight.Bold else FontWeight.Medium,
            color = color
        )
    }
}
