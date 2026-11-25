package com.fernandez.murocampus.viewmodel

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
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class DetalleViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val post = Post(1, 7, "Titulo completo", "Primera\nSegunda\nTercera")
    private val comentarios = listOf(Comentario(7, 31, "Nombre", "correo@test.com", "Texto completo\ndel comentario"))
    private val repo = RepositoryFalso()

    @Before fun configurar() { Dispatchers.setMain(dispatcher) }
    @After fun limpiar() {
        try {
            store.clear()
            dispatcher.scheduler.runCurrent()
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test fun cargandoMientrasAmbasSolicitudesEstanPendientes() = runTest(dispatcher) {
        repo.post = { awaitCancellation() }
        repo.comentarios = { awaitCancellation() }
        val vm = crear()
        vm.abrir(7)
        assertEquals(UiState.Cargando, vm.detalle.value)
        runCurrent()
        assertEquals(listOf(7), repo.idsPost)
        assertEquals(listOf(7), repo.idsComentarios)
        assertEquals(UiState.Cargando, vm.detalle.value)
    }

    @Test fun solicitaLaPublicacionConSuIdReal() = runTest(dispatcher) {
        val vm = crear()
        vm.abrir(42)
        runCurrent()
        assertEquals(listOf(42), repo.idsPost)
    }

    @Test fun solicitaLosComentariosConElMismoId() = runTest(dispatcher) {
        val vm = crear()
        vm.abrir(42)
        runCurrent()
        assertEquals(listOf(42), repo.idsComentarios)
    }

    @Test fun exitoConservaPublicacionYComentariosCompletos() = runTest(dispatcher) {
        val vm = crear()
        vm.abrir(7)
        runCurrent()
        val datos = (vm.detalle.value as UiState.Exito).datos
        assertEquals(DetalleUi(post, comentarios), datos)
        assertSame(post, datos.post)
        assertSame(comentarios, datos.comentarios)
    }

    @Test fun esperaLosComentariosAntesDePublicarExito() = runTest(dispatcher) {
        val pendiente = CompletableDeferred<Result<List<Comentario>>>()
        repo.comentarios = { pendiente.await() }
        val vm = crear()
        vm.abrir(7)
        runCurrent()
        assertEquals(UiState.Cargando, vm.detalle.value)
        pendiente.complete(Result.success(comentarios))
        runCurrent()
        assertEquals(UiState.Exito(DetalleUi(post, comentarios)), vm.detalle.value)
    }

    @Test fun comentariosVaciosSonUnExitoValido() = runTest(dispatcher) {
        repo.comentarios = { Result.success(emptyList()) }
        val vm = crear()
        vm.abrir(7)
        runCurrent()
        assertEquals(UiState.Exito(DetalleUi(post, emptyList())), vm.detalle.value)
    }

    @Test fun falloDePublicacionProduceError() = errorEsperado(IllegalStateException(), "Error desconocido")
    @Test fun falloDeComentariosProduceError() = errorEsperado(IOException(), "Sin conexión a internet.\nRevisa tu red e inténtalo otra vez.", true)
    @Test fun http404ProduceMensajeDelServidor() = errorEsperado(
        HttpException(Response.error<Post>(404, "{}".toResponseBody("application/json".toMediaType()))),
        "Error del servidor (404)"
    )
    @Test fun conexionProduceMensajeCentralizado() = errorEsperado(IOException(), "Sin conexión a internet.\nRevisa tu red e inténtalo otra vez.")
    @Test fun serializacionProduceRespuestaInesperada() = errorEsperado(SerializationException(), "Respuesta inesperada")

    @Test fun reintentarConsultaOtraVezElDetalleSinRecargarLista() = runTest(dispatcher) {
        repo.post = { Result.failure(IOException()) }
        val vm = crear()
        vm.abrir(7)
        runCurrent()
        assertTrue(vm.detalle.value is UiState.Error)
        repo.post = { Result.success(post) }
        vm.abrir(7)
        assertEquals(UiState.Cargando, vm.detalle.value)
        runCurrent()
        assertEquals(UiState.Exito(DetalleUi(post, comentarios)), vm.detalle.value)
        assertEquals(listOf(7, 7), repo.idsPost)
        assertEquals(1, repo.llamadasLista)
    }

    @Test fun cancelacionLanzadaNoSeConvierteEnError() = runTest(dispatcher) {
        repo.post = { throw CancellationException("normal") }
        val vm = crear()
        vm.abrir(7)
        runCurrent()
        assertEquals(UiState.Cargando, vm.detalle.value)
    }

    @Test fun cancelacionEnResultNoSeConvierteEnError() = runTest(dispatcher) {
        repo.comentarios = { Result.failure(CancellationException("normal")) }
        val vm = crear()
        vm.abrir(7)
        runCurrent()
        assertEquals(UiState.Cargando, vm.detalle.value)
    }

    @Test fun cerrarCancelaAmbasSolicitudesPendientes() = runTest(dispatcher) {
        var postCancelado = false
        var comentariosCancelados = false
        repo.post = { try { awaitCancellation() } finally { postCancelado = true } }
        repo.comentarios = { try { awaitCancellation() } finally { comentariosCancelados = true } }
        val vm = crear()
        vm.abrir(7)
        runCurrent()
        vm.cerrarDetalle()
        runCurrent()
        assertTrue(postCancelado)
        assertTrue(comentariosCancelados)
        assertEquals(UiState.Cargando, vm.detalle.value)
    }

    @Test fun seleccionRapidaCancelaLaCargaAnterior() = runTest(dispatcher) {
        var cancelada = false
        repo.post = { id ->
            if (id == 1) try { awaitCancellation() } finally { cancelada = true }
            else Result.success(post.copy(id = id))
        }
        val vm = crear()
        vm.abrir(1)
        runCurrent()
        vm.abrir(2)
        runCurrent()
        assertTrue(cancelada)
        assertEquals(2, (vm.detalle.value as UiState.Exito).datos.post.id)
    }

    @Test fun respuestaTardiaNoSobrescribeLaSeleccionNueva() = runTest(dispatcher) {
        val antigua = CompletableDeferred<Result<Post>>()
        repo.post = { id -> if (id == 1) withContext(NonCancellable) { antigua.await() } else Result.success(post.copy(id = id)) }
        val vm = crear()
        vm.abrir(1)
        runCurrent()
        vm.abrir(2)
        runCurrent()
        assertEquals(2, (vm.detalle.value as UiState.Exito).datos.post.id)
        antigua.complete(Result.success(post.copy(id = 1)))
        runCurrent()
        assertEquals(2, (vm.detalle.value as UiState.Exito).datos.post.id)
    }

    @Test fun abrirYCerrarConservaLaMismaListaSinNuevasSolicitudes() = runTest(dispatcher) {
        val vm = crear()
        runCurrent()
        val estadoLista = vm.lista.value
        vm.abrir(7)
        runCurrent()
        vm.cerrarDetalle()
        runCurrent()
        assertSame(estadoLista, vm.lista.value)
        assertEquals(1, repo.llamadasLista)
    }

    @Test fun cerrarDetalleNoCancelaUnaCargaDeListaPendiente() = runTest(dispatcher) {
        val lista = CompletableDeferred<Result<List<Post>>>()
        repo.lista = { lista.await() }
        val vm = crear()
        vm.abrir(7)
        runCurrent()
        vm.cerrarDetalle()
        lista.complete(Result.success(listOf(post)))
        runCurrent()
        assertEquals(UiState.Exito(listOf(post)), vm.lista.value)
    }

    @Test fun limpiarViewModelCancelaElDetalle() = runTest(dispatcher) {
        var cancelada = false
        repo.post = { try { awaitCancellation() } finally { cancelada = true } }
        val vm = crear()
        vm.abrir(7)
        runCurrent()
        store.clear()
        runCurrent()
        assertTrue(cancelada)
        assertEquals(UiState.Cargando, vm.detalle.value)
    }

    @Test fun falloDePublicacionCancelaLosComentariosPendientes() = runTest(dispatcher) {
        val pendiente = CompletableDeferred<Result<Post>>()
        var comentariosCancelados = false
        repo.post = { pendiente.await() }
        repo.comentarios = { try { awaitCancellation() } finally { comentariosCancelados = true } }
        val vm = crear()
        vm.abrir(7)
        runCurrent()
        pendiente.complete(Result.failure(IOException()))
        runCurrent()
        assertTrue(comentariosCancelados)
        assertTrue(vm.detalle.value is UiState.Error)
    }

    @Test fun cerrarImpidePublicarUnaRespuestaTardiaNoCooperativa() = runTest(dispatcher) {
        val pendiente = CompletableDeferred<Result<Post>>()
        repo.post = { withContext(NonCancellable) { pendiente.await() } }
        val vm = crear()
        vm.abrir(7)
        runCurrent()
        vm.cerrarDetalle()
        pendiente.complete(Result.success(post))
        runCurrent()
        assertEquals(UiState.Cargando, vm.detalle.value)
        assertEquals(1, repo.llamadasLista)
    }

    private fun crear() = PostsViewModel(repo).also { store.put("detalle", it) }

    private fun errorEsperado(error: Exception, mensaje: String, fallaComentarios: Boolean = false) = runTest(dispatcher) {
        if (fallaComentarios) repo.comentarios = { Result.failure(error) }
        else repo.post = { Result.failure(error) }
        val vm = crear()
        vm.abrir(7)
        runCurrent()
        assertEquals(UiState.Error(mensaje), vm.detalle.value)
    }

    private inner class RepositoryFalso : PostRepository {
        var llamadasLista = 0
        val idsPost = mutableListOf<Int>()
        val idsComentarios = mutableListOf<Int>()
        var lista: suspend () -> Result<List<Post>> = { Result.success(listOf(this@DetalleViewModelTest.post)) }
        var post: suspend (Int) -> Result<Post> = { Result.success(this@DetalleViewModelTest.post) }
        var comentarios: suspend (Int) -> Result<List<Comentario>> = { Result.success(this@DetalleViewModelTest.comentarios) }
        override suspend fun listar(): Result<List<Post>> { llamadasLista++; return lista() }
        override suspend fun detalle(id: Int): Result<Post> { idsPost += id; return post(id) }
        override suspend fun comentarios(id: Int): Result<List<Comentario>> { idsComentarios += id; return comentarios.invoke(id) }
        override suspend fun porUsuario(userId: Int): Result<List<Post>> = error("No se utiliza")
        override suspend fun crear(nuevo: NuevoPost): Result<Post> = error("No se utiliza")
    }
}
