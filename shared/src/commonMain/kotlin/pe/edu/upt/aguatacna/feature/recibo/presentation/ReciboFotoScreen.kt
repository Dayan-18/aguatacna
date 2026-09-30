package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.presentation.componentes.*

private val FondoRevision = Color(0xFFF2F7F7)

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
    val advertencias by viewModel.advertencias.collectAsStateWithLifecycle()

    LaunchedEffect(borrador?.periodoConsumo?.valor) {
        viewModel.descartarError()
    }

    IconosClarosEnBarraDeEstado(claros = false)
    Box(modifier = Modifier.fillMaxSize().background(FondoRevision)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            ReciboBarraSuperior(
                titulo = "Revisa tu recibo",
                subtitulo = "Confirma los datos antes de guardar",
                onVolver = onVolver,
                trailingContent = { ChipEstadoRevision(borrador) }
            )

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                errorDuplicado?.let { AvisoRevision(it) }
                advertencias.forEach { AvisoRevision(it) }

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
            onConfirmar = { viewModel.confirmar(onCompletado = onVolver) }
        )
    }
}

@Composable
private fun ChipEstadoRevision(borrador: ReciboBorrador?) {
    when {
        borrador?.tieneCamposDudosos == true ->
            ChipRevision("Revisa los datos", Ocre, Color(0xFFFFF7ED), Color(0xFFFDE0B5))
        borrador?.origen == OrigenDatos.MANUAL ->
            ChipRevision("Manual", TintaSuave, Divisor.copy(alpha = 0.5f), null)
        else ->
            ChipRevision("Leído", Color(0xFF0A7B83), Color(0xFFDFF4F3), Color(0xFFCAECEA))
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
            .background(FondoRevision.copy(alpha = 0.95f))
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
