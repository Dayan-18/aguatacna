package pe.edu.upt.aguatacna.core.db

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import platform.Foundation.NSHomeDirectory

fun crearBaseDeDatos(): AguaTacnaDatabase =
    Room.databaseBuilder<AguaTacnaDatabase>(name = "${NSHomeDirectory()}/$NOMBRE_BASE_DE_DATOS")
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
