package pe.edu.upt.aguatacna.feature.bienvenida.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/** Muestra la bienvenida hasta que la persona elige cómo entrar; después deja pasar a la app. */
@Composable
fun PuertaDeAcceso(
    viewModel: AccesoViewModel = viewModel { AccesoViewModel.desdeInyeccion() },
    contenido: @Composable () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when {
        uiState.cargando -> Unit
        uiState.modo == null -> BienvenidaScreen(
            uiState = uiState,
            onAlternarConsentimiento = viewModel::alternarConsentimiento,
            onSinCuenta = viewModel::empezarSinCuenta,
            onGoogle = viewModel::entrarConGoogle,
            onDescartarMensaje = viewModel::descartarMensaje
        )
        else -> contenido()
    }
}
