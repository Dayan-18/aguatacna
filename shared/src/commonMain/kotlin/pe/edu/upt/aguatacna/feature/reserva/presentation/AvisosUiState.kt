package pe.edu.upt.aguatacna.feature.reserva.presentation

data class AvisosUiState(
    val cargando: Boolean = true,
    val avisos: List<AvisoVista> = emptyList()
)

enum class TipoDeAviso { AGOTAMIENTO, CONFIRMAR_LLENADO }

/** A dónde lleva tocar un aviso. */
enum class DestinoDelAviso { QUE_RECORTAR, MI_RESERVA }

data class AvisoVista(
    val id: String,
    val tipo: TipoDeAviso,
    val titulo: String,
    val texto: String,
    val cuando: String,
    val sinLeer: Boolean,
    val destino: DestinoDelAviso
)
