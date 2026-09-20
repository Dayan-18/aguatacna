package pe.edu.upt.aguatacna

import android.app.Application
import pe.edu.upt.aguatacna.core.di.iniciarAplicacion
import pe.edu.upt.aguatacna.feature.reserva.RecalculoHorarioWorker

class AguaTacnaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        iniciarAplicacion(this)
        RecalculoHorarioWorker.programar(this)
    }
}
