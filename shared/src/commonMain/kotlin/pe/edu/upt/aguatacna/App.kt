package pe.edu.upt.aguatacna

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import pe.edu.upt.aguatacna.core.navigation.AppNavegacion
import pe.edu.upt.aguatacna.core.ui.theme.AguaTacnaTheme
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.feature.bienvenida.presentation.PuertaDeAcceso
import pe.edu.upt.aguatacna.feature.sector.presentation.RegistrarDomicilioScreen

@Composable
@Preview
fun App() {
    AguaTacnaTheme {
        PuertaDeAcceso(
            registrarDomicilio = {
                // La bienvenida deja los iconos claros; esta pantalla tiene el encabezado blanco.
                IconosClarosEnBarraDeEstado(claros = false)
                RegistrarDomicilioScreen()
            }
        ) { AppNavegacion() }
    }
}
