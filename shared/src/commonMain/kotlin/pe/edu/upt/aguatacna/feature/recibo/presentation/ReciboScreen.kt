package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Punto de entrada del feature Recibo.
 * Gestiona la navegación interna entre las 4 sub-pantallas
 * sin librería de navegación (patrón idéntico a ReservaScreen).
 */
@Composable
fun ReciboScreen() {
    var subPantalla by rememberSaveable { mutableStateOf("principal") }

    when (subPantalla) {
        "historial" -> ReciboHistorialScreen(onVolver = { subPantalla = "principal" })
        "foto" -> ReciboFotoScreen(onVolver = { subPantalla = "principal" })
        "manualMedidor" -> ReciboManualMedidorScreen(onVolver = { subPantalla = "principal" })
        else -> ReciboPrincipalScreen(
            onVerHistorial = { subPantalla = "historial" },
            onEscanearRecibo = { subPantalla = "foto" },
            onLecturaManual = { subPantalla = "manualMedidor" }
        )
    }
}
