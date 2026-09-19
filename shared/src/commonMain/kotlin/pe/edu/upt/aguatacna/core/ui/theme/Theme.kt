package pe.edu.upt.aguatacna.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val esquemaClaro = lightColorScheme(
    primary = AguaMedia,
    onPrimary = Blanco,
    primaryContainer = Tenue,
    onPrimaryContainer = AguaMedia,
    secondary = Agua,
    tertiary = Ocre,
    error = Coral,
    background = Fondo,
    onBackground = Tinta,
    surface = Blanco,
    onSurface = Tinta,
    surfaceVariant = Tenue,
    onSurfaceVariant = TintaSuave
)

@Composable
fun AguaTacnaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = esquemaClaro, content = content)
}
