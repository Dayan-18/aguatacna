package pe.edu.upt.aguatacna.feature.bienvenida.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * El primer uso, en orden: la bienvenida hasta que la persona elige cómo entrar, el registro del
 * domicilio hasta que tiene un sector, y después la app.
 */
@Composable
fun PuertaDeAcceso(
    viewModel: AccesoViewModel = viewModel { AccesoViewModel.desdeInyeccion() },
    registrarDomicilio: @Composable () -> Unit,
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
        !uiState.tieneDomicilio -> registrarDomicilio()
        else -> contenido()
    }
}
