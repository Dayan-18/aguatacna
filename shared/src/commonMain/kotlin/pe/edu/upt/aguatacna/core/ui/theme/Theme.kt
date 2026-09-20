package pe.edu.upt.aguatacna.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily

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
    val fuente = FuenteTexto
    val tipografia = remember(fuente) { tipografiaCon(fuente) }
    MaterialTheme(colorScheme = esquemaClaro, typography = tipografia, content = content)
}

private fun tipografiaCon(fuente: FontFamily): Typography {
    val base = Typography()
    return base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = fuente),
        displayMedium = base.displayMedium.copy(fontFamily = fuente),
        displaySmall = base.displaySmall.copy(fontFamily = fuente),
        headlineLarge = base.headlineLarge.copy(fontFamily = fuente),
        headlineMedium = base.headlineMedium.copy(fontFamily = fuente),
        headlineSmall = base.headlineSmall.copy(fontFamily = fuente),
        titleLarge = base.titleLarge.copy(fontFamily = fuente),
        titleMedium = base.titleMedium.copy(fontFamily = fuente),
        titleSmall = base.titleSmall.copy(fontFamily = fuente),
        bodyLarge = base.bodyLarge.copy(fontFamily = fuente),
        bodyMedium = base.bodyMedium.copy(fontFamily = fuente),
        bodySmall = base.bodySmall.copy(fontFamily = fuente),
        labelLarge = base.labelLarge.copy(fontFamily = fuente),
        labelMedium = base.labelMedium.copy(fontFamily = fuente),
        labelSmall = base.labelSmall.copy(fontFamily = fuente)
    )
}
