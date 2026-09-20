package pe.edu.upt.aguatacna.feature.reserva

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.core.di.koinIniciado
import pe.edu.upt.aguatacna.core.util.Reloj
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.RecalcularYAvisar
import java.util.concurrent.TimeUnit

private const val NOMBRE_DE_LA_TAREA = "recalculo-horario-reserva"
private const val NOMBRE_INMEDIATO = "recalculo-inmediato-reserva"

/** Cada hora recalcula la proyección y avisa si la reserva no alcanza o hay que confirmar el llenado. */
class RecalculoHorarioWorker(contexto: Context, parametros: WorkerParameters) : CoroutineWorker(contexto, parametros) {

    override suspend fun doWork(): Result {
        if (!koinIniciado()) return Result.retry()
        val koin = KoinPlatform.getKoin()
        val reloj = koin.get<Reloj>()
        val tarea = RecalcularYAvisar(
            repositorio = koin.get(),
            sector = koin.get(),
            registro = koin.get(),
            notificador = NotificadorAndroid(applicationContext, reloj::ahora)
        )
        tarea(reloj.ahora())
        return Result.success()
    }

    companion object {
        /** Recalcula ya mismo, sin esperar a la próxima hora: se usa al abrir la app para que los avisos estén al día. */
        fun recalcularAhora(context: Context) {
            WorkManager.getInstance(context)
                .enqueueUniqueWork(NOMBRE_INMEDIATO, ExistingWorkPolicy.REPLACE, OneTimeWorkRequestBuilder<RecalculoHorarioWorker>().build())
        }

        /** Se puede llamar en cada arranque: si la tarea ya está programada, se conserva. */
        fun programar(context: Context) {
            val peticion = PeriodicWorkRequestBuilder<RecalculoHorarioWorker>(1, TimeUnit.HOURS).build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(NOMBRE_DE_LA_TAREA, ExistingPeriodicWorkPolicy.KEEP, peticion)
        }
    }
}
