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
class EdicionViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val original = Post(2, 12, "Original", "Contenido original")
    private val otro = Post(2, 13, "Otro", "Otro contenido")
    private val comentario = Comentario(12, 1, "Nombre", "email@test", "Comentario")
    private val repo = Falso()
    @Before fun configurar() { Dispatchers.setMain(dispatcher) }
    @After fun limpiar() { try { store.clear(); dispatcher.scheduler.runCurrent() } finally { Dispatchers.resetMain() } }
    private fun vm() = PostsViewModel(repo).also { store.put("editar", it); it.abrir(12); it.prepararEdicion() }
    private fun detalle(vm: PostsViewModel) = (vm.detalle.value as UiState.Exito).datos
    private fun lista(vm: PostsViewModel) = (vm.lista.value as UiState.Exito).datos

    @Test fun estadoInicialNulo() = runTest(dispatcher) { val vm = vm(); runCurrent(); assertNull(vm.edicion.value) }
    @Test fun rechazaTituloVacioOEspacios() = runTest(dispatcher) {
        val vm = vm(); runCurrent(); vm.editar(12, ""); vm.editar(12, "   "); runCurrent(); assertTrue(repo.cambios.isEmpty()); assertNull(vm.edicion.value)
    }
    @Test fun rechazaTituloSinCambiosYRecortadoIgual() = runTest(dispatcher) {
        val vm = vm(); runCurrent(); vm.editar(12, "Original"); vm.editar(12, " Original "); runCurrent(); assertTrue(repo.cambios.isEmpty())
    }
    @Test fun delegaIdYTituloRecortado() = runTest(dispatcher) {
        val vm = vm(); runCurrent(); vm.editar(12, " Nuevo "); runCurrent(); assertEquals(listOf(12 to EditarPost("Nuevo")), repo.cambios)
    }
    @Test fun cargandoNoActualizaAntesDeRespuesta() = runTest(dispatcher) {
        repo.respuesta = { awaitCancellation() }; val vm = vm(); runCurrent(); vm.editar(12, "Nuevo"); runCurrent()
        assertEquals(UiState.Cargando, vm.edicion.value); assertEquals(original, detalle(vm).post)
    }
    @Test fun usaObjetoDevueltoNoElTituloEnviado() = runTest(dispatcher) {
        val devuelto = original.copy(title = "Normalizado por servidor")
        repo.respuesta = { Result.success(devuelto) }; val vm = vm(); runCurrent(); vm.editar(12, "Enviado"); runCurrent()
        assertSame(devuelto, (vm.edicion.value as UiState.Exito).datos); assertSame(devuelto, detalle(vm).post)
        assertSame(devuelto, lista(vm).first()); assertEquals(otro, lista(vm).last())
    }
    @Test fun conservaIdAutorContenidoYComentarios() = runTest(dispatcher) {
        val vm = vm(); runCurrent(); val comentarios = detalle(vm).comentarios; vm.editar(12, "Nuevo"); runCurrent()
        assertEquals(original.copy(title = "Nuevo"), detalle(vm).post); assertSame(comentarios, detalle(vm).comentarios)
    }
    @Test fun falloConservaDatosYEsErrorIndependiente() = runTest(dispatcher) {
        repo.respuesta = { Result.failure(IOException()) }; val vm = vm(); runCurrent(); vm.editar(12, "Nuevo"); runCurrent()
        assertTrue(vm.edicion.value is UiState.Error); assertEquals(original, detalle(vm).post); assertEquals(listOf(original, otro), lista(vm))
        assertNull(vm.eliminacion.value); assertNull(vm.creacion.value)
    }
    @Test fun errorPermiteReintentoVoluntario() = runTest(dispatcher) {
        repo.respuesta = { Result.failure(IOException()) }; val vm = vm(); runCurrent(); vm.editar(12, "Nuevo"); runCurrent()
        repo.respuesta = { Result.success(original.copy(title = it.title)) }; vm.editar(12, "Nuevo"); runCurrent()
        assertEquals(2, repo.cambios.size); assertTrue(vm.edicion.value is UiState.Exito)
    }
    @Test fun evitaEnviosSimultaneos() = runTest(dispatcher) {
        repo.respuesta = { awaitCancellation() }; val vm = vm(); runCurrent(); repeat(5) { vm.editar(12, "Nuevo") }; runCurrent(); assertEquals(1, repo.cambios.size)
    }
    @Test fun cancelacionLanzadaNoSeConvierteEnError() = runTest(dispatcher) {
        repo.respuesta = { throw CancellationException() }; val vm = vm(); runCurrent(); vm.editar(12, "Nuevo"); runCurrent(); assertEquals(UiState.Cargando, vm.edicion.value)
    }
    @Test fun cancelacionEnFailureNoSeConvierteEnError() = runTest(dispatcher) {
        repo.respuesta = { Result.failure(CancellationException()) }; val vm = vm(); runCurrent(); vm.editar(12, "Nuevo"); runCurrent(); assertEquals(UiState.Cargando, vm.edicion.value)
    }
    @Test fun cerrarEdicionCancelaSolicitud() = runTest(dispatcher) {
        var cancelada = false; repo.respuesta = { try { awaitCancellation() } finally { cancelada = true } }
        val vm = vm(); runCurrent(); vm.editar(12, "Nuevo"); runCurrent(); vm.cerrarEdicion(); runCurrent(); assertTrue(cancelada)
    }
    @Test fun respuestaTardiaNoModificaOtroDetalle() = runTest(dispatcher) {
        val espera = CompletableDeferred<Result<Post>>(); repo.respuesta = { withContext(NonCancellable) { espera.await() } }
        val vm = vm(); runCurrent(); vm.editar(12, "Nuevo"); runCurrent(); vm.abrir(13); runCurrent()
        espera.complete(Result.success(original.copy(title = "Tardío"))); runCurrent()
        assertEquals(otro, detalle(vm).post); assertEquals(listOf(original, otro), lista(vm))
    }
    @Test fun nuevoTituloSeAplicaABusquedaSinGet() = runTest(dispatcher) {
        val vm = vm(); runCurrent(); vm.buscar("Nuevo"); runCurrent(); vm.editar(12, "Nuevo"); runCurrent()
        assertEquals(UiState.Exito(listOf(original.copy(title = "Nuevo"))), vm.resultados.value); assertEquals(1, repo.getLista)
    }
    @Test fun conservaFiltroConsultaYActualizacionTrasRecarga() = runTest(dispatcher) {
        val vm = vm(); runCurrent(); vm.filtrar(2); vm.buscar("Nuevo"); runCurrent(); vm.editar(12, "Nuevo"); runCurrent()
        vm.cargar(); runCurrent(); assertEquals("Nuevo", lista(vm).first().title); assertEquals("Nuevo", vm.busqueda.value); assertEquals(2, vm.usuarioFiltro.value)
        vm.filtrar(null); runCurrent(); assertEquals("Nuevo", lista(vm).first().title)
    }
    @Test fun nuevoDetalleUsaActualizacionDeSesion() = runTest(dispatcher) {
        val vm = vm(); runCurrent(); vm.editar(12, "Nuevo"); runCurrent(); vm.cerrarDetalle(); vm.abrir(12); runCurrent()
        assertEquals("Nuevo", detalle(vm).post.title); assertEquals(listOf(comentario), detalle(vm).comentarios)
    }
    @Test fun eliminarTienePrioridadSobreActualizacionGuardada() = runTest(dispatcher) {
        val vm = vm(); runCurrent(); vm.editar(12, "Nuevo"); runCurrent(); vm.eliminar(12); runCurrent(); vm.cargar(); runCurrent()
        assertEquals(listOf(otro), lista(vm)); vm.abrir(12); runCurrent(); assertTrue(vm.detalle.value is UiState.Error)
    }
    @Test fun eliminarCancelaEdicionPendienteYNoReaparece() = runTest(dispatcher) {
        val espera = CompletableDeferred<Result<Post>>(); repo.respuesta = { withContext(NonCancellable) { espera.await() } }
        val vm = vm(); runCurrent(); vm.editar(12, "Nuevo"); runCurrent(); vm.eliminar(12); runCurrent()
        espera.complete(Result.success(original.copy(title = "Nuevo"))); runCurrent(); vm.cargar(); runCurrent(); assertEquals(listOf(otro), lista(vm))
    }
    @Test fun idDistintoNoLlamaRepository() = runTest(dispatcher) {
        val vm = vm(); runCurrent(); vm.editar(13, "Nuevo"); runCurrent(); assertTrue(repo.cambios.isEmpty())
    }
    private inner class Falso : PostRepository {
        var getLista = 0
        val cambios = mutableListOf<Pair<Int, EditarPost>>()
        var respuesta: suspend (EditarPost) -> Result<Post> = { Result.success(original.copy(title = it.title)) }
        override suspend fun listar(): Result<List<Post>> { getLista++; return Result.success(listOf(original, otro)) }
        override suspend fun porUsuario(userId: Int) = Result.success(listOf(original, otro))
        override suspend fun detalle(id: Int) = Result.success(if (id == 12) original else otro)
        override suspend fun comentarios(id: Int) = Result.success(listOf(comentario))
        override suspend fun crear(nuevo: NuevoPost): Result<Post> = error("Llamada inesperada")
        override suspend fun eliminar(id: Int) = Result.success(Unit)
        override suspend fun editar(id: Int, cambios: EditarPost): Result<Post> { this.cambios += id to cambios; return respuesta(cambios) }
    }
}
