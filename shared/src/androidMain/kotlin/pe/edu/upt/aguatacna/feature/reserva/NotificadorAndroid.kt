package pe.edu.upt.aguatacna.feature.reserva

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Aviso
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.Notificador
import pe.edu.upt.aguatacna.feature.reserva.presentation.describirMomento
import pe.edu.upt.aguatacna.feature.reserva.presentation.formatearDuracion

private const val CANAL_ID = "reserva"
private const val CANAL_NOMBRE = "Tu reserva de agua"
const val ID_AVISO_AGOTAMIENTO = 1001
const val ID_AVISO_CONFIRMAR = 1002

class NotificadorAndroid(
    private val context: Context,
    private val ahora: () -> LocalDateTime
) : Notificador {

    override fun mostrar(aviso: Aviso) {
        if (!puedeNotificar()) return
        crearCanal()
        val notificacion = when (aviso) {
            is Aviso.AgotamientoAntesDelAbastecimiento -> agotamiento(aviso)
            is Aviso.ConfirmarLlenado -> confirmar(aviso)
        }
        val id = if (aviso is Aviso.ConfirmarLlenado) ID_AVISO_CONFIRMAR else ID_AVISO_AGOTAMIENTO
        NotificationManagerCompat.from(context).notify(id, notificacion)
    }

    private fun agotamiento(aviso: Aviso.AgotamientoAntesDelAbastecimiento) = base(
        titulo = "Tu reserva no alcanza",
        texto = "Se agotaría ${describirMomento(aviso.agotamiento, ahora())} y el agua vuelve después: " +
            "te faltarían ${formatearDuracion(aviso.deficit.horas)}."
    ).build()

    private fun confirmar(aviso: Aviso.ConfirmarLlenado) = base(
        titulo = "¿Ya llenaste el reservorio?",
        texto = "El agua llegó a tu sector ${describirMomento(aviso.inicioDeLaVentana, ahora())}. Confírmalo con un toque."
    ).addAction(0, "Sí, lo llené", accionDeLlenado()).build()

    private fun base(titulo: String, texto: String) = NotificationCompat.Builder(context, CANAL_ID)
        .setSmallIcon(context.applicationInfo.icon)
        .setContentTitle(titulo)
        .setContentText(texto)
        .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
        .setContentIntent(abrirLaApp())
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    private fun abrirLaApp(): PendingIntent {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun accionDeLlenado(): PendingIntent {
        val intent = Intent(context, LlenadoDesdeAvisoReceiver::class.java)
        return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun puedeNotificar(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private fun crearCanal() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val canal = NotificationChannel(CANAL_ID, CANAL_NOMBRE, NotificationManager.IMPORTANCE_DEFAULT)
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }
}
