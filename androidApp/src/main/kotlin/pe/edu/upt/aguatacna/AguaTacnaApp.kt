package pe.edu.upt.aguatacna

import android.app.Application
import pe.edu.upt.aguatacna.core.di.iniciarAplicacion

class AguaTacnaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        iniciarAplicacion(this)
    }
}
