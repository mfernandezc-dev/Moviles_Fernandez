package com.tecsup.centrotareas

import com.tecsup.centrotareas.data.TareasRepositoryLocal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TareasRepositoryLocalTest {
    private val repo = TareasRepositoryLocal()

    @Test
    fun cuentaLosCincoValoresDeLaGuia() = runTest {
        val casos = mapOf(100 to 25, 1000 to 168, 10000 to 1229,
            100000 to 9592, 500000 to 41538)
        for ((hasta, esperado) in casos) {
            assertEquals("hasta=$hasta", esperado, repo.contarPrimos(hasta))
        }
    }

    @Test
    fun limitesPequenos() = runTest {
        assertEquals(0, repo.contarPrimos(1))
        assertEquals(1, repo.contarPrimos(2))
        assertEquals(2, repo.contarPrimos(3))
    }

    @Test
    fun cancelaUnCalculoGrande() = runTest {
        withContext(Dispatchers.Default) {
            val iniciado = CompletableDeferred<Unit>()
            val trabajo = launch {
                iniciado.complete(Unit)
                repo.contarPrimos(Int.MAX_VALUE)
            }
            iniciado.await()
            delay(20)
            withTimeout(2000) { trabajo.cancelAndJoin() }
            assertTrue(trabajo.isCancelled)
        }
    }
}
