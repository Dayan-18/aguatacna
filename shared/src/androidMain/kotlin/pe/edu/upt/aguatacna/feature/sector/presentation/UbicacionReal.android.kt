package pe.edu.upt.aguatacna.feature.sector.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada

private const val TIEMPO_MAXIMO_MS = 30_000L
private const val ANTIGUEDAD_ACEPTABLE_MS = 2 * 60_000L

// Android 12+ ignora la solicitud si se pide la ubicación precisa sin la aproximada.
private val PERMISOS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

@Composable
actual fun rememberSolicitarUbicacion(
    onBuscando: () -> Unit,
    onResultado: (ResultadoUbicacion) -> Unit
): () -> Unit {
    val context = LocalContext.current
    return {
        if (PERMISOS.any { concedido(context, it) }) {
            onBuscando()
            pedirUbicacion(context, onResultado)
        } else {
            (context as? Activity)?.let { ActivityCompat.requestPermissions(it, PERMISOS, 7001) }
        }
    }
}

private fun concedido(context: Context, permiso: String) =
    ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED

// "fused" (Google) y "network" (Wi-Fi/antenas) responden en segundos incluso dentro de casa;
// "gps" (satélite) es el más preciso pero tarda y solo se puede usar con permiso preciso.
private fun proveedoresActivos(lm: LocationManager, context: Context): List<String> {
    val candidatos = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) add(LocationManager.FUSED_PROVIDER)
        add(LocationManager.NETWORK_PROVIDER)
        if (concedido(context, Manifest.permission.ACCESS_FINE_LOCATION)) add(LocationManager.GPS_PROVIDER)
    }
    return candidatos.filter { runCatching { lm.isProviderEnabled(it) }.getOrDefault(false) }
}

@SuppressLint("MissingPermission")
private fun pedirUbicacion(context: Context, onResultado: (ResultadoUbicacion) -> Unit) {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        ?: return onResultado(ResultadoUbicacion.SinSenal)
    val proveedores = proveedoresActivos(lm, context)
    if (proveedores.isEmpty()) return onResultado(ResultadoUbicacion.GpsApagado)

    val ultima = proveedores
        .mapNotNull { runCatching { lm.getLastKnownLocation(it) }.getOrNull() }
        .maxByOrNull { it.time }
    if (ultima != null && System.currentTimeMillis() - ultima.time < ANTIGUEDAD_ACEPTABLE_MS) {
        return onResultado(ResultadoUbicacion.Encontrada(Coordenada(ultima.latitude, ultima.longitude)))
    }

    var entregado = false
    val escuchas = mutableListOf<LocationListener>()
    fun terminar(loc: Location?) {
        if (entregado) return
        entregado = true
        escuchas.forEach { lm.removeUpdates(it) }
        onResultado(
            loc?.let { ResultadoUbicacion.Encontrada(Coordenada(it.latitude, it.longitude)) }
                ?: ResultadoUbicacion.SinSenal
        )
    }
    proveedores.forEach { proveedor ->
        val escucha = object : LocationListener {
            override fun onLocationChanged(location: Location) = terminar(location)
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            @Deprecated("Solo lo llaman versiones antiguas de Android")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }
        escuchas += escucha
        runCatching { lm.requestLocationUpdates(proveedor, 0L, 0f, escucha, Looper.getMainLooper()) }
    }
    // Si nadie respondió a tiempo, se usa la última conocida aunque sea antigua.
    Handler(Looper.getMainLooper()).postDelayed({ terminar(ultima) }, TIEMPO_MAXIMO_MS)
}
