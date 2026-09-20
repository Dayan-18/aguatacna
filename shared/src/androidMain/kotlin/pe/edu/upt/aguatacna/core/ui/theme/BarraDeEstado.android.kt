package pe.edu.upt.aguatacna.core.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
actual fun IconosClarosEnBarraDeEstado(claros: Boolean) {
    val vista = LocalView.current
    if (vista.isInEditMode) return
    SideEffect {
        val ventana = vista.context.buscarActividad()?.window ?: return@SideEffect
        WindowCompat.getInsetsController(ventana, vista).isAppearanceLightStatusBars = !claros
    }
}

private fun Context.buscarActividad(): Activity? {
    var actual: Context? = this
    while (actual is ContextWrapper) {
        if (actual is Activity) return actual
        actual = actual.baseContext
    }
    return null
}
