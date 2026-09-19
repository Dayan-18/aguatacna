package pe.edu.upt.aguatacna.core.navigation

import aguatacna.shared.generated.resources.Res
import aguatacna.shared.generated.resources.ic_ahorro
import aguatacna.shared.generated.resources.ic_asistente
import aguatacna.shared.generated.resources.ic_recibo
import aguatacna.shared.generated.resources.ic_reserva
import aguatacna.shared.generated.resources.ic_sector
import org.jetbrains.compose.resources.DrawableResource

enum class Destino(
    val etiqueta: String,
    val icono: DrawableResource,
    val responsable: String
) {
    RESERVA("Reserva", Res.drawable.ic_reserva, "Cristhian"),
    SECTOR("Sector", Res.drawable.ic_sector, "Dayan"),
    AHORRO("Ahorro", Res.drawable.ic_ahorro, "Jimmy"),
    RECIBO("Recibo", Res.drawable.ic_recibo, "Iker"),
    ASISTENTE("Asistente", Res.drawable.ic_asistente, "Iker")
}
