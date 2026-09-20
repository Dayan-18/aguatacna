package pe.edu.upt.aguatacna.feature.reserva

import android.content.Context
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.RegistroDeAvisos

/** Guarda solo el último aviso de cada tipo (el prefijo de la clave), que es lo único que hace falta para no repetir. */
class RegistroDeAvisosPrefs(context: Context) : RegistroDeAvisos {

    private val prefs = context.getSharedPreferences("avisos_reserva", Context.MODE_PRIVATE)

    override fun yaSeAviso(clave: String): Boolean = prefs.getString(tipoDe(clave), null) == clave

    override fun marcarComoAvisado(clave: String) {
        prefs.edit().putString(tipoDe(clave), clave).apply()
    }

    private fun tipoDe(clave: String) = clave.substringBefore('-')
}
