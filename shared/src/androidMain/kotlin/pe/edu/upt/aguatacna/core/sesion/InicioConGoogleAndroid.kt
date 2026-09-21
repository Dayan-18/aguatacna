package pe.edu.upt.aguatacna.core.sesion

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.CancellationException
import pe.edu.upt.aguatacna.core.nube.ConfiguracionNube
import pe.edu.upt.aguatacna.core.util.nuevoUuid
import java.lang.ref.WeakReference
import java.security.MessageDigest

/** La ventana de Google necesita una `Activity`; la actual se registra desde `MainActivity`. */
object ActividadActual {
    private var referencia: WeakReference<Activity>? = null

    fun registrar(actividad: Activity) { referencia = WeakReference(actividad) }

    fun liberar(actividad: Activity) { if (referencia?.get() === actividad) referencia = null }

    fun obtener(): Activity? = referencia?.get()
}

/** Pide a Google la cuenta con Credential Manager y entrega su token a Supabase, que abre la sesión. */
class InicioConGoogleAndroid(
    private val supabase: SupabaseClient,
    private val actividad: () -> Activity? = ActividadActual::obtener
) : InicioConGoogle {

    override suspend fun iniciar(): ResultadoInicio {
        val pantalla = actividad() ?: return ResultadoInicio.Fallido("No hay una pantalla activa")
        // Google recibe el hash del nonce y Supabase el original: así comprueba que el token es de esta petición.
        val nonce = nuevoUuid()
        val opcion = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(ConfiguracionNube.ID_CLIENTE_WEB_GOOGLE)
            .setNonce(hashDelNonce(nonce))
            .build()
        val peticion = GetCredentialRequest.Builder().addCredentialOption(opcion).build()
        return try {
            val credencial = CredentialManager.create(pantalla).getCredential(pantalla, peticion).credential
            if (credencial !is CustomCredential || credencial.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                return ResultadoInicio.Fallido("Google no entregó una cuenta")
            }
            val token = GoogleIdTokenCredential.createFrom(credencial.data).idToken
            supabase.auth.signInWith(IDToken) {
                idToken = token
                provider = Google
                this.nonce = nonce
            }
            ResultadoInicio.Exitoso
        } catch (e: CancellationException) {
            throw e
        } catch (e: GetCredentialCancellationException) {
            ResultadoInicio.Cancelado
        } catch (e: NoCredentialException) {
            ResultadoInicio.SinCuentas
        } catch (e: GetCredentialException) {
            ResultadoInicio.Fallido(e.message ?: "Error de Google")
        } catch (e: Exception) {
            ResultadoInicio.Fallido(e.message ?: "Error de Supabase")
        }
    }

    companion object {
        fun hashDelNonce(nonce: String): String =
            MessageDigest.getInstance("SHA-256").digest(nonce.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}
