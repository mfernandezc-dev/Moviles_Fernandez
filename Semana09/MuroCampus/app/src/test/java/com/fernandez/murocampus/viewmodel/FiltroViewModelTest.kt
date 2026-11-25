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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FiltroViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val todos = (1..10).flatMap { posts(it) }
    private val repo = RepositoryFalso()

    @Before fun configurar() { Dispatchers.setMain(dispatcher) }
    @After fun limpiar() {
        try { store.clear(); dispatcher.scheduler.runCurrent() }
        finally { Dispatchers.resetMain() }
    }

    @Test fun filtroInicialEsNull() = runTest(dispatcher) {
        assertNull(crear().usuarioFiltro.value)
        runCurrent()
    }

    @Test fun cargaInicialUtilizaListarUnaVez() = runTest(dispatcher) {
        val vm = crear()
        runCurrent()
        assertEquals(1, repo.llamadasLista)
        assertTrue(repo.usuarios.isEmpty())
        assertEquals(UiState.Exito(todos), vm.lista.value)
    }

    @Test fun filtrarActualizaLaSeleccionInmediatamente() = runTest(dispatcher) {
        val vm = crear()
        runCurrent()
        vm.filtrar(2)
        assertEquals(2, vm.usuarioFiltro.value)
        assertEquals(UiState.Cargando, vm.lista.value)
        runCurrent()
    }

    @Test fun filtrarDelegaAlRepositoryConUserIdDos() = runTest(dispatcher) {
        val vm = crear()
        runCurrent()
        vm.filtrar(2)
        runCurrent()
        assertEquals(listOf(2), repo.usuarios)
        assertEquals(1, repo.llamadasLista)
    }

    @Test fun conservaLasDiezPublicacionesDevueltasSinFiltradoLocal() = runTest(dispatcher) {
        val respuesta = posts(2)
        repo.usuario = { Result.success(respuesta) }
        val vm = crear()
        runCurrent()
        vm.filtrar(2)
        runCurrent()
        assertSame(respuesta, (vm.lista.value as UiState.Exito).datos)
        assertEquals(10, (vm.lista.value as UiState.Exito).datos.size)
    }

    @Test fun todosEjecutaListarYRecuperaLasCienPublicaciones() = runTest(dispatcher) {
        val vm = crear()
        runCurrent()
        vm.filtrar(2)
        runCurrent()
        vm.filtrar(null)
        runCurrent()
        assertNull(vm.usuarioFiltro.value)
        assertEquals(2, repo.llamadasLista)
        assertEquals(UiState.Exito(todos), vm.lista.value)
        assertEquals(100, (vm.lista.value as UiState.Exito).datos.size)
    }

    @Test fun permaneceCargandoDuranteLaSolicitudPendiente() = runTest(dispatcher) {
        repo.usuario = { awaitCancellation() }
        val vm = crear()
        runCurrent()
        vm.filtrar(2)
        runCurrent()
        assertEquals(UiState.Cargando, vm.lista.value)
        assertEquals(2, vm.usuarioFiltro.value)
    }

    @Test fun falloGeneraErrorSinPerderElFiltro() = runTest(dispatcher) {
        repo.usuario = { Result.failure(IOException()) }
        val vm = crear()
        runCurrent()
        vm.filtrar(3)
        runCurrent()
        assertEquals(UiState.Error("Sin conexión a internet.\nRevisa tu red e inténtalo otra vez."), vm.lista.value)
        assertEquals(3, vm.usuarioFiltro.value)
    }

    @Test fun reintentarConsultaElFiltroActivoAunqueYaEsteSeleccionado() = runTest(dispatcher) {
        repo.usuario = { Result.failure(IOException()) }
        val vm = crear()
        runCurrent()
        vm.filtrar(3)
        runCurrent()
        repo.usuario = { Result.success(posts(it)) }
        vm.cargar()
        assertEquals(UiState.Cargando, vm.lista.value)
        runCurrent()
        assertEquals(listOf(3, 3), repo.usuarios)
        assertEquals(UiState.Exito(posts(3)), vm.lista.value)
        assertEquals(3, vm.usuarioFiltro.value)
    }

    @Test fun listaVaciaEsExitoYConservaElFiltro() = runTest(dispatcher) {
        repo.usuario = { Result.success(emptyList()) }
        val vm = crear()
        runCurrent()
        vm.filtrar(4)
        runCurrent()
        assertEquals(UiState.Exito(emptyList<Post>()), vm.lista.value)
        assertEquals(4, vm.usuarioFiltro.value)
    }

    @Test fun cambiarFiltroCancelaLaSolicitudAnterior() = runTest(dispatcher) {
        var cancelada = false
        repo.usuario = { id -> if (id == 2) try { awaitCancellation() } finally { cancelada = true } else Result.success(posts(id)) }
        val vm = crear()
        runCurrent()
        vm.filtrar(2)
        runCurrent()
        vm.filtrar(5)
        runCurrent()
        assertTrue(cancelada)
        assertEquals(5, vm.usuarioFiltro.value)
        assertEquals(UiState.Exito(posts(5)), vm.lista.value)
    }

    @Test fun respuestaTardiaNoSobrescribeAlUltimoFiltro() = runTest(dispatcher) {
        val antigua = CompletableDeferred<Result<List<Post>>>()
        repo.usuario = { id -> if (id == 2) withContext(NonCancellable) { antigua.await() } else Result.success(posts(id)) }
        val vm = crear()
        runCurrent()
        vm.filtrar(2)
        runCurrent()
        vm.filtrar(5)
        runCurrent()
        antigua.complete(Result.success(posts(2)))
        runCurrent()
        assertEquals(5, vm.usuarioFiltro.value)
        assertEquals(UiState.Exito(posts(5)), vm.lista.value)
    }

    @Test fun errorTardioNoSobrescribeUnExitoNuevo() = runTest(dispatcher) {
        val antigua = CompletableDeferred<Result<List<Post>>>()
        repo.usuario = { id -> if (id == 2) withContext(NonCancellable) { antigua.await() } else Result.success(posts(id)) }
        val vm = crear()
        runCurrent()
        vm.filtrar(2)
        runCurrent()
        vm.filtrar(5)
        runCurrent()
        antigua.complete(Result.failure(IOException()))
        runCurrent()
        assertEquals(UiState.Exito(posts(5)), vm.lista.value)
    }

    @Test fun transmiteLosDiezIdentificadoresSinAlterarlos() = runTest(dispatcher) {
        val vm = crear()
        runCurrent()
        for (id in 1..10) {
            vm.filtrar(id)
            runCurrent()
            assertEquals(UiState.Exito(posts(id)), vm.lista.value)
        }
        assertEquals((1..10).toList(), repo.usuarios)
    }

    @Test fun seleccionarElMismoFiltroCargadoNoDescargaOtraVez() = runTest(dispatcher) {
        val vm = crear()
        runCurrent()
        vm.filtrar(2)
        runCurrent()
        val estado = vm.lista.value
        repeat(5) { vm.filtrar(2) }
        runCurrent()
        assertEquals(listOf(2), repo.usuarios)
        assertSame(estado, vm.lista.value)
    }

    @Test fun seleccionarElMismoFiltroPendienteNoDuplicaLaSolicitud() = runTest(dispatcher) {
        repo.usuario = { awaitCancellation() }
        val vm = crear()
        runCurrent()
        repeat(5) { vm.filtrar(2) }
        runCurrent()
        assertEquals(listOf(2), repo.usuarios)
    }

    @Test fun todosYaSeleccionadoNoRecargaLaLista() = runTest(dispatcher) {
        val vm = crear()
        runCurrent()
        repeat(5) { vm.filtrar(null) }
        runCurrent()
        assertEquals(1, repo.llamadasLista)
    }

    @Test fun abrirYCerrarDetalleConservaFiltroDatosYNumeroDeSolicitudes() = runTest(dispatcher) {
        val vm = crear()
        runCurrent()
        vm.filtrar(2)
        runCurrent()
        val estado = vm.lista.value
        vm.abrir(11)
        runCurrent()
        assertEquals(11, (vm.detalle.value as UiState.Exito).datos.post.id)
        vm.cerrarDetalle()
        assertEquals(2, vm.usuarioFiltro.value)
        assertSame(estado, vm.lista.value)
        assertEquals(listOf(2), repo.usuarios)
        assertEquals(1, repo.llamadasLista)
    }

    @Test fun cancellationExceptionLanzadaNoEsErrorVisible() = runTest(dispatcher) {
        repo.usuario = { throw CancellationException("normal") }
        val vm = crear()
        runCurrent()
        vm.filtrar(2)
        runCurrent()
        assertEquals(UiState.Cargando, vm.lista.value)
        assertEquals(2, vm.usuarioFiltro.value)
    }

    @Test fun cancellationExceptionEnFailureNoEsErrorVisible() = runTest(dispatcher) {
        repo.usuario = { Result.failure(CancellationException("normal")) }
        val vm = crear()
        runCurrent()
        vm.filtrar(2)
        runCurrent()
        assertEquals(UiState.Cargando, vm.lista.value)
    }

    @Test fun filtroPuedeReemplazarLaCargaInicialPendiente() = runTest(dispatcher) {
        var cancelada = false
        repo.lista = { try { awaitCancellation() } finally { cancelada = true } }
        val vm = crear()
        runCurrent()
        vm.filtrar(2)
        runCurrent()
        assertTrue(cancelada)
        assertEquals(UiState.Exito(posts(2)), vm.lista.value)
    }

    @Test fun cargarRespetaElFiltroDespuesDeUnExito() = runTest(dispatcher) {
        val vm = crear()
        runCurrent()
        vm.filtrar(8)
        runCurrent()
        vm.cargar()
        runCurrent()
        assertEquals(listOf(8, 8), repo.usuarios)
        assertEquals(1, repo.llamadasLista)
    }

    @Test fun cambiarFiltroNoCancelaNiModificaElDetalle() = runTest(dispatcher) {
        val vm = crear()
        runCurrent()
        vm.abrir(11)
        runCurrent()
        val detalle = vm.detalle.value
        vm.filtrar(2)
        runCurrent()
        assertSame(detalle, vm.detalle.value)
    }

    private fun crear() = PostsViewModel(repo).also { store.put("filtro", it) }
    private fun posts(userId: Int) = (1..10).map { n -> Post(userId, (userId - 1) * 10 + n, "Titulo $userId-$n", "Contenido $userId-$n") }

    private inner class RepositoryFalso : PostRepository {
        override suspend fun eliminar(id: Int): Result<Unit> = error("Llamada inesperada a eliminar")
        var llamadasLista = 0
        val usuarios = mutableListOf<Int>()
        var lista: suspend () -> Result<List<Post>> = { Result.success(todos) }
        var usuario: suspend (Int) -> Result<List<Post>> = { Result.success(posts(it)) }
        override suspend fun listar(): Result<List<Post>> { llamadasLista++; return lista() }
        override suspend fun porUsuario(userId: Int): Result<List<Post>> { usuarios += userId; return usuario(userId) }
        override suspend fun detalle(id: Int): Result<Post> = Result.success(todos.single { it.id == id })
        override suspend fun comentarios(id: Int): Result<List<Comentario>> = Result.success(emptyList())
        override suspend fun crear(nuevo: NuevoPost): Result<Post> = error("No se utiliza")
    }
}
