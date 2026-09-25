package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave
import pe.edu.upt.aguatacna.core.util.RelojDelSistema
import pe.edu.upt.aguatacna.feature.recibo.data.BorradorReciboStore
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.model.aBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.presentation.componentes.*

// Pantalla base del feature Recibo: gestiona el flujo hacia historial, cámara, foto/revisión y lectura manual.
@Composable
fun ReciboScreen(
    resetTrigger: Int = 0
) {
    var subPantalla by rememberSaveable { mutableStateOf("principal") }
    var campoEdicion by rememberSaveable { mutableStateOf(TipoCampoEdicion.CONSUMO_M3) }
    val borradorStore: BorradorReciboStore = remember { KoinPlatform.getKoin().get() }
    val reciboRepo: ReciboRepository = remember { KoinPlatform.getKoin().get() }
    val coroutineScope = rememberCoroutineScope()

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
            onModificarRecibo = { periodo ->
                coroutineScope.launch {
                    val recibo = reciboRepo.obtenerPorPeriodo(periodo)
                    if (recibo != null) {
                        borradorStore.guardar(recibo.aBorrador())
                    } else {
                        borradorStore.guardar(
                            ReciboBorrador(
                                periodoConsumo = Campo(periodo, 1f),
                                consumoM3 = Campo(null, 1f),
                                importeTotal = Campo(null, 1f),
                                origen = OrigenDatos.MANUAL
                            )
                        )
                    }
                    subPantalla = "foto"
                }
            }
        )
        "camara" -> CamaraReciboScreen(
            onReciboDetectado = { subPantalla = "foto" },
            onIngresarManual = {
                campoEdicion = TipoCampoEdicion.CONSUMO_M3
                subPantalla = "manualMedidor"
            },
            onVolver = { subPantalla = "principal" }
        )
        "foto" -> ReciboFotoScreen(
            onVolver = { subPantalla = "principal" },
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
            onVerHistorial = { subPantalla = "historial" },
            onEscanearRecibo = { subPantalla = "camara" },
            onRevisarLectura = { reciboOriginal ->
                if (reciboOriginal != null) {
                    borradorStore.guardar(reciboOriginal.aBorrador())
                }
                subPantalla = "foto"
            },
            onIngresarManual = {
                val ahora = RelojDelSistema().ahora()
                val periodoActual = PeriodoConsumo(ahora.year, ahora.monthNumber)
                borradorStore.guardar(
                    ReciboBorrador(
                        periodoConsumo = Campo(periodoActual, confianza = 1f, corregidoPorUsuario = false),
                        consumoM3 = Campo(null, 1f, corregidoPorUsuario = false),
                        importeTotal = Campo(null, 1f, corregidoPorUsuario = false),
                        origen = OrigenDatos.MANUAL
                    )
                )
                subPantalla = "foto"
            }
        )
    }
}

@Composable
private fun ReciboContenidoPrincipal(
    onVerHistorial: () -> Unit,
    onEscanearRecibo: () -> Unit,
    onRevisarLectura: (Recibo?) -> Unit,
    onIngresarManual: () -> Unit = {},
    viewModel: ReciboViewModel = viewModel { ReciboViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val subtituloEncabezado = when (val s = uiState) {
        is ReciboUiState.ConDatos -> {
            val medidor = s.reciboOriginal?.numeroMedidor?.let { "Medidor $it · " } ?: ""
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
                is ReciboUiState.Cargando -> {
                    TarjetaEscanear(
                        onEscanearRecibo = onEscanearRecibo,
                        onIngresarManual = onIngresarManual
                    )
                }

                is ReciboUiState.SinRecibos -> {
                    TarjetaEscanearPrincipal(
                        onEscanearRecibo = onEscanearRecibo,
                        onIngresarManual = onIngresarManual
                    )
                }

                is ReciboUiState.ConDatos -> {
                    val estilo = EstiloEstado.desde(state.estadoConsumo)

                    TarjetaReciboActivo(
                        state = state,
                        estilo = estilo,
                        onVerHistorial = onVerHistorial,
                        onRevisarLectura = { onRevisarLectura(state.reciboOriginal) }
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
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
        Box(
            modifier = Modifier
                .size(40.dp)
                .sombraSuave(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Blanco)
                .border(1.dp, Divisor, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Outlined.HelpOutline,
                contentDescription = "Ayuda",
                modifier = Modifier.size(20.dp),
                tint = TintaSuave
            )
        }
    }
}
