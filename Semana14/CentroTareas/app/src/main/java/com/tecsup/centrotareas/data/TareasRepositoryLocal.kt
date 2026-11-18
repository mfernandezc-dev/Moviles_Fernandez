package com.tecsup.centrotareas.data

import com.tecsup.centrotareas.model.AvancePrimos
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.last

class TareasRepositoryLocal : TareasRepository {
    override fun progresoTarea(): Flow<Float> = flow {
        emit(0f)
        for (i in 1..100) {
            delay(40)
            emit(i / 100f)
        }
    }.flowOn(Dispatchers.Default)

    override suspend fun contarPrimos(hasta: Int): Int =
        contarPrimosConProgreso(hasta).last().cantidad

    override fun contarPrimosConProgreso(hasta: Int): Flow<AvancePrimos> = flow {
        if (hasta < 2) {
            emit(AvancePrimos(1f, 0))
            return@flow
        }
        var cantidad = 0
        var ultimoPorcentaje = 0
        emit(AvancePrimos(0f, 0))
        for (n in 2..hasta) {
            currentCoroutineContext().ensureActive()
            if (esPrimo(n)) cantidad++
            // Como máximo una publicación por porcentaje, sin llenar el canal del Flow.
            val porcentaje = ((n.toLong() - 1) * 100 / (hasta.toLong() - 1)).toInt()
            if (porcentaje > ultimoPorcentaje) {
                emit(AvancePrimos(porcentaje / 100f, cantidad))
                ultimoPorcentaje = porcentaje
            }
        }
    }.flowOn(Dispatchers.Default)

    private fun esPrimo(n: Int): Boolean {
        var d = 2
        while (d.toLong() * d <= n) {
            if (n % d == 0) return false
            d++
        }
        return true
    }

    override suspend fun cargarPerfil(): String {
        delay(2000)
        return "Ana Torres (ficticia)"
    }

    override suspend fun cargarPedidos(simularError: Boolean): List<String> {
        delay(3000)
        if (simularError) throw IOException("Sin conexión")
        return listOf("Pedido 101", "Pedido 102", "Pedido 103")
    }
}
