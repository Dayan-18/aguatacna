package pe.edu.upt.aguatacna

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import pe.edu.upt.aguatacna.core.di.mantenerReservaSincronizada
import pe.edu.upt.aguatacna.core.sesion.ActividadActual
import pe.edu.upt.aguatacna.feature.reserva.RecalculoHorarioWorker

class MainActivity : ComponentActivity() {

    private val pedirPermisoDeAvisos =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* sin permiso, los avisos simplemente no se muestran */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ActividadActual.registrar(this)
        solicitarPermisoDeAvisos()

        setContent {
            App()
        }
    }

    override fun onDestroy() {
        ActividadActual.liberar(this)
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        RecalculoHorarioWorker.recalcularAhora(this)
        // Mientras la app está a la vista, copia a la nube los cambios de la reserva si hay una sesión de Google.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) { mantenerReservaSincronizada() }
        }
    }

    // Desde Android 13 los avisos de la reserva necesitan el permiso del usuario.
    private fun solicitarPermisoDeAvisos() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val concedido = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!concedido) pedirPermisoDeAvisos.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}