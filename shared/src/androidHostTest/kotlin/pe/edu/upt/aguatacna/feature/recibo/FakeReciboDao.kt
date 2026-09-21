package pe.edu.upt.aguatacna.feature.recibo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import pe.edu.upt.aguatacna.feature.recibo.data.local.ReciboDao
import pe.edu.upt.aguatacna.feature.recibo.data.local.ReciboEntity

class FakeReciboDao : ReciboDao {
    private val recibos = MutableStateFlow<List<ReciboEntity>>(emptyList())

    override fun observarTodos(): Flow<List<ReciboEntity>> =
        recibos.asStateFlow().map { lista ->
            lista.sortedWith(compareByDescending<ReciboEntity> { it.anio }.thenByDescending { it.mes })
        }

    override suspend fun buscarPorPeriodo(anio: Int, mes: Int): ReciboEntity? =
        recibos.value.firstOrNull { it.anio == anio && it.mes == mes }

    override suspend fun guardar(recibo: ReciboEntity) {
        val actual = recibos.value.toMutableList()
        val index = actual.indexOfFirst { it.id == recibo.id }
        if (index >= 0) {
            actual[index] = recibo
        } else {
            actual.add(recibo)
        }
        recibos.value = actual
    }

    override suspend fun guardarTodos(recibos: List<ReciboEntity>) {
        val actual = this.recibos.value.toMutableList()
        for (r in recibos) {
            val idx = actual.indexOfFirst { it.id == r.id }
            if (idx >= 0) actual[idx] = r else actual.add(r)
        }
        this.recibos.value = actual
    }

    override suspend fun borrarPorId(id: String) {
        recibos.value = recibos.value.filterNot { it.id == id }
    }

    override suspend fun borrarPorPeriodo(anio: Int, mes: Int) {
        recibos.value = recibos.value.filterNot { it.anio == anio && it.mes == mes }
    }

    override suspend fun borrarTodos() {
        recibos.value = emptyList()
    }
}
