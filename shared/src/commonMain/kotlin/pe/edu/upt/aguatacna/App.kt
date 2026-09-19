package pe.edu.upt.aguatacna

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import pe.edu.upt.aguatacna.core.navigation.AppNavegacion
import pe.edu.upt.aguatacna.core.ui.theme.AguaTacnaTheme

@Composable
@Preview
fun App() {
    AguaTacnaTheme {
        AppNavegacion()
    }
}
