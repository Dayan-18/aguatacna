package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pe.edu.upt.aguatacna.core.ui.theme.Fondo

@Composable
fun PuntosCisternaScreen(onVolver: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize().background(Fondo).verticalScroll(rememberScrollState())
    ) {
        BarraSuperiorSector(
            titulo = "Puntos de cisterna",
            subtitulo = "Puntos cerca de tu domicilio",
            onVolver = onVolver
        )
    }
}
