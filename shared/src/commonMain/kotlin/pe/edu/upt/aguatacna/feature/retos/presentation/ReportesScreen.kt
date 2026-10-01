package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.launch
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.feature.retos.domain.model.TipoReporte

@Composable
fun ReportesScreen(
    onVolver: () -> Unit,
    ubicacion: ReporteUbicacionUi,
    onEnviar: (TipoReporte, ByteArray?) -> Unit
) {
    var tipo by rememberSaveable { mutableStateOf(TipoReporte.FUGA) }
    var fotoAdjunta by remember { mutableStateOf<ImageBitmap?>(null) }
    var errorFoto by rememberSaveable { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val galeria = rememberFilePickerLauncher(type = FileKitType.Image) { archivo ->
        if (archivo != null) scope.launch {
            runCatching { archivo.toImageBitmap() }
                .onSuccess { fotoAdjunta = it; errorFoto = null }
                .onFailure { errorFoto = "No se pudo abrir la fotografía." }
        }
    }
    Column(
        Modifier.fillMaxSize().background(Fondo).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
    ) {
        AhorroTopBar("Reportar incidencia", "Con fotografía y ubicación", onVolver)
        ZonaFotoReporte(fotoAdjunta, galeria::launch)
        errorFoto?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
        EtiquetaSeccion("TIPO DE INCIDENCIA", Modifier.padding(top = 18.dp, bottom = 8.dp))
        TiposReporte(tipo, onCambiar = { tipo = it })
        UbicacionReporte(ubicacion)
        AvisoReporte()
        Button(
            onClick = {
                scope.launch {
                    val bytes = fotoAdjunta?.encodeToByteArray(quality = 80)
                    onEnviar(tipo, bytes)
                }
            },
            enabled = ubicacion.coordenada != null && !ubicacion.buscando,
            modifier = Modifier.fillMaxWidth().padding(top = 28.dp, bottom = 18.dp).height(50.dp),
            shape = RoundedCornerShape(13.dp), colors = ButtonDefaults.buttonColors(containerColor = Agua)
        ) { Text("Enviar reporte") }
    }
}

internal fun etiquetaTipo(tipo: TipoReporte): String = when (tipo) {
    TipoReporte.FUGA -> "Fuga en la vía"
    TipoReporte.BAJA_PRESION -> "Rotura de tubería"
    TipoReporte.CORTE_NO_PROGRAMADO -> "Desperdicio"
    TipoReporte.CISTERNA -> "Cisterna"
}
