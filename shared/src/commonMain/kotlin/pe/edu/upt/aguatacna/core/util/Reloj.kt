package pe.edu.upt.aguatacna.core.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun interface Reloj {
    fun ahora(): LocalDateTime
}

@OptIn(ExperimentalTime::class)
class RelojDelSistema : Reloj {
    override fun ahora(): LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

@OptIn(ExperimentalUuidApi::class)
fun nuevoUuid(): String = Uuid.random().toString()
