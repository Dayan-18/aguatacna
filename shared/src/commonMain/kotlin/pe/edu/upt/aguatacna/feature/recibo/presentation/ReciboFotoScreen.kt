package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.presentation.componentes.*

private val TealBadgeFondo = Color(0xFFDFF4F3)
private val TealBadgeBorde = Color(0xFFCAECEA)
private val TealBadgeTexto = Color(0xFF0A7B83)
private val OcreBadgeFondo = Color(0xFFFFF7ED)
private val OcreBadgeBorde = Color(0xFFFDE0B5)

// Pantalla "Revisa tu recibo": muestra los datos del ReciboBorrador y permite confirmar o corregir.
@Composable
fun ReciboFotoScreen(
    onVolver: () -> Unit,
    onRetomarFoto: () -> Unit = {},
    onCorregirCampo: (TipoCampoEdicion) -> Unit = {},
    viewModel: RevisionViewModel = viewModel { RevisionViewModel.desdeInyeccion() }
) {
    val borrador by viewModel.borrador.collectAsStateWithLifecycle()
    val errorDuplicado by viewModel.errorDuplicado.collectAsStateWithLifecycle()

    LaunchedEffect(borrador) {
        if (borrador == null) {
            val nuevo = ReciboBorrador(
                periodoConsumo = Campo(PeriodoConsumo(2026, 8), confianza = 1f),
                consumoM3 = Campo(null, 1f),
                importeTotal = Campo(null, 1f),
                origen = OrigenDatos.MANUAL
            )
            viewModel.guardarBorrador(nuevo)
        }
    }

    LaunchedEffect(borrador?.periodoConsumo?.valor) {
        viewModel.descartarError()
    }

    IconosClarosEnBarraDeEstado(claros = false)
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F7F7))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            val tieneDudosos = borrador?.tieneCamposDudosos ?: false
            val esManual = borrador?.origen == OrigenDatos.MANUAL

            ReciboBarraSuperior(
                titulo = "Revisa tu recibo",
                subtitulo = "Confirma los datos antes de guardar",
                onVolver = onVolver,
                trailingContent = {
                    when {
                        tieneDudosos -> {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(OcreBadgeFondo)
                                    .border(1.dp, OcreBadgeBorde, RoundedCornerShape(50))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Ocre))
                                Text("Revisa los datos", fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Ocre)
                            }
                        }
                        esManual -> {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Divisor.copy(alpha = 0.5f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TintaSuave))
                                Text("Manual", fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TintaSuave)
                            }
                        }
                        else -> {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(TealBadgeFondo)
                                    .border(1.dp, TealBadgeBorde, RoundedCornerShape(50))
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TealBadgeTexto))
                                Text("Leído", fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealBadgeTexto)
                            }
                        }
                    }
                }
            )

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (errorDuplicado != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEF2E4))
                            .border(1.dp, Color(0xFFF9DFC5), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = Ocre,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = errorDuplicado!!,
                            fontFamily = FuenteTexto,
                            fontSize = 12.sp,
                            color = Color(0xFF7C4D29),
                            lineHeight = 17.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                TarjetaDocumentoRecibo(borrador = borrador)

                ListaCamposRevision(
                    borrador = borrador,
                    onFilaClick = onCorregirCampo
                )

                TipInformativo()

                ZonaRetomarFoto(
                    tieneFoto = borrador != null,
                    onClick = onRetomarFoto
                )
            }
        }

        BotonesFlotantesRevision(
            modifier = Modifier.align(Alignment.BottomCenter),
            habilitadoConfirmar = borrador?.esConfirmable == true,
            onConfirmar = {
                viewModel.confirmar(onCompletado = onVolver)
            }
        )
    }
}

@Composable
private fun BotonesFlotantesRevision(
    modifier: Modifier = Modifier,
    habilitadoConfirmar: Boolean,
    onConfirmar: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF2F7F7).copy(alpha = 0.95f))
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Button(
            onClick = onConfirmar,
            enabled = habilitadoConfirmar,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AguaMedia)
        ) {
            Text("Confirmar datos", fontFamily = FuenteTexto, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}
