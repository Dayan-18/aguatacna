package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.core.util.RelojDelSistema
import pe.edu.upt.aguatacna.feature.recibo.data.BorradorReciboStore
import pe.edu.upt.aguatacna.feature.recibo.domain.model.aBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave
import pe.edu.upt.aguatacna.feature.recibo.presentation.captura.CamaraReciboScreen
import pe.edu.upt.aguatacna.feature.recibo.presentation.comun.EstiloEstado

// Colores específicos del diseño (ya existían, no se crean nuevos — T-0.2)
private val RojoFondo = Color(0xFFFEF2F2)
private val Rojo = Color(0xFFDC2626)
private val TealClaro = Color(0xFFE4F3F4)
private val TealBorde = Color(0x33087E8B)
private val InfoFondo = Color(0xCCDCF0F2) // 80% opacidad
private val InfoBorde = Color(0x33087E8B)

/**
 * Pantalla principal del feature Recibo.
 * Actúa como pantalla base y gestiona el flujo hacia las sub-pantallas
 * (historial, cámara, foto/revisión y lectura manual de medidor).
 */
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
                            pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador(
                                periodoConsumo = pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo(periodo, 1f),
                                consumoM3 = pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo(null, 1f),
                                importeTotal = pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo(null, 1f),
                                origen = pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos.MANUAL
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
        // T-3.7, T-9.1: Pantalla parametrizada con el campo editable
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
                val periodoActual = pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo(ahora.year, ahora.monthNumber)
                borradorStore.guardar(
                    pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador(
                        periodoConsumo = pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo(
                            periodoActual,
                            confianza = 1f,
                            corregidoPorUsuario = false
                        ),
                        consumoM3 = pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo(null, 1f, corregidoPorUsuario = false),
                        importeTotal = pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo(null, 1f, corregidoPorUsuario = false),
                        origen = pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos.MANUAL
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
    onRevisarLectura: (pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo?) -> Unit,
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
        // ── Header (sin flecha de volver) ──
        EncabezadoRecibo(subtitulo = subtituloEncabezado)

        // ── Contenido scrolleable ──
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val state = uiState) {
                is ReciboUiState.Cargando -> {
                    // Mientras carga, mostrar solo la tarjeta de escaneo
                    TarjetaEscanear(onEscanearRecibo = onEscanearRecibo, onIngresarManual = onIngresarManual)
                }

                is ReciboUiState.SinRecibos -> {
                    // Sin recibos: escaneo principal + opción de ingreso a mano
                    TarjetaEscanearPrincipal(
                        onEscanearRecibo = onEscanearRecibo,
                        onIngresarManual = onIngresarManual
                    )
                }

                is ReciboUiState.ConDatos -> {
                    val estilo = EstiloEstado.desde(state.estadoConsumo)

                    // ── Tarjeta Recibo Activo (Hero Card) con datos reales ──
                    TarjetaReciboActivo(
                        state = state,
                        estilo = estilo,
                        onVerHistorial = onVerHistorial,
                        onRevisarLectura = { onRevisarLectura(state.reciboOriginal) }
                    )

                    // ── Escanear nuevo recibo o ingresar otro recibo a mano ──
                    TarjetaEscanear(
                        onEscanearRecibo = onEscanearRecibo,
                        onIngresarManual = onIngresarManual
                    )

                    // ── Herramientas y Reportes ──
                    SeccionHerramientas(promedioHistorico = state.promedioHistorico)

                    // ── Banner informativo Sunass ──
                    BannerSunass()
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Header (sin flecha de volver según requerimiento)
// ──────────────────────────────────────────────────────────────────────

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
        // Botón ayuda con icono vector
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

// ──────────────────────────────────────────────────────────────────────
// Tarjeta Recibo Activo (Hero Card) — T-3.3: datos reales
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun TarjetaReciboActivo(
    state: ReciboUiState.ConDatos,
    estilo: EstiloEstado,
    onVerHistorial: () -> Unit,
    onRevisarLectura: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .sombraSuave(26.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Blanco)
            .border(1.dp, Divisor.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
            .padding(20.dp)
    ) {
        // Fila superior: Mes + badge estado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Ícono documento limpio acorde al diseño
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TealClaro),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ReceiptLong,
                        contentDescription = null,
                        tint = AguaMedia,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        "RECIBO ACTIVO",
                        fontFamily = FuenteTexto,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TintaTenue,
                        letterSpacing = 1.sp
                    )
                    Text(
                        state.mes, // ← dato real (T-3.3)
                        fontFamily = FuenteTexto,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Tinta
                    )
                }
            }
            // Badge estado dinámico (T-3.4)
            BadgeEstado(estilo)
        }

        Spacer(Modifier.height(12.dp))

        // Precio y vencimiento — datos reales (T-3.3)
        HorizontalDivider(color = Divisor)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text("Total a pagar", fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TintaTenue)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("S/", fontFamily = FuenteNumeros, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TintaSuave)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        state.importeDisplay, // ← dato real
                        fontFamily = FuenteNumeros,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Tinta
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Vencimiento", fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TintaTenue)
                Spacer(Modifier.height(4.dp))
                Text(
                    state.fechaVencimiento, // ← dato real
                    fontFamily = FuenteTexto,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Rojo,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RojoFondo)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
        HorizontalDivider(color = Divisor)

        Spacer(Modifier.height(10.dp))

        // Métricas: Consumo y Variación — datos reales (T-3.3) con colores dinámicos (T-3.4)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Consumo facturado
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Fondo)
                    .border(1.dp, Divisor, RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Text("Consumo facturado", fontFamily = FuenteTexto, fontSize = 11.sp, color = TintaSuave)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        state.consumoM3.toString(), // ← dato real
                        fontFamily = FuenteNumeros,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Tinta
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("m³", fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
                }
            }
            // Variación Sunass — color dinámico (T-3.4)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(estilo.colorFondo)
                    .border(1.dp, estilo.colorBorde, RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Text("Variación Sunass", fontFamily = FuenteTexto, fontSize = 11.sp, color = estilo.variacionColor)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        state.variacionTexto, // ← dato real
                        fontFamily = FuenteNumeros,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = estilo.variacionColor
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("vs prom.", fontFamily = FuenteTexto, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = estilo.variacionColor.copy(alpha = 0.7f))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // T-3.5: Botón principal dinámico — naranja si atípico, teal si normal
        Button(
            onClick = onVerHistorial,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = estilo.botonHistorialColor)
        ) {
            if (state.esAtipico) {
                Icon(Icons.Outlined.ErrorOutline, contentDescription = null, modifier = Modifier.size(18.dp), tint = Blanco)
                Spacer(Modifier.width(8.dp))
            }
            Text(
                estilo.botonHistorialTexto,
                fontFamily = FuenteTexto,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.height(8.dp))

        // T-3.8: Botón "Revisar lectura y datos detectados" — visible solo con recibo
        OutlinedButton(
            onClick = onRevisarLectura,
            modifier = Modifier.fillMaxWidth().height(40.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Outlined.Visibility, contentDescription = null, modifier = Modifier.size(16.dp), tint = AguaMedia)
            Spacer(Modifier.width(6.dp))
            Text(
                "Revisar lectura y datos detectados",
                fontFamily = FuenteTexto,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = TintaSuave
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Estado vacío: Tarjeta principal de escaneo (T-3.2)
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun TarjetaEscanearPrincipal(
    onEscanearRecibo: () -> Unit,
    onIngresarManual: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .sombraSuave(24.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Blanco)
            .border(1.dp, Divisor.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Ícono grande de cámara
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(TealClaro)
                .border(1.dp, TealBorde, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.PhotoCamera,
                contentDescription = null,
                tint = AguaMedia,
                modifier = Modifier.size(36.dp)
            )
        }

        Text(
            "Escanea tu recibo",
            fontFamily = FuenteTexto,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Tinta
        )

        Text(
            "Toma una foto de tu recibo de EPS Tacna y la app leerá los datos automáticamente.",
            fontFamily = FuenteTexto,
            fontSize = 13.sp,
            color = TintaSuave,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(Modifier.height(4.dp))

        Button(
            onClick = onEscanearRecibo,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AguaMedia)
        ) {
            Icon(Icons.Outlined.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp), tint = Blanco)
            Spacer(Modifier.width(8.dp))
            Text(
                "Tomar foto del recibo",
                fontFamily = FuenteTexto,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // T-9.6: Opción de entrada manual sin cámara
        Text(
            text = "o ingresa los datos a mano",
            fontFamily = FuenteTexto,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = AguaMedia,
            modifier = Modifier
                .clickable(onClick = onIngresarManual)
                .padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
        )
    }
}

// ──────────────────────────────────────────────────────────────────────
// Escanear nuevo recibo — T-3.6: botón "+" reducido
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun TarjetaEscanear(
    onEscanearRecibo: () -> Unit,
    onIngresarManual: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .sombraSuave(24.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Blanco)
            .border(1.dp, Divisor.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícono cámara
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(TealClaro)
                .border(1.dp, TealBorde, RoundedCornerShape(16.dp))
                .clickable(onClick = onEscanearRecibo),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.PhotoCamera,
                contentDescription = null,
                tint = AguaMedia,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Escanear nuevo recibo",
                fontFamily = FuenteTexto,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Tinta
            )
            Text(
                "Sube o toma una foto para digitalizar.",
                fontFamily = FuenteTexto,
                fontSize = 11.sp,
                color = TintaSuave,
                maxLines = 1
            )
            Text(
                text = "o ingresar otro recibo a mano",
                fontFamily = FuenteTexto,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = AguaMedia,
                modifier = Modifier
                    .clickable(onClick = onIngresarManual)
                    .padding(top = 2.dp)
            )
        }

        Spacer(Modifier.width(8.dp))

        // Botón "+" reducido
        IconButton(
            onClick = onEscanearRecibo,
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AguaMedia),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Escanear", tint = Blanco, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Badge estado dinámico (T-3.4)
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun BadgeEstado(estilo: EstiloEstado) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(estilo.chipColorFondo)
            .border(1.dp, estilo.chipColorBorde, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(estilo.chipColorTexto))
        Text(estilo.chipTexto, fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = estilo.chipColorTexto)
    }
}

// ──────────────────────────────────────────────────────────────────────
// Sección Herramientas y Reportes — dato de promedio real (T-3.3)
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun SeccionHerramientas(promedioHistorico: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "HERRAMIENTAS Y REPORTES",
                fontFamily = FuenteTexto,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TintaTenue,
                letterSpacing = 1.sp
            )
            Text(
                "EPS Tacna",
                fontFamily = FuenteTexto,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = AguaMedia
            )
        }

        // 1. Histórico de 6 meses — dato real de promedio
        TarjetaHerramienta(
            iconoVector = Icons.Outlined.BarChart,
            iconoColor = AguaMedia,
            iconoFondo = Color(0xFFF0F8F8),
            titulo = "Histórico de 6 meses",
            subtitulo = "Promedio regular: $promedioHistorico m³", // ← dato real (T-3.3)
            trailingContent = {
                // Mini barras
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    val alturas = listOf(14.dp, 12.dp, 14.dp, 16.dp, 16.dp)
                    alturas.forEach { h ->
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(h)
                                .clip(RoundedCornerShape(50))
                                .background(AguaMedia.copy(alpha = 0.7f))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(28.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Ocre)
                    )
                }
            }
        )

        // 2. Tus reclamos Sunass
        TarjetaHerramienta(
            iconoVector = Icons.Outlined.Shield,
            iconoColor = Ocre,
            iconoFondo = Color(0xFFFFF7ED),
            titulo = "Tus reclamos Sunass",
            subtitulo = "Paso 1: Inspección técnica domiciliaria",
            badgeTexto = "1 ACTIVO",
            trailingContent = {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TintaTenue,
                    modifier = Modifier.size(20.dp)
                )
            }
        )

        // 3. Descargar recibo oficial
        TarjetaHerramienta(
            iconoVector = Icons.Outlined.FileDownload,
            iconoColor = TintaSuave,
            iconoFondo = Color(0xFFF1F5F9),
            titulo = "Descargar recibo oficial",
            subtitulo = "PDF con validez legal EPS Tacna",
            trailingContent = {
                Text(
                    "PDF 420 KB",
                    fontFamily = FuenteTexto,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AguaMedia,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TealClaro)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        )
    }
}

