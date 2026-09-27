package pe.edu.upt.aguatacna.core.db

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import pe.edu.upt.aguatacna.data.local.UsuarioDao
import pe.edu.upt.aguatacna.data.local.UsuarioEntity
import pe.edu.upt.aguatacna.feature.recibo.data.local.ReciboDao
import pe.edu.upt.aguatacna.feature.recibo.data.local.ReciboEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.EventoLlenadoEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.AvisoReservaDao
import pe.edu.upt.aguatacna.feature.reserva.data.local.AvisoReservaEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.NovedadReservaEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.PerfilHogarEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.ReservaDao
import pe.edu.upt.aguatacna.feature.retos.data.local.RetoEntity
import pe.edu.upt.aguatacna.feature.sector.data.local.ConfirmacionHorarioEntity
import pe.edu.upt.aguatacna.feature.sector.data.local.CronogramaEntity
import pe.edu.upt.aguatacna.feature.sector.data.local.PuntoCisternaEntity
import pe.edu.upt.aguatacna.feature.sector.data.local.SectorDao
import pe.edu.upt.aguatacna.feature.sector.data.local.SectorEntity

const val NOMBRE_BASE_DE_DATOS = "aguatacna.db"

@Database(
    entities = [
        UsuarioEntity::class,
        PerfilHogarEntity::class,
        EventoLlenadoEntity::class,
        NovedadReservaEntity::class,
        AvisoReservaEntity::class,
        SectorEntity::class,
        CronogramaEntity::class,
        PuntoCisternaEntity::class,
        ConfirmacionHorarioEntity::class,
        ReciboEntity::class,
        RetoEntity::class
    ],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4)
    ]
)
@ConstructedBy(AguaTacnaDatabaseConstructor::class)
abstract class AguaTacnaDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun reservaDao(): ReservaDao
    abstract fun reciboDao(): ReciboDao
    abstract fun avisoReservaDao(): AvisoReservaDao
    abstract fun sectorDao(): SectorDao
}

// Room genera el `actual` de cada plataforma.
@Suppress("KotlinNoActualForExpect")
expect object AguaTacnaDatabaseConstructor : RoomDatabaseConstructor<AguaTacnaDatabase> {
    override fun initialize(): AguaTacnaDatabase
}
