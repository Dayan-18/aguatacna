package pe.edu.upt.aguatacna.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// La llave es un UUID local e inmutable (constitución, artículo III).
// El identificador de Google o del proveedor de autenticación son campos, nunca la llave.
@Entity(tableName = "usuario")
data class UsuarioEntity(
    @PrimaryKey val id: String,
    val googleSub: String? = null,
    val authProviderId: String? = null,
    val sectorId: String? = null
)
