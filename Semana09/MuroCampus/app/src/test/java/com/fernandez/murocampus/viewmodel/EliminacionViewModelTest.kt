package com.fernandez.murocampus.viewmodel

import androidx.lifecycle.ViewModelStore
import com.fernandez.murocampus.data.PostRepository
import com.fernandez.murocampus.model.*
import java.io.IOException
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EliminacionViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val posts = listOf(Post(2, 1, "Qui uno", "Texto uno"), Post(2, 2, "Qui dos", "Texto dos"))
    private val repo = Falso()
    @Before fun configurar() { Dispatchers.setMain(dispatcher) }
    @After fun limpiar() { try { store.clear(); dispatcher.scheduler.runCurrent() } finally { Dispatchers.resetMain() } }
    private fun vm() = PostsViewModel(repo).also { store.put("eliminar", it); it.abrir(1) }
    private fun datos(vm: PostsViewModel) = (vm.lista.value as UiState.Exito).datos

    @Test fun inicialmenteNoHayOperacion() = runTest(dispatcher) { val vm = vm(); runCurrent(); assertNull(vm.eliminacion.value) }
    @Test fun noEliminaAntesDeRecibirExito() = runTest(dispatcher) {
        repo.respuesta = { awaitCancellation() }; val vm = vm(); runCurrent(); vm.eliminar(1); runCurrent()
        assertEquals(UiState.Cargando, vm.eliminacion.value); assertEquals(posts, datos(vm))
    }
    @Test fun exitoDelegaIdYRetiraSoloElPost() = runTest(dispatcher) {
        val vm = vm(); runCurrent(); vm.eliminar(1); runCurrent()
        assertEquals(listOf(1), repo.ids); assertEquals(UiState.Exito(Unit), vm.eliminacion.value)
        assertEquals(listOf(posts[1]), datos(vm))
    }
    @Test fun falloConservaDetalleYPublicacion() = runTest(dispatcher) {
        repo.respuesta = { Result.failure(IOException()) }; val vm = vm(); runCurrent(); val detalle = vm.detalle.value
        vm.eliminar(1); runCurrent(); assertTrue(vm.eliminacion.value is UiState.Error)
        assertSame(detalle, vm.detalle.value); assertEquals(posts, datos(vm))
    }
    @Test fun permiteReintentoVoluntarioTrasError() = runTest(dispatcher) {
        repo.respuesta = { Result.failure(IOException()) }; val vm = vm(); runCurrent(); vm.eliminar(1); runCurrent()
        repo.respuesta = { Result.success(Unit) }; vm.eliminar(1); runCurrent()
        assertEquals(listOf(1, 1), repo.ids); assertEquals(UiState.Exito(Unit), vm.eliminacion.value)
    }
    @Test fun evitaSolicitudesSimultaneas() = runTest(dispatcher) {
        repo.respuesta = { awaitCancellation() }; val vm = vm(); runCurrent()
        repeat(5) { vm.eliminar(1) }; runCurrent(); assertEquals(listOf(1), repo.ids)
    }
    @Test fun noRepiteIdYaEliminado() = runTest(dispatcher) {
        val vm = vm(); runCurrent(); vm.eliminar(1); runCurrent(); vm.eliminar(1); runCurrent(); assertEquals(listOf(1), repo.ids)
    }
    @Test fun conservaBusquedaYFiltroYExclusionAlRecargar() = runTest(dispatcher) {
        val vm = vm(); runCurrent(); vm.filtrar(2); vm.buscar("qui"); runCurrent(); vm.eliminar(1); runCurrent()
        vm.cargar(); runCurrent(); assertEquals(listOf(posts[1]), datos(vm)); assertEquals("qui", vm.busqueda.value)
        assertEquals(2, vm.usuarioFiltro.value); assertEquals(UiState.Exito(listOf(posts[1])), vm.resultados.value)
        vm.filtrar(null); runCurrent(); assertEquals(listOf(posts[1]), datos(vm))
    }
    @Test fun resultadoDeListaPendienteTambienExcluyeEliminados() = runTest(dispatcher) {
        val vm = vm(); runCurrent(); val espera = CompletableDeferred<Result<List<Post>>>()
        repo.lista = { espera.await() }; vm.cargar(); runCurrent(); vm.eliminar(1); runCurrent()
        espera.complete(Result.success(posts)); runCurrent(); assertEquals(listOf(posts[1]), datos(vm))
    }
    @Test fun cancelacionLanzadaNoEsError() = runTest(dispatcher) {
        repo.respuesta = { throw CancellationException() }; val vm = vm(); runCurrent(); vm.eliminar(1); runCurrent()
        assertEquals(UiState.Cargando, vm.eliminacion.value); assertEquals(posts, datos(vm))
    }
    @Test fun cancelacionEnFailureNoEsError() = runTest(dispatcher) {
        repo.respuesta = { Result.failure(CancellationException()) }; val vm = vm(); runCurrent(); vm.eliminar(1); runCurrent()
        assertEquals(UiState.Cargando, vm.eliminacion.value); assertEquals(posts, datos(vm))
    }
    @Test fun cerrarDetalleCancelaOperacion() = runTest(dispatcher) {
        var cancelada = false; repo.respuesta = { try { awaitCancellation() } finally { cancelada = true } }
        val vm = vm(); runCurrent(); vm.eliminar(1); runCurrent(); vm.cerrarDetalle(); runCurrent(); assertTrue(cancelada)
    }
    @Test fun respuestaObsoletaNoModificaNuevaPantalla() = runTest(dispatcher) {
        val espera = CompletableDeferred<Result<Unit>>(); repo.respuesta = { withContext(NonCancellable) { espera.await() } }
        val vm = vm(); runCurrent(); vm.eliminar(1); runCurrent(); vm.abrir(2); runCurrent()
        espera.complete(Result.success(Unit)); runCurrent(); assertNull(vm.eliminacion.value); assertEquals(posts, datos(vm))
        assertEquals(2, (vm.detalle.value as UiState.Exito).datos.post.id)
    }
    @Test fun idDistintoAlDetalleNoEnviaSolicitud() = runTest(dispatcher) {
        val vm = vm(); runCurrent(); vm.eliminar(2); runCurrent(); assertTrue(repo.ids.isEmpty())
    }
    private inner class Falso : PostRepository {
        override suspend fun editar(id: Int, cambios: EditarPost): Result<Post> = error("Llamada inesperada a editar")
        val ids = mutableListOf<Int>()
        var respuesta: suspend () -> Result<Unit> = { Result.success(Unit) }
        var lista: suspend () -> Result<List<Post>> = { Result.success(posts) }
        override suspend fun listar() = lista()
        override suspend fun porUsuario(userId: Int) = lista()
        override suspend fun detalle(id: Int) = Result.success(posts.single { it.id == id })
        override suspend fun comentarios(id: Int): Result<List<Comentario>> = Result.success(emptyList())
        override suspend fun crear(nuevo: NuevoPost): Result<Post> = error("Llamada inesperada a crear")
        override suspend fun eliminar(id: Int): Result<Unit> { ids += id; return respuesta() }
    }
}
