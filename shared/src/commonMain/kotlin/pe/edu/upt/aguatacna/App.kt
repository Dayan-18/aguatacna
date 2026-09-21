package pe.edu.upt.aguatacna

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import pe.edu.upt.aguatacna.core.navigation.AppNavegacion
import pe.edu.upt.aguatacna.core.ui.theme.AguaTacnaTheme
import pe.edu.upt.aguatacna.feature.bienvenida.presentation.PuertaDeAcceso

@Composable
@Preview
fun App() {
    AguaTacnaTheme {
        PuertaDeAcceso { AppNavegacion() }
    }
}
