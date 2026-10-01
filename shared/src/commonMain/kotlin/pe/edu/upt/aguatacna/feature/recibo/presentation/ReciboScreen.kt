package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.presentation.componentes.*

// Pantalla base del feature Recibo: gestiona el flujo hacia historial, cámara, foto/revisión y lectura manual.
@Composable
fun ReciboScreen(
    resetTrigger: Int = 0,
    viewModel: ReciboViewModel = viewModel { ReciboViewModel.desdeInyeccion() }
) {
    var subPantalla by rememberSaveable { mutableStateOf("principal") }
    var campoEdicion by rememberSaveable { mutableStateOf(TipoCampoEdicion.CONSUMO_M3) }
    // Pantalla a la que se vuelve al salir o confirmar "Revisa tu recibo".
    var origenRevision by rememberSaveable { mutableStateOf("principal") }

    // Si el usuario toca el icono de Recibo en la barra inferior desde una sub-pantalla,
    // vuelve a la pantalla base. Si ya está en la base, no se recarga nada.
    LaunchedEffect(resetTrigger) {
        if (resetTrigger > 0 && subPantalla != "principal") {
            subPantalla = "principal"
        }
    }

    when (subPantalla) {
        "historial" -> ReciboHistorialScreen(
            onVolver = { subPantalla = "principal" },
            onEditarRecibo = {
                origenRevision = "historial"
                subPantalla = "foto"
            }
        )
        "camara" -> CamaraReciboScreen(
            onReciboDetectado = { subPantalla = "foto" },
            onIngresarManual = {
                campoEdicion = TipoCampoEdicion.CONSUMO_M3
                subPantalla = "manualMedidor"
            },
            onVolver = { subPantalla = origenRevision }
        )
        "foto" -> ReciboFotoScreen(
            onVolver = { subPantalla = origenRevision },
            onRetomarFoto = { subPantalla = "camara" },
            onCorregirCampo = { campo ->
                campoEdicion = campo
                subPantalla = "manualMedidor"
            }
        )
        "manualMedidor" -> ReciboManualMedidorScreen(
            campo = campoEdicion,
            onVolver = { subPantalla = "foto" }
        )
        else -> ReciboContenidoPrincipal(
            viewModel = viewModel,
            onVerHistorial = { subPantalla = "historial" },
            onEscanearRecibo = {
                origenRevision = "principal"
                subPantalla = "camara"
            },
            onRevisarLectura = { recibo ->
                origenRevision = "principal"
                viewModel.prepararRevision(recibo)
                subPantalla = "foto"
            },
            onIngresarManual = {
                origenRevision = "principal"
                viewModel.iniciarManual()
                subPantalla = "foto"
            }
        )
    }
}

@Composable
private fun ReciboContenidoPrincipal(
    viewModel: ReciboViewModel,
    onVerHistorial: () -> Unit,
    onEscanearRecibo: () -> Unit,
    onRevisarLectura: (Recibo) -> Unit,
    onIngresarManual: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val subtituloEncabezado = when (val s = uiState) {
        is ReciboUiState.ConDatos -> {
            val medidor = s.recibo.numeroMedidor?.let { "Medidor $it · " } ?: ""
            "${medidor}EPS Tacna · ${s.mes}"
        }
        else -> "EPS Tacna"
    }

    IconosClarosEnBarraDeEstado(claros = false)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoRecibo(subtitulo = subtituloEncabezado)

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val state = uiState) {
                is ReciboUiState.Cargando -> TarjetaEscanear(
                    onEscanearRecibo = onEscanearRecibo,
                    onIngresarManual = onIngresarManual
                )

                is ReciboUiState.SinRecibos -> TarjetaEscanearPrincipal(
                    onEscanearRecibo = onEscanearRecibo,
                    onIngresarManual = onIngresarManual
                )

                is ReciboUiState.ConDatos -> {
                    TarjetaReciboActivo(
                        state = state,
                        estilo = EstiloEstado.desde(state.estadoConsumo),
                        onVerHistorial = onVerHistorial,
                        onRevisarLectura = { onRevisarLectura(state.recibo) }
                    )

                    TarjetaEscanear(
                        onEscanearRecibo = onEscanearRecibo,
                        onIngresarManual = onIngresarManual
                    )

                    SeccionHerramientas(promedioHistorico = state.promedioHistorico)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EncabezadoRecibo(subtitulo: String = "EPS Tacna") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            "Recibo y Facturación",
            fontFamily = FuenteTexto,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Tinta
        )
        Text(
            subtitulo,
            fontFamily = FuenteTexto,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TintaTenue
        )
    }
}
