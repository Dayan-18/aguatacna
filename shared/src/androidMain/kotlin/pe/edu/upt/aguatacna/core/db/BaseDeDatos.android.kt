package pe.edu.upt.aguatacna.core.db

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

fun crearBaseDeDatos(context: Context): AguaTacnaDatabase =
    Room.databaseBuilder<AguaTacnaDatabase>(
        context = context.applicationContext,
        name = context.getDatabasePath(NOMBRE_BASE_DE_DATOS).absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
