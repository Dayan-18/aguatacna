package pe.edu.upt.aguatacna.feature.reserva

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.core.util.Reloj
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.ReservaRepository

/** La acción "Sí, lo llené" de la notificación: registra un llenado completo sin abrir la app. */
class LlenadoDesdeAvisoReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendiente = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val koin = KoinPlatform.getKoin()
                koin.get<ReservaRepository>().registrarLlenado(koin.get<Reloj>().ahora(), TipoLlenado.COMPLETO)
                NotificationManagerCompat.from(context).cancel(ID_AVISO_CONFIRMAR)
            } finally {
                pendiente.finish()
            }
        }
    }
}
