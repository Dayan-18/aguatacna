package pe.edu.upt.aguatacna.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/** Estado del permiso de cámara para que la UI pueda reaccionar. */
enum class EstadoPermisoCamara {
    /** Aún no se ha intentado pedir el permiso. */
    Desconocido,

    /** Concedido: la cámara se abre con normalidad. */
    Concedido,

    /** Denegado, pero se puede volver a pedir. */
    Denegado,

    /** Denegado con "no volver a preguntar": hay que ir a Ajustes. */
    DenegadoPermanente,
}

/**
 * Objeto que expone la UI:
 *  - [estado]: estado actual del permiso
 *  - [tomarFoto]: pide permiso (si hace falta) y abre la cámara
 *  - [abrirAjustes]: abre los ajustes de la app (para permiso denegado permanente)
 */
class CapturaFoto(
    val estado: EstadoPermisoCamara,
    val tomarFoto: () -> Unit,
    val abrirAjustes: () -> Unit,
)

/**
 * Base reutilizable para tomar fotos en Android e iOS.
 *
 * @param onFotoCapturada se llama con la foto en bytes cuando el usuario toma la foto.
 * @param onError se llama si algo falla (permiso, cámara o lectura del archivo).
 */
@Composable
fun rememberCapturaFoto(
    onFotoCapturada: (ByteArray) -> Unit,
    onError: (String) -> Unit = {},
): CapturaFoto {
    val scope = rememberCoroutineScope()
    var estado by remember { mutableStateOf(EstadoPermisoCamara.Desconocido) }

    // Siempre usa las últimas versiones de los callbacks
    val fotoCallback by rememberUpdatedState(onFotoCapturada)
    val errorCallback by rememberUpdatedState(onError)

    // Controlador de permisos (moko)
    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) { factory.createPermissionsController() }
    BindEffect(controller)

    // Lanzador de la cámara (FileKit)
    val cameraLauncher = rememberCameraPickerLauncher { archivo ->
        // archivo == null si el usuario cancela
        if (archivo != null) {
            scope.launch {
                try {
                    fotoCallback(archivo.readBytes())
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    errorCallback("No se pudo leer la foto: ${e.message}")
                }
            }
        }
    }

    val tomarFoto: () -> Unit = {
        scope.launch {
            try {
                // Si ya estaba concedido, regresa de inmediato sin mostrar nada.
                // Si no, muestra el diálogo del sistema.
                controller.providePermission(Permission.CAMERA)
                estado = EstadoPermisoCamara.Concedido
                cameraLauncher.launch()
            } catch (e: DeniedAlwaysException) {
                estado = EstadoPermisoCamara.DenegadoPermanente
            } catch (e: DeniedException) {
                estado = EstadoPermisoCamara.Denegado
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorCallback("Error al abrir la cámara: ${e.message}")
            }
        }
    }

    return CapturaFoto(
        estado = estado,
        tomarFoto = tomarFoto,
        abrirAjustes = { controller.openAppSettings() },
    )
}
