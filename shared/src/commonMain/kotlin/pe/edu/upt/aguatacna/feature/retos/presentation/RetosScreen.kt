package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RetosScreen(viewModel: RetosViewModel = viewModel { RetosViewModel.conDatosDePrueba() }) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var seccion by rememberSaveable { mutableStateOf(SeccionRetos.RETOS) }
    if (estado.cargando) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else {
        Column(Modifier.fillMaxSize()) {
            RetosNavegacion(seccion, onSeleccionar = { seccion = it })
            Box(Modifier.weight(1f)) {
                when (seccion) {
                    SeccionRetos.RETOS -> RetosContenido(estado, viewModel::completar)
                    SeccionRetos.COMUNIDAD -> ComunidadScreen(estado.posicionSector)
                    SeccionRetos.REPORTAR -> ReportesScreen()
                }
            }
        }
    }
}
