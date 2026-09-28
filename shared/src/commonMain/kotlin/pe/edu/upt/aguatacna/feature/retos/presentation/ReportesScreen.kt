package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import pe.edu.upt.aguatacna.core.util.rememberCapturaFoto
import pe.edu.upt.aguatacna.feature.retos.data.FakeReporteRepository
import pe.edu.upt.aguatacna.feature.retos.domain.model.Reporte
import pe.edu.upt.aguatacna.feature.retos.domain.model.TipoReporte
import pe.edu.upt.aguatacna.feature.retos.domain.usecase.CrearReporte
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher

@Composable
fun ReportesScreen() {
    var tipo by rememberSaveable { mutableStateOf(TipoReporte.CORTE_NO_PROGRAMADO) }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var fotoAdjunta by rememberSaveable { mutableStateOf(false) }
    var mensaje by rememberSaveable { mutableStateOf<String?>(null) }
    val repositorio = remember { FakeReporteRepository() }
    val scope = rememberCoroutineScope()
    val captura = rememberCapturaFoto(onFotoCapturada = { fotoAdjunta = true })
    val galeria = rememberFilePickerLauncher(type = FileKitType.Image) { archivo ->
        if (archivo != null) fotoAdjunta = true
    }

    Column(
        Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Reportar una incidencia", style = MaterialTheme.typography.headlineSmall)
        Text("El reporte queda en cola y se enviará al recuperar conexión.")
        MapaReportes()
        TipoReporte.entries.forEach { opcion ->
            FilterChip(opcion == tipo, { tipo = opcion }, label = { Text(opcion.name.replace('_', ' ')) })
        }
        OutlinedTextField(descripcion, { descripcion = it }, Modifier.fillMaxWidth(), label = { Text("¿Qué ocurrió?") }, minLines = 3)
        Button(onClick = captura.tomarFoto, modifier = Modifier.fillMaxWidth()) {
            Text(if (fotoAdjunta) "Foto adjuntada" else "Tomar fotografía")
        }
        Button(onClick = galeria::launch, modifier = Modifier.fillMaxWidth()) {
            Text("Elegir de la galería")
        }
        Button(
            enabled = descripcion.isNotBlank(),
            onClick = {
                scope.launch {
                    CrearReporte(repositorio).ejecutar(
                        Reporte("reporte-${descripcion.hashCode()}", tipo, descripcion, -17.9841, -70.2372, if (fotoAdjunta) "captura-local" else null)
                    )
                    mensaje = "Reporte guardado para sincronizar."
                    descripcion = ""
                    fotoAdjunta = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar reporte") }
        mensaje?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
    }
}
