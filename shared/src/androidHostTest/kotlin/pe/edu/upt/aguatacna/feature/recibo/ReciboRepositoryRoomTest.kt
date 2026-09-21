package pe.edu.upt.aguatacna.feature.recibo

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.feature.recibo.data.ReciboRepositoryRoom
import pe.edu.upt.aguatacna.feature.recibo.data.local.ReciboEntity
import pe.edu.upt.aguatacna.feature.recibo.data.local.toDomain
import pe.edu.upt.aguatacna.feature.recibo.data.local.toEntity
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TipoConsumo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReciboRepositoryRoomTest {

    private val dao = FakeReciboDao()
    private val repositorio = ReciboRepositoryRoom(dao)

    private fun <T> bloque(cuerpo: suspend () -> T): T = runBlocking { cuerpo() }

    private fun crearRecibo(
        id: String? = null,
        anio: Int = 2026,
        mes: Int = 4,
        consumoM3: Int = 18,
        centimos: Long = 4550L
    ) = Recibo(
        id = id ?: "r-$anio-$mes",
        periodoConsumo = PeriodoConsumo(anio, mes),
        consumoM3 = consumoM3,
        importeTotal = Dinero(centimos),
        fechaEmision = LocalDate(2026, 4, 15),
        fechaVencimiento = LocalDate(2026, 5, 2),
        tipoConsumo = TipoConsumo.LECTURA,
        lecturaAnteriorM3 = 100,
        lecturaActualM3 = 118,
        numeroMedidor = "MED-00123",
        numeroRecibo = "REC-9988",
        origen = OrigenDatos.ESCANEADO
    )

    @Test
    fun guardarYObservarPersisteLosCamposCorrectamente() = bloque {
        val recibo = crearRecibo(id = "r-1")
        repositorio.guardar(recibo)

        val lista = repositorio.observarRecibos().first()
        assertEquals(1, lista.size)
        val guardado = lista.first()
        assertEquals("r-1", guardado.id)
        assertEquals(PeriodoConsumo(2026, 4), guardado.periodoConsumo)
        assertEquals(18, guardado.consumoM3)
        assertEquals(Dinero(4550L), guardado.importeTotal)
        assertEquals(100, guardado.lecturaAnteriorM3)
        assertEquals(118, guardado.lecturaActualM3)
        assertEquals("MED-00123", guardado.numeroMedidor)
        assertEquals(OrigenDatos.ESCANEADO, guardado.origen)
    }

    @Test
    fun upsertReutilizaElIdSiElPeriodoYaExiste() = bloque {
        val reciboOriginal = crearRecibo(id = "original-id", anio = 2026, mes = 3, consumoM3 = 14)
        repositorio.guardar(reciboOriginal)

        // Nuevo recibo para el mismo período con ID diferente y consumo modificado
        val reciboActualizado = crearRecibo(id = "nuevo-id", anio = 2026, mes = 3, consumoM3 = 22)
        repositorio.guardar(reciboActualizado)

        val lista = repositorio.observarRecibos().first()
        assertEquals(1, lista.size)
        val guardado = lista.first()
        assertEquals("original-id", guardado.id, "Debe reutilizar el ID existente para el mismo período")
        assertEquals(22, guardado.consumoM3, "Debe actualizar el consumo")
    }

    @Test
    fun obtenerPorPeriodoDevuelveReciboExistenteONull() = bloque {
        repositorio.guardar(crearRecibo(anio = 2026, mes = 1))
        repositorio.guardar(crearRecibo(anio = 2026, mes = 2))

        val enero = repositorio.obtenerPorPeriodo(PeriodoConsumo(2026, 1))
        assertNotNull(enero)
        assertEquals(PeriodoConsumo(2026, 1), enero.periodoConsumo)

        val marzo = repositorio.obtenerPorPeriodo(PeriodoConsumo(2026, 3))
        assertNull(marzo)
    }

    @Test
    fun eliminarRemueveElReciboCorrecto() = bloque {
        repositorio.guardar(crearRecibo(id = "r-1", anio = 2026, mes = 1))
        repositorio.guardar(crearRecibo(id = "r-2", anio = 2026, mes = 2))

        repositorio.eliminar("r-1")

        val lista = repositorio.observarRecibos().first()
        assertEquals(1, lista.size)
        assertEquals("r-2", lista.first().id)
    }

    @Test
    fun mapeoEntidadMantieneIntegridadSinDatosPersonales() {
        val recibo = crearRecibo()
        val entidad = recibo.toEntity()

        // Verificar que la entidad no tiene campos de datos personales (T-11.2)
        assertEquals(2026, entidad.anio)
        assertEquals(4, entidad.mes)
        assertEquals(18, entidad.consumoM3)
        assertEquals(4550L, entidad.importeCentimos)
        assertEquals("2026-04-15", entidad.fechaEmision)
        assertEquals("2026-05-02", entidad.fechaVencimiento)
        assertEquals("LECTURA", entidad.tipoConsumo)
        assertEquals("ESCANEADO", entidad.origen)

        // Comprobar reversibilidad
        val restaurado = entidad.toDomain()
        assertEquals(recibo.periodoConsumo, restaurado.periodoConsumo)
        assertEquals(recibo.consumoM3, restaurado.consumoM3)
        assertEquals(recibo.importeTotal, restaurado.importeTotal)
        assertEquals(recibo.fechaEmision, restaurado.fechaEmision)
        assertEquals(recibo.fechaVencimiento, restaurado.fechaVencimiento)
        assertEquals(recibo.tipoConsumo, restaurado.tipoConsumo)
        assertEquals(recibo.origen, restaurado.origen)
    }
}
