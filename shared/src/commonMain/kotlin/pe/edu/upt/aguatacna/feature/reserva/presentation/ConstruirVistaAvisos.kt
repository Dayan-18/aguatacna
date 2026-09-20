package pe.edu.upt.aguatacna.feature.reserva.presentation

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Aviso
import pe.edu.upt.aguatacna.feature.reserva.domain.model.AvisoGuardado

/** Convierte los avisos guardados en las tarjetas de la lista, con los textos del Figma. */
class ConstruirVistaAvisos {

    operator fun invoke(guardados: List<AvisoGuardado>, ahora: LocalDateTime): List<AvisoVista> =
        guardados.map { guardado ->
            when (val aviso = guardado.aviso) {
                is Aviso.AgotamientoAntesDelAbastecimiento -> agotamiento(guardado, aviso, ahora)
                is Aviso.ConfirmarLlenado -> confirmar(guardado, ahora)
            }
        }

    private fun agotamiento(guardado: AvisoGuardado, aviso: Aviso.AgotamientoAntesDelAbastecimiento, ahora: LocalDateTime): AvisoVista {
        val cuandoSeAcaba = describirHora(aviso.agotamiento, guardado.momento).let {
            if (aviso.agotamiento.date == guardado.momento.date) "a las $it" else it
        }
        return AvisoVista(
            id = guardado.id,
            tipo = TipoDeAviso.AGOTAMIENTO,
            titulo = "Tu reserva se agota antes del próximo abastecimiento",
            texto = "Se acaba $cuandoSeAcaba y te faltarían ${formatearDuracion(aviso.deficit.horas)} de agua. Toca para ver qué recortar.",
            cuando = describirCuando(guardado.momento, ahora),
            sinLeer = !guardado.leido,
            destino = DestinoDelAviso.QUE_RECORTAR
        )
    }

    private fun confirmar(guardado: AvisoGuardado, ahora: LocalDateTime) = AvisoVista(
        id = guardado.id,
        tipo = TipoDeAviso.CONFIRMAR_LLENADO,
        titulo = "¿Llegó el agua a tu casa?",
        texto = "Confirma con un toque para mantener tu proyección al día.",
        cuando = describirCuando(guardado.momento, ahora),
        sinLeer = !guardado.leido,
        destino = DestinoDelAviso.MI_RESERVA
    )
}
