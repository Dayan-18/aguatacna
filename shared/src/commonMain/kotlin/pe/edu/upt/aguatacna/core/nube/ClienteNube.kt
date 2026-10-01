package pe.edu.upt.aguatacna.core.nube

import com.russhwolf.settings.Settings
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsCodeVerifierCache
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Datos públicos del proyecto Supabase "AguaTacna". La clave es la publicable (sb_publishable_…),
 * pensada para ir dentro de la app: lo que protege los datos son las políticas por usuario de cada tabla.
 * El secreto del cliente de Google vive solo en el panel de Supabase, nunca aquí.
 */
object ConfiguracionNube {
    const val URL = "https://gkcqhmoegunrohoveamz.supabase.co"
    const val CLAVE_PUBLICA = "sb_publishable_JRUe-q-GxAv3LP0S6e13FQ_Wf-ScLAV"

    /** ID del cliente web de Google Cloud; con él Google emite el token que Supabase valida. */
    const val ID_CLIENTE_WEB_GOOGLE = "894466818178-2f6l16kmgq4fq46ge81ehbe7t0slop8k.apps.googleusercontent.com"
}

/**
 * `settings` guarda la sesión para que sobreviva a un reinicio de la app: sin pasarla, el plugin
 * de autenticación intenta crear su propio almacenamiento con el contexto por defecto de la
 * plataforma, lo que revienta fuera de un Android real (por ejemplo, en las pruebas unitarias).
 * Cada plataforma aporta la suya (SharedPreferences en Android); las pruebas, una en memoria.
 */
fun crearClienteSupabase(settings: Settings): SupabaseClient =
    createSupabaseClient(ConfiguracionNube.URL, ConfiguracionNube.CLAVE_PUBLICA) {
        install(Auth) {
            sessionManager = SettingsSessionManager(settings)
            // También por defecto crea su propio almacenamiento (para el flujo OAuth con navegador, que no usamos).
            codeVerifierCache = SettingsCodeVerifierCache(settings)
        }
        install(Postgrest)
    }
