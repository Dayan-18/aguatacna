package pe.edu.upt.aguatacna.core.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import pe.edu.upt.aguatacna.data.local.UsuarioEntity
import pe.edu.upt.aguatacna.feature.recibo.data.local.ReciboEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.EventoLlenadoEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.NovedadReservaEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.PerfilHogarEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.ReservaDao
import pe.edu.upt.aguatacna.feature.retos.data.local.RetoEntity
import pe.edu.upt.aguatacna.feature.sector.data.local.SectorEntity

const val NOMBRE_BASE_DE_DATOS = "aguatacna.db"

@Database(
    entities = [
        UsuarioEntity::class,
        PerfilHogarEntity::class,
        EventoLlenadoEntity::class,
        NovedadReservaEntity::class,
        SectorEntity::class,
        ReciboEntity::class,
        RetoEntity::class
    ],
    version = 1,
    exportSchema = true
)
@ConstructedBy(AguaTacnaDatabaseConstructor::class)
abstract class AguaTacnaDatabase : RoomDatabase() {
    abstract fun reservaDao(): ReservaDao
}

// Room genera el `actual` de cada plataforma.
@Suppress("KotlinNoActualForExpect")
expect object AguaTacnaDatabaseConstructor : RoomDatabaseConstructor<AguaTacnaDatabase> {
    override fun initialize(): AguaTacnaDatabase
}