@Composable
private fun TarjetaHerramienta(
    iconoVector: androidx.compose.ui.graphics.vector.ImageVector,
    iconoColor: Color,
    iconoFondo: Color,
    titulo: String,
    subtitulo: String,
    badgeTexto: String? = null,
    trailingContent: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .sombraSuave(22.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Blanco)
            .border(1.dp, Divisor.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconoFondo),
                contentAlignment = Alignment.Center
            ) {
                Icon(iconoVector, contentDescription = null, tint = iconoColor, modifier = Modifier.size(20.dp))
            }
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(titulo, fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Tinta)
                    if (badgeTexto != null) {
                        Text(
                            badgeTexto,
                            fontFamily = FuenteTexto,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Ocre,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Ocre.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(subtitulo, fontFamily = FuenteTexto, fontSize = 11.sp, color = TintaSuave)
            }
        }
        trailingContent()
    }
}

// ──────────────────────────────────────────────────────────────────────
// Banner informativo Sunass
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun BannerSunass() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(InfoFondo)
            .border(1.dp, InfoBorde, RoundedCornerShape(20.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Ícono info
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(AguaMedia.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Info,
                contentDescription = null,
                tint = AguaMedia,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            buildString {
                append("Norma Sunass: ")
                append("Si tu consumo supera el 100% del promedio histórico, la EPS Tacna debe inspeccionar tu predio antes de cualquier corte.")
            },
            fontFamily = FuenteTexto,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TintaSuave,
            lineHeight = 16.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
