package pe.edu.upt.aguatacna.core.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import pe.edu.upt.aguatacna.feature.recibo.di.moduloRecibo
import pe.edu.upt.aguatacna.feature.reserva.di.moduloReserva
import pe.edu.upt.aguatacna.feature.retos.di.moduloRetos
import pe.edu.upt.aguatacna.feature.sector.di.moduloSector

val modulosApp: List<Module> = listOf(moduloReserva, moduloSector, moduloRecibo, moduloRetos)

fun iniciarKoin(extra: KoinAppDeclaration? = null): KoinApplication =
    startKoin {
        extra?.invoke(this)
        modules(modulosApp)
    }
