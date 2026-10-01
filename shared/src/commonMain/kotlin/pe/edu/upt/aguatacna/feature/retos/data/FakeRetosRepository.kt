package pe.edu.upt.aguatacna.feature.retos.data

import kotlinx.datetime.LocalDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import pe.edu.upt.aguatacna.feature.retos.domain.model.Reto
import pe.edu.upt.aguatacna.feature.retos.domain.model.RetoUsuario
import pe.edu.upt.aguatacna.feature.retos.domain.repository.RetosRepository

class FakeRetosRepository(
    private val hoy: LocalDate,
    incluirDemostracion: Boolean = true
) : RetosRepository {
    private val cumplimientos = if (incluirDemostracion) ((0..5).map { dias ->
        RetoUsuario(
            retoId = if (dias % 2 == 0) "carga-completa" else "ducha-corta",
            fecha = hoy.minus(dias, DateTimeUnit.DAY),
            cumplido = true
        )
    } + RetoUsuario("ducha-corta", hoy, true)).toMutableList() else mutableListOf()

    override suspend fun obtenerRetosActivos(): List<Reto> = retosDePrueba
    override suspend fun obtenerCumplimientos(): List<RetoUsuario> = cumplimientos.toList()
    override suspend fun guardarCumplimiento(cumplimiento: RetoUsuario) {
        cumplimientos.removeAll { it.retoId == cumplimiento.retoId && it.fecha == cumplimiento.fecha }
        cumplimientos.add(cumplimiento)
    }
    override suspend fun obtenerPromedioSector(sectorId: String): Double? = 310.0
    override suspend fun fechaActual(): LocalDate = hoy
}

private val retosDePrueba = listOf(
    Reto("carga-completa", "Lavar ropa solo con carga completa", "Aprovecha cada ciclo de lavado.", 90),
    Reto("ducha-corta", "Duchas de 5 minutos toda la semana", "Reduce el tiempo bajo la ducha.", 210),
    Reto("reusar-agua", "Reutilizar el agua del enjuague", "Úsala para limpiar pisos.", 60)
)
