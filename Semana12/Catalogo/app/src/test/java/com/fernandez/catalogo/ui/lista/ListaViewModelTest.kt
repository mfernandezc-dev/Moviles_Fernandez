package com.fernandez.catalogo.ui.lista

import androidx.lifecycle.ViewModelStore
import com.fernandez.catalogo.domain.model.Item
import com.fernandez.catalogo.testing.FakeItemRepository
import com.fernandez.catalogo.testing.FakeNotificador
import com.fernandez.catalogo.testing.MainDispatcherRule
import com.fernandez.catalogo.ui.UiState
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListaViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val viewModelStore = ViewModelStore()

    @After
    fun liberarViewModel() {
        // Cancela viewModelScope y los StateFlow antes de restablecer Dispatchers.Main.
        viewModelStore.clear()
    }

    @Test
    fun sincronizacionExitosa_refrescaYExponeLosProductosEsperados() =
        runTest(mainDispatcherRule.dispatcher) {
            val esperados = listOf(producto(1), producto(2, favorito = true))
            val repository = FakeItemRepository(
                resultadoRefrescar = Result.success(esperados.size),
                productosTrasRefrescar = esperados
            )
            val notificador = FakeNotificador()
            val viewModel = crearViewModel(repository, notificador)

            assertEquals(UiState.Cargando, viewModel.estado.value)
            advanceUntilIdle()

            assertEquals(1, repository.llamadasRefrescar)
            assertEquals(UiState.Exito(esperados), viewModel.estado.value)
            assertEquals(esperados.size, repository.cantidadGuardada())
            assertFalse(viewModel.actualizacionFallida.value)
            // La sincronización actual no solicita notificaciones automáticas.
            assertTrue(notificador.notificaciones.isEmpty())
        }

    @Test
    fun errorSinDatosGuardados_exponeErrorDeConexion() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeItemRepository(
            resultadoRefrescar = Result.failure(IOException("Sin conexión simulada"))
        )
        val notificador = FakeNotificador()
        val viewModel = crearViewModel(repository, notificador)

        advanceUntilIdle()

        assertEquals(1, repository.llamadasRefrescar)
        assertEquals(0, repository.cantidadGuardada())
        assertEquals(
            UiState.Error("No se pudieron cargar las laptops. Comprueba tu conexión y vuelve a intentarlo."),
            viewModel.estado.value
        )
        assertTrue(viewModel.actualizacionFallida.value)
        assertTrue(notificador.notificaciones.isEmpty())
    }

    @Test
    fun errorConDatosLocales_conservaProductosYActivaAvisoDeActualizacion() =
        runTest(mainDispatcherRule.dispatcher) {
            val locales = listOf(producto(10, favorito = true), producto(20))
            val repository = FakeItemRepository(
                productosIniciales = locales,
                resultadoRefrescar = Result.failure(IOException("Sin conexión simulada"))
            )
            val notificador = FakeNotificador()
            val viewModel = crearViewModel(repository, notificador)

            advanceUntilIdle()

            assertEquals(1, repository.llamadasRefrescar)
            assertEquals(UiState.Exito(locales), viewModel.estado.value)
            assertTrue(viewModel.actualizacionFallida.value)
            assertEquals(locales.size, repository.cantidadGuardada())
            assertEquals(1, repository.cantidadFavoritos())
            assertEquals(listOf(locales.first()), repository.favoritos.first())
            assertTrue(notificador.notificaciones.isEmpty())
        }

    private fun crearViewModel(
        repository: FakeItemRepository,
        notificador: FakeNotificador
    ): ListaViewModel = ListaViewModel(repository, notificador).also {
        viewModelStore.put("lista", it)
    }

    private fun producto(id: Int, favorito: Boolean = false): Item = Item(
        id = id,
        titulo = "Laptop de prueba $id",
        subtitulo = "Producto sintético en memoria",
        imagenUrl = null,
        precio = 1000.0 + id,
        esFavorito = favorito
    )
}
