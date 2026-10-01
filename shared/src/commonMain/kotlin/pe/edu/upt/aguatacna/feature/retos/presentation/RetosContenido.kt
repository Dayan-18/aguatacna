package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upt.aguatacna.core.ui.theme.Fondo

@Composable
fun RetosContenido(
    estado: RetosUiState,
    completar: (String) -> Unit,
    onVerPosicion: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().background(Fondo).verticalScroll(rememberScrollState())
    ) {
        CabeceraAhorro(estado.racha.dias, onVerPosicion)
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
            RachaSemanal(estado.racha.dias, estado.hoy, estado.diasCumplidos)
            estado.hoy?.let { CalendarioAhorro(it, estado.diasCumplidos) }
            EtiquetaSeccion("RETOS DE LA SEMANA", Modifier.padding(top = 22.dp, bottom = 8.dp))
            RetosSemana(estado.retos, estado.cumplidos, completar)
            ResumenAhorro(estado.retos.filter { it.id in estado.cumplidos }.sumOf { it.litrosMeta })
            EtiquetaSeccion("INSIGNIAS", Modifier.padding(top = 18.dp, bottom = 8.dp))
            InsigniasAhorro(estado.diasCumplidos.size, estado.retos.filter { it.id in estado.cumplidos }.sumOf { it.litrosMeta })
        }
    }
}
