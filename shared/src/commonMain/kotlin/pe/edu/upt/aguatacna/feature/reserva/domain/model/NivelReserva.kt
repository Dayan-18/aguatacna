package pe.edu.upt.aguatacna.feature.reserva.domain.model

data class NivelReserva(
    val litros: Litros,
    val capacidad: CapacidadLitros
) {
    init {
        require(litros <= capacidad.litros) { "El nivel no puede superar la capacidad del reservorio" }
    }

    val porcentaje: Double get() = litros / capacidad.litros * 100

    companion object {
        // "Llenó a la mitad" deja el tanque al 50 %; no suma al nivel anterior.
        fun trasLlenado(capacidad: CapacidadLitros, tipo: TipoLlenado): NivelReserva =
            NivelReserva(capacidad.litros * tipo.fraccion, capacidad)
    }
}
