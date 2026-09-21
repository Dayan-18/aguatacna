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