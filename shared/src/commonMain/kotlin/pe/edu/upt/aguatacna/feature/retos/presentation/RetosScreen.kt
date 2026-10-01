package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RetosScreen(
    viewModel: RetosViewModel = viewModel { RetosViewModel.desdeInyeccion() },
    reportesViewModel: ReportesViewModel = viewModel { ReportesViewModel.desdeInyeccion() }
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val reportes by reportesViewModel.reportes.collectAsStateWithLifecycle()
    var seccion by rememberSaveable { mutableStateOf(SeccionRetos.RESUMEN) }
    if (estado.cargando) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else {
        when (seccion) {
            SeccionRetos.RESUMEN -> RetosContenido(
                estado = estado,
                completar = viewModel::alternarReto,
                onVerPosicion = { seccion = SeccionRetos.SECTOR }
            )
            SeccionRetos.SECTOR -> ComunidadScreen(
                posicion = estado.posicionSector,
                onVolver = { seccion = SeccionRetos.RESUMEN },
                onVerIncidencias = { seccion = SeccionRetos.INCIDENCIAS },
                onReportar = { seccion = SeccionRetos.REPORTAR }
            )
            SeccionRetos.REPORTAR -> ReportesScreen(
                onVolver = { seccion = SeccionRetos.SECTOR },
                onEnviar = { tipo, foto ->
                    reportesViewModel.guardar(tipo, foto) { seccion = SeccionRetos.INCIDENCIAS }
                }
            )
            SeccionRetos.INCIDENCIAS -> IncidenciasScreen(
                reportes = reportes,
                onVolver = { seccion = SeccionRetos.SECTOR },
                onReportar = { seccion = SeccionRetos.REPORTAR }
            )
        }
    }
}
