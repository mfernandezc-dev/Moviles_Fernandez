package com.fernandez.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.fernandez.murocampus.data.PostRepository
import com.fernandez.murocampus.model.Comentario
import com.fernandez.murocampus.model.NuevoPost
import com.fernandez.murocampus.model.Post
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class PostsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val posts = listOf(Post(1, 1, "Titulo controlado", "Contenido controlado"))
    private val repo = RepositoryFalso()

    @Before
    fun configurarMain() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun restaurarMain() {
        try {
            store.clear()
            dispatcher.scheduler.runCurrent()
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun estadoInicialEsCargandoMientrasLaSolicitudEstaPendiente() = runTest(dispatcher) {
        val pendiente = CompletableDeferred<Result<List<Post>>>()
        repo.respuesta = { pendiente.await() }
        val vm = crearVm()
        assertEquals(UiState.Cargando, vm.lista.value)
        runCurrent()
        assertEquals(1, repo.llamadas)
        assertEquals(UiState.Cargando, vm.lista.value)
    }

    @Test
    fun constructorEjecutaUnaCargaInicial() = runTest(dispatcher) {
        crearVm()
        runCurrent()
        assertEquals(1, repo.llamadas)
    }

    @Test
    fun respuestaExitosaConservaLosDatos() = runTest(dispatcher) {
        val vm = crearVm()
        runCurrent()
        assertEquals(UiState.Exito(posts), vm.lista.value)
        assertSame(posts, (vm.lista.value as UiState.Exito).datos)
    }

    @Test
    fun conservaLasCienPublicacionesCompletas() = runTest(dispatcher) {
        val cien = (1..100).map { Post((it - 1) / 10 + 1, it, "Titulo $it", "Contenido $it") }
        repo.respuesta = { Result.success(cien) }
        val vm = crearVm()
        runCurrent()
        assertEquals(UiState.Exito(cien), vm.lista.value)
        assertEquals(100, (vm.lista.value as UiState.Exito).datos.size)
    }

    @Test
    fun ioExceptionProduceMensajeDeConexion() = assertError(
        IOException("controlada"),
        "Sin conexión a internet.\nRevisa tu red e inténtalo otra vez."
    )

    @Test
    fun http404ProduceMensajeEsperado() = assertError(httpError(404), "Error del servidor (404)")

    @Test
    fun http500ProduceMensajeEsperado() = assertError(httpError(500), "Error del servidor (500)")

    @Test
    fun serializacionProduceRespuestaInesperada() = assertError(
        SerializationException("controlada"), "Respuesta inesperada"
    )

    @Test
    fun otraExcepcionProduceErrorDesconocido() = assertError(
        IllegalStateException("controlada"), "Error desconocido"
    )

    @Test
    fun reintentarPasaDeErrorACargandoYDespuesAExito() = runTest(dispatcher) {
        repo.respuesta = { Result.failure(IOException()) }
        val vm = crearVm()
        runCurrent()
        assertTrue(vm.lista.value is UiState.Error)
        val pendiente = CompletableDeferred<Result<List<Post>>>()
        repo.respuesta = { pendiente.await() }
        vm.cargar()
        runCurrent()
        assertEquals(UiState.Cargando, vm.lista.value)
        assertEquals(2, repo.llamadas)
        pendiente.complete(Result.success(posts))
        runCurrent()
        assertEquals(UiState.Exito(posts), vm.lista.value)
    }

    @Test
    fun observarStateFlowNoRealizaSolicitudesAdicionales() = runTest(dispatcher) {
        val vm = crearVm()
        val observados = mutableListOf<UiState<List<Post>>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.lista.collect { observados += it }
        }
        runCurrent()
        assertEquals(listOf(UiState.Cargando, UiState.Exito(posts)), observados)
        assertEquals(1, repo.llamadas)
    }

    @Test
    fun cargarRepetidamenteNoDuplicaUnaSolicitudActiva() = runTest(dispatcher) {
        val pendiente = CompletableDeferred<Result<List<Post>>>()
        repo.respuesta = { pendiente.await() }
        val vm = crearVm()
        runCurrent()
        repeat(5) { vm.cargar() }
        runCurrent()
        assertEquals(1, repo.llamadas)
        pendiente.complete(Result.success(posts))
        runCurrent()
        assertEquals(UiState.Exito(posts), vm.lista.value)
    }

    @Test
    fun listaVaciaEsExitoLegitimo() = runTest(dispatcher) {
        repo.respuesta = { Result.success(emptyList()) }
        val vm = crearVm()
        runCurrent()
        assertEquals(UiState.Exito(emptyList<Post>()), vm.lista.value)
    }

    @Test
    fun limpiarViewModelCancelaLaSolicitudSinMostrarError() = runTest(dispatcher) {
        var cancelada = false
        repo.respuesta = {
            try {
                awaitCancellation()
            } finally {
                cancelada = true
            }
        }
        val vm = crearVm()
        runCurrent()
        store.clear()
        runCurrent()
        assertTrue(cancelada)
        assertEquals(UiState.Cargando, vm.lista.value)
    }

    @Test
    fun cancellationExceptionLanzadaNoEsErrorVisible() = runTest(dispatcher) {
        repo.respuesta = { throw CancellationException("controlada") }
        val vm = crearVm()
        runCurrent()
        assertEquals(UiState.Cargando, vm.lista.value)
    }

    @Test
    fun cancellationExceptionEnFailureNoEsErrorVisible() = runTest(dispatcher) {
        repo.respuesta = { Result.failure(CancellationException("controlada")) }
        val vm = crearVm()
        runCurrent()
        assertEquals(UiState.Cargando, vm.lista.value)
    }

    @Test
    fun nuevaCargaTrasExitoVuelveACargando() = runTest(dispatcher) {
        val vm = crearVm()
        runCurrent()
        val pendiente = CompletableDeferred<Result<List<Post>>>()
        repo.respuesta = { pendiente.await() }
        vm.cargar()
        runCurrent()
        assertEquals(UiState.Cargando, vm.lista.value)
        pendiente.complete(Result.success(emptyList()))
        runCurrent()
        assertEquals(UiState.Exito(emptyList<Post>()), vm.lista.value)
    }

    @Test
    fun factoryCreaElViewModelSolicitado() = runTest(dispatcher) {
        val vm = PostsViewModelFactory(repo).create(PostsViewModel::class.java)
        store.put("factory", vm)
        runCurrent()
        assertEquals(UiState.Exito(posts), vm.lista.value)
    }

    @Test
    fun factoryRechazaUnViewModelDesconocido() {
        val error = assertThrows(IllegalArgumentException::class.java) {
            PostsViewModelFactory(repo).create(OtroViewModel::class.java)
        }
        assertEquals("ViewModel desconocido", error.message)
    }

    @Test
    fun providerReutilizaElViewModelDelMismoStore() = runTest(dispatcher) {
        val primero = crearVm()
        runCurrent()
        val segundo = crearVm()
        runCurrent()
        assertSame(primero, segundo)
        assertEquals(1, repo.llamadas)
    }

    private fun crearVm(): PostsViewModel =
        ViewModelProvider(store, PostsViewModelFactory(repo))[PostsViewModel::class.java]

    private fun assertError(error: Throwable, mensaje: String) = runTest(dispatcher) {
        repo.respuesta = { Result.failure(error) }
        val vm = crearVm()
        runCurrent()
        assertEquals(UiState.Error(mensaje), vm.lista.value)
    }

    private fun httpError(codigo: Int) = HttpException(
        Response.error<Post>(codigo, "{}".toResponseBody("application/json".toMediaType()))
    )

    private class OtroViewModel : ViewModel()

    private inner class RepositoryFalso : PostRepository {
        var llamadas = 0
        var respuesta: suspend () -> Result<List<Post>> = { Result.success(posts) }

        override suspend fun listar(): Result<List<Post>> {
            llamadas++
            return respuesta()
        }

        override suspend fun detalle(id: Int): Result<Post> = error("Operacion no utilizada")
        override suspend fun comentarios(id: Int): Result<List<Comentario>> = error("Operacion no utilizada")
        override suspend fun porUsuario(userId: Int): Result<List<Post>> = error("Operacion no utilizada")
        override suspend fun crear(nuevo: NuevoPost): Result<Post> = error("Operacion no utilizada")
    }
}
