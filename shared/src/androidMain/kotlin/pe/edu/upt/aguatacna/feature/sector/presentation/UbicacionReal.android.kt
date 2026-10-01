package pe.edu.upt.aguatacna.feature.sector.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada

@Composable
actual fun rememberSolicitarUbicacion(onResultado: (Coordenada?) -> Unit): () -> Unit {
    val context = LocalContext.current
    return {
        if (tienePermisoUbicacion(context)) {
            onResultado(ubicacionActual(context))
        } else {
            // Pide el permiso. Cuando el usuario lo conceda, al volver a tocar se lee el GPS.
            (context as? Activity)?.let {
                ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 7001)
            }
        }
    }
}

private fun tienePermisoUbicacion(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

// Con el permiso concedido, toma la última ubicación conocida (GPS o red). Null si no hay (p. ej. emulador).
@SuppressLint("MissingPermission")
private fun ubicacionActual(context: Context): Coordenada? {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
    val loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    return loc?.let { Coordenada(it.latitude, it.longitude) }
}
