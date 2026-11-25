package com.fernandez.murocampus.viewmodel

import androidx.lifecycle.ViewModelStore
import com.fernandez.murocampus.data.PostRepository
import com.fernandez.murocampus.model.Comentario
import com.fernandez.murocampus.model.EditarPost
import com.fernandez.murocampus.model.NuevoPost
import com.fernandez.murocampus.model.Post
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PublicacionesLocalesTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val remotos = listOf(Post(1, 1, "Original uno", "Texto"), Post(2, 2, "Original dos", "Texto"))
    private val repo = Falso()
    @Before fun configurar() { Dispatchers.setMain(dispatcher) }
    @After fun limpiar() { try { store.clear(); dispatcher.scheduler.runCurrent() } finally { Dispatchers.resetMain() } }
    private fun crear() = PostsViewModel(repo).also { store.put("locales", it) }
    private fun datos(vm: PostsViewModel) = (vm.resultados.value as UiState.Exito).datos
    private fun local(vm: PostsViewModel) = datos(vm).first { vm.esLocal(it.id) }

    @Test fun postExitosoApareceAlInicioConDatosRecibidos() = runTest(dispatcher) {
        val vm = crear(); runCurrent(); vm.publicar("Tecsup", "moviles"); runCurrent()
        assertEquals("Tecsup", datos(vm).first().title); assertEquals("moviles", datos(vm).first().body)
        assertEquals(remotos, datos(vm).drop(1)); assertEquals(1, repo.getLista)
    }
    @Test fun creacionConservaRespuesta101SinModificarListaDescargada() = runTest(dispatcher) {
        val vm = crear(); runCurrent(); val lista = vm.lista.value; vm.publicar("Tecsup", "moviles"); runCurrent()
        assertEquals(101, (vm.creacion.value as UiState.Exito).datos.id); assertTrue(local(vm).id < 0)
        assertSame(lista, vm.lista.value); assertEquals(remotos, (vm.lista.value as UiState.Exito).datos)
    }
    @Test fun usaTituloAutorYCuerpoDeRespuestaNoDatosInventados() = runTest(dispatcher) {
        repo.responder = { Result.success(Post(2, 101, "Normalizado", "Devuelto")) }
        val vm = crear(); vm.publicar("Tecsup", "moviles"); runCurrent()
        assertEquals(Post(2, -1, "Normalizado", "Devuelto"), local(vm))
    }
    @Test fun busquedaIncluyeNuevosPostsSinGet() = runTest(dispatcher) {
        val vm = crear(); runCurrent(); vm.buscar("  TECSUP  "); vm.publicar("Tecsup", "moviles"); runCurrent()
        assertEquals(listOf("Tecsup"), datos(vm).map { it.title }); assertEquals(1, repo.getLista)
    }
    @Test fun dosRespuestas101TienenClavesDistintasYOrdenMasRecientePrimero() = runTest(dispatcher) {
        val vm = crear(); vm.publicar("Primero", "uno"); runCurrent(); vm.publicar("Segundo", "dos"); runCurrent()
        val datos = datos(vm); assertEquals(listOf("Segundo", "Primero"), datos.take(2).map { it.title })
        assertEquals(datos.size, datos.map { it.id }.distinct().size); assertTrue(datos.take(2).all { it.id < 0 })
    }
    @Test fun localNoColisionaAunqueDescargaIncluyaId101() = runTest(dispatcher) {
        repo.lista = { Result.success(remotos + Post(1, 101, "Remoto 101", "Texto")) }
        val vm = crear(); vm.publicar("Local", "Texto"); runCurrent()
        assertEquals(4, datos(vm).map { it.id }.distinct().size)
        assertFalse(vm.esLocal(101)); assertTrue(vm.esLocal(-1))
    }
    @Test fun filtroRespetaAutorDevueltoYNoPierdeLocales() = runTest(dispatcher) {
        val vm = crear(); vm.publicar("Tecsup", "moviles"); runCurrent(); vm.filtrar(2); runCurrent()
        assertTrue(datos(vm).none { vm.esLocal(it.id) }); vm.filtrar(1); runCurrent()
        assertEquals("Tecsup", datos(vm).first().title); assertTrue(datos(vm).all { it.userId == 1 })
        vm.filtrar(null); runCurrent(); assertEquals("Tecsup", datos(vm).first().title)
    }
    @Test fun crearConFiltroDistintoNoCambiaAutorNiFiltro() = runTest(dispatcher) {
        val vm = crear(); runCurrent(); vm.filtrar(2); runCurrent(); vm.publicar("Tecsup", "moviles"); runCurrent()
        assertEquals(2, vm.usuarioFiltro.value); assertTrue(datos(vm).none { it.title == "Tecsup" })
        vm.filtrar(1); runCurrent(); assertEquals("Tecsup", datos(vm).first().title)
    }
    @Test fun recargaMantieneLocalesUnaSolaVez() = runTest(dispatcher) {
        val vm = crear(); vm.publicar("Tecsup", "moviles"); runCurrent(); val clave = local(vm).id
        repeat(3) { vm.cargar(); runCurrent() }
        assertEquals(1, datos(vm).count { it.id == clave }); assertEquals(3, datos(vm).size)
    }
    @Test fun recargaPendienteNoBorraPublicacionCreadaMientrasEspera() = runTest(dispatcher) {
        val espera = CompletableDeferred<Result<List<Post>>>(); repo.lista = { espera.await() }
        val vm = crear(); runCurrent(); vm.publicar("Tecsup", "moviles"); runCurrent()
        espera.complete(Result.success(remotos)); runCurrent(); assertEquals("Tecsup", datos(vm).first().title)
    }
    @Test fun detalleLocalNoInvocaGetNiComentariosRemotos() = runTest(dispatcher) {
        val vm = crear(); vm.publicar("Tecsup", "moviles"); runCurrent(); val post = local(vm)
        vm.abrir(post.id); runCurrent(); val detalle = (vm.detalle.value as UiState.Exito).datos
        assertEquals(post, detalle.post); assertTrue(detalle.comentarios.isEmpty()); assertTrue(repo.getDetalle.isEmpty())
    }
    @Test fun editarLocalNoInvocaPatchYActualizaBusquedaYDetalle() = runTest(dispatcher) {
        val vm = crear(); vm.publicar("Tecsup", "moviles"); runCurrent(); val id = local(vm).id
        vm.abrir(id); vm.prepararEdicion(); vm.editar(id, " Tecsup editado "); runCurrent()
        assertEquals("Tecsup editado", (vm.detalle.value as UiState.Exito).datos.post.title)
        vm.buscar("editado"); runCurrent(); assertEquals(listOf(id), datos(vm).map { it.id }); assertTrue(repo.patches.isEmpty())
    }
    @Test fun edicionLocalConservaAutorCuerpoClaveYSeMantieneTrasRecarga() = runTest(dispatcher) {
        val vm = crear(); vm.publicar("Tecsup", "moviles"); runCurrent(); val post = local(vm)
        vm.abrir(post.id); vm.editar(post.id, "Nuevo"); runCurrent(); vm.cargar(); runCurrent()
        assertEquals(post.copy(title = "Nuevo"), local(vm)); vm.abrir(post.id); runCurrent()
        assertEquals(post.copy(title = "Nuevo"), (vm.detalle.value as UiState.Exito).datos.post)
    }
    @Test fun eliminarLocalNoInvocaDeleteNiReapareceAlRecargar() = runTest(dispatcher) {
        val vm = crear(); vm.publicar("Tecsup", "moviles"); runCurrent(); val id = local(vm).id
        vm.abrir(id); vm.eliminar(id); runCurrent(); assertEquals(UiState.Exito(Unit), vm.eliminacion.value)
        vm.cargar(); runCurrent(); assertEquals(remotos, datos(vm)); assertTrue(repo.deletes.isEmpty()); assertFalse(vm.esLocal(id))
        vm.abrir(id); runCurrent(); assertTrue(vm.detalle.value is UiState.Error); assertTrue(repo.getDetalle.isEmpty())
    }
    @Test fun modificarUnLocalNoAfectaOtroConMismoIdServidor() = runTest(dispatcher) {
        val vm = crear(); vm.publicar("Primero", "uno"); runCurrent(); val primero = local(vm).id
        vm.publicar("Segundo", "dos"); runCurrent(); val segundo = local(vm).id
        vm.abrir(primero); vm.editar(primero, "Editado"); runCurrent(); vm.eliminar(primero); runCurrent()
        assertEquals("Segundo", local(vm).title); assertEquals(segundo, local(vm).id)
    }
    @Test fun remotosSiguenDelegandoPatchDeleteYGetRealesAlRepository() = runTest(dispatcher) {
        val vm = crear(); runCurrent(); vm.abrir(1); runCurrent(); vm.editar(1, "Remoto editado"); runCurrent(); vm.eliminar(1); runCurrent()
        assertEquals(listOf(1), repo.getDetalle); assertEquals(listOf(1), repo.patches); assertEquals(listOf(1), repo.deletes)
    }
    @Test fun falloPostNoCreaLocal() = runTest(dispatcher) {
        repo.responder = { Result.failure(IOException()) }; val vm = crear(); vm.publicar("Tecsup", "moviles"); runCurrent()
        assertEquals(remotos, datos(vm)); assertTrue(vm.creacion.value is UiState.Error)
    }
    @Test fun respuestaPostObsoletaNoInsertaLocal() = runTest(dispatcher) {
        val espera = CompletableDeferred<Result<Post>>(); repo.responder = { withContext(NonCancellable) { espera.await() } }
        val vm = crear(); vm.publicar("Tecsup", "moviles"); runCurrent(); vm.cerrarCreacion()
        espera.complete(Result.success(Post(1, 101, "Tecsup", "moviles"))); runCurrent(); assertEquals(remotos, datos(vm))
    }
    @Test fun cancelacionPostNoInsertaLocal() = runTest(dispatcher) {
        repo.responder = { throw CancellationException() }; val vm = crear(); vm.publicar("Tecsup", "moviles"); runCurrent()
        assertEquals(remotos, datos(vm))
    }
    @Test fun idLocalDesconocidoNuncaConsultaServidor() = runTest(dispatcher) {
        val vm = crear(); runCurrent(); vm.abrir(-99); vm.editar(-99, "Nuevo"); vm.eliminar(-99); runCurrent()
        assertTrue(vm.detalle.value is UiState.Error); assertTrue(repo.getDetalle.isEmpty()); assertTrue(repo.patches.isEmpty()); assertTrue(repo.deletes.isEmpty())
    }
    private inner class Falso : PostRepository {
        var getLista = 0
        val getDetalle = mutableListOf<Int>()
        val patches = mutableListOf<Int>()
        val deletes = mutableListOf<Int>()
        var lista: suspend () -> Result<List<Post>> = { Result.success(remotos) }
        var responder: suspend (NuevoPost) -> Result<Post> = { Result.success(Post(it.userId, 101, it.title, it.body)) }
        override suspend fun listar(): Result<List<Post>> { getLista++; return lista() }
        override suspend fun porUsuario(userId: Int) = Result.success(remotos.filter { it.userId == userId })
        override suspend fun detalle(id: Int): Result<Post> { check(id > 0); getDetalle += id; return Result.success(remotos.single { it.id == id }) }
        override suspend fun comentarios(id: Int): Result<List<Comentario>> { check(id > 0); return Result.success(emptyList()) }
        override suspend fun crear(nuevo: NuevoPost) = responder(nuevo)
        override suspend fun editar(id: Int, cambios: EditarPost): Result<Post> { check(id > 0); patches += id; return Result.success(remotos.single { it.id == id }.copy(title = cambios.title)) }
        override suspend fun eliminar(id: Int): Result<Unit> { check(id > 0); deletes += id; return Result.success(Unit) }
    }
}
