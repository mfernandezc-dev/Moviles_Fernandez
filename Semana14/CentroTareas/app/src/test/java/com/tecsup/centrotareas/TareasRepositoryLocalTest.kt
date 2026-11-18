package com.tecsup.centrotareas

import com.tecsup.centrotareas.data.TareasRepositoryLocal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TareasRepositoryLocalTest {
    private val repo = TareasRepositoryLocal()

    @Test
    fun progresoCompletoOrdenadoDesdeRepository() = runTest {
        val avances = repo.progresoTarea().toList()
        assertEquals(101, avances.size)
        assertEquals(0f, avances.first())
        assertEquals(1f, avances.last())
        assertTrue(avances.zipWithNext().all { (anterior, siguiente) -> siguiente >= anterior })
    }

    @Test
    fun cancelarFlujoNoAfectaElSiguienteInicio() = runTest {
        val primera = repo.progresoTarea().take(6).toList()
        val reinicio = repo.progresoTarea().take(2).toList()
        assertTrue(primera.last() < 1f)
        assertEquals(listOf(0f, 0.01f), reinicio)
    }

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
    fun primosEmiteAvanceAcotadoYResultadoCorrecto() = runTest {
        val avances = repo.contarPrimosConProgreso(500000).toList()
        assertEquals(101, avances.size)
        assertEquals(0f, avances.first().progreso)
        assertEquals(1f, avances.last().progreso)
        assertEquals(41538, avances.last().cantidad)
        assertTrue(avances.all { it.progreso in 0f..1f })
        assertTrue(avances.zipWithNext().all { (a, b) ->
            b.progreso > a.progreso && b.cantidad >= a.cantidad
        })
    }

    @Test
    fun cancelarFlujoDePrimosPermiteReiniciar() = runTest {
        val parcial = repo.contarPrimosConProgreso(500000).take(5).toList()
        assertTrue(parcial.last().progreso < 1f)
        val reinicio = repo.contarPrimosConProgreso(100).toList()
        assertEquals(0f, reinicio.first().progreso)
        assertEquals(25, reinicio.last().cantidad)
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
