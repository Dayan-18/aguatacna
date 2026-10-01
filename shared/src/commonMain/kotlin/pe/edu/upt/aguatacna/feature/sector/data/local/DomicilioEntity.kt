package pe.edu.upt.aguatacna.feature.sector.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// La coordenada exacta de la casa vive solo en el teléfono: nunca se sincroniza con
// Supabase, donde solo se guarda el sector (constitución, art. IX). Una sola fila (id = 0).
@Entity(tableName = "domicilio")
data class DomicilioEntity(
    @PrimaryKey val id: Int = 0,
    val latitud: Double,
    val longitud: Double
)
