package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.util.EstadoPermisoCamara
import pe.edu.upt.aguatacna.core.util.rememberCapturaFoto
import pe.edu.upt.aguatacna.feature.recibo.presentation.componentes.PantallaAbriendoCamara
import pe.edu.upt.aguatacna.feature.recibo.presentation.componentes.PantallaErrorLectura
import pe.edu.upt.aguatacna.feature.recibo.presentation.componentes.PantallaPermisoDenegado
import pe.edu.upt.aguatacna.feature.recibo.presentation.componentes.PantallaPermisoDenegadoPermanente
import pe.edu.upt.aguatacna.feature.recibo.presentation.componentes.PantallaProcesandoOcr

// Abre la cámara del sistema y pasa directo a procesamiento OCR, sin doble confirmación.
@Composable
fun CamaraReciboScreen(
    onReciboDetectado: () -> Unit,
    onIngresarManual: () -> Unit,
    onVolver: () -> Unit,
    viewModel: CapturaViewModel = viewModel { CapturaViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Si el usuario cancela la cámara o no se pudo abrir, se vuelve sin mostrar error.
    val capturaFoto = rememberCapturaFoto(onFotoCapturada = viewModel::onFotoCapturada, onError = { onVolver() })

    // Al entrar se limpia cualquier error anterior y se abre la cámara.
    LaunchedEffect(Unit) {
        viewModel.reiniciar()
        capturaFoto.tomarFoto()
    }
    LaunchedEffect(uiState) {
        if (uiState is CapturaUiState.Exito) onReciboDetectado()
    }

    when (val state = uiState) {
        is CapturaUiState.Inactivo -> when (capturaFoto.estado) {
            EstadoPermisoCamara.DenegadoPermanente ->
                PantallaPermisoDenegadoPermanente(onAbrirAjustes = capturaFoto.abrirAjustes, onVolver = onVolver)
            EstadoPermisoCamara.Denegado ->
                PantallaPermisoDenegado(onReintentar = capturaFoto.tomarFoto, onVolver = onVolver)
            else -> PantallaAbriendoCamara()
        }
        is CapturaUiState.Procesando -> PantallaProcesandoOcr()
        is CapturaUiState.Error -> PantallaErrorLectura(
            mensaje = state.mensaje,
            onReintentar = {
                viewModel.reiniciar()
                capturaFoto.tomarFoto()
            },
            onIngresarManual = {
                viewModel.iniciarManual()
                onIngresarManual()
            },
            onVolver = onVolver
        )
        is CapturaUiState.Exito -> Unit
    }
}
