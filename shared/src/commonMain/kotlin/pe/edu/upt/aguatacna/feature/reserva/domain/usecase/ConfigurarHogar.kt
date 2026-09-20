package pe.edu.upt.aguatacna.feature.reserva.domain.usecase

import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfiguracionHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.EstimadorPorHabitos
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.ReservaRepository

/** Guarda la configuración del hogar junto con el consumo que se estima de sus hábitos. */
class ConfigurarHogar(
    private val estimador: EstimadorPorHabitos,
    private val repositorio: ReservaRepository
) {
    suspend operator fun invoke(configuracion: ConfiguracionHogar): Result<Unit> {
        val consumo = estimador.estimar(configuracion.habitos, configuracion.habitantes)
        return repositorio.guardarPerfil(configuracion, consumo)
    }
}
