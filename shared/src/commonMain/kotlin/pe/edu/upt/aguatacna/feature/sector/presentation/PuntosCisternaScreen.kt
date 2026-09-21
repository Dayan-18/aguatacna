package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.ui.theme.Fondo

@Composable
fun PuntosCisternaScreen(
    onVolver: () -> Unit = {},
    viewModel: PuntosCisternaViewModel = viewModel { PuntosCisternaViewModel.conDatosDePrueba() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val margen = Modifier.padding(horizontal = 20.dp)
    Column(
        modifier = Modifier.fillMaxSize().background(Fondo).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BarraSuperiorSector(
            titulo = "Puntos de cisterna",
            subtitulo = subtituloDe(uiState.cisternas.size),
            onVolver = onVolver
        )
        uiState.cisternas.forEach { cisterna -> TarjetaCisterna(cisterna, margen) }
        Spacer(Modifier.height(24.dp))
    }
}

private fun subtituloDe(cantidad: Int): String = when (cantidad) {
    0 -> "Sin puntos cerca de tu domicilio"
    1 -> "1 punto cerca de tu domicilio"
    else -> "$cantidad puntos cerca de tu domicilio"
}
