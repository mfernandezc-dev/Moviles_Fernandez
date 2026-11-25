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
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class CreacionViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val recibido = Post(1, 742, "Título recibido", "Contenido recibido")
    private val repo = RepositoryFalso()

    @Before fun configurar() { Dispatchers.setMain(dispatcher) }
    @After fun limpiar() {
        try { store.clear(); dispatcher.scheduler.runCurrent() }
        finally { Dispatchers.resetMain() }
    }

    @Test fun estadoInicialEsNull() = runTest(dispatcher) {
        val vm = crear()
        assertNull(vm.creacion.value)
        runCurrent()
        assertTrue(repo.enviados.isEmpty())
    }

    @Test fun tituloVacioNoPublica() = invalido("", "Contenido")
    @Test fun contenidoVacioNoPublica() = invalido("Título", "")
    @Test fun tituloConEspaciosNoPublica() = invalido(" \t\n ", "Contenido")
    @Test fun contenidoConEspaciosNoPublica() = invalido("Título", " \t\n ")

    @Test fun formularioValidoConstruyeNuevoPostSinId() = runTest(dispatcher) {
        val vm = crear()
        vm.publicar("Título", "Contenido")
        runCurrent()
        assertEquals(listOf(NuevoPost(1, "Título", "Contenido")), repo.enviados)
    }

    @Test fun autorEsUnoAunqueElFiltroSeaOtroUsuario() = runTest(dispatcher) {
        val vm = crear()
        runCurrent()
        vm.filtrar(8)
        runCurrent()
        vm.publicar("Título", "Contenido")
        runCurrent()
        assertEquals(1, repo.enviados.single().userId)
        assertEquals(8, vm.usuarioFiltro.value)
    }

    @Test fun recortaExtremosYConservaUnicodeYSaltosInteriores() = runTest(dispatcher) {
        val vm = crear()
        vm.publicar("  Mi primera publicación  ", "  Línea uno\nLínea dos con ñ  ")
        runCurrent()
        assertEquals(NuevoPost(1, "Mi primera publicación", "Línea uno\nLínea dos con ñ"), repo.enviados.single())
    }

    @Test fun cargandoMientrasLaSolicitudEstaPendiente() = runTest(dispatcher) {
        repo.respuesta = { awaitCancellation() }
        val vm = crear()
        vm.publicar("Título", "Contenido")
        assertEquals(UiState.Cargando, vm.creacion.value)
        runCurrent()
        assertEquals(UiState.Cargando, vm.creacion.value)
        assertEquals(1, repo.enviados.size)
    }

    @Test fun exitoConservaLaRespuestaCompletaYElIdRecibido() = runTest(dispatcher) {
        val vm = crear()
        vm.publicar("Título enviado", "Contenido enviado")
        runCurrent()
        assertEquals(UiState.Exito(recibido), vm.creacion.value)
        assertSame(recibido, (vm.creacion.value as UiState.Exito).datos)
        assertEquals(742, (vm.creacion.value as UiState.Exito).datos.id)
    }

    @Test fun ioExceptionProduceMensajeDeConexion() = errorEsperado(IOException(), "Sin conexión a internet.\nRevisa tu red e inténtalo otra vez.")
    @Test fun http404ProduceMensajeDelServidor() = errorEsperado(http(404), "Error del servidor (404)")
    @Test fun http500ProduceMensajeDelServidor() = errorEsperado(http(500), "Error del servidor (500)")
    @Test fun serializacionProduceRespuestaInesperada() = errorEsperado(SerializationException(), "Respuesta inesperada")
    @Test fun excepcionOrdinariaProduceErrorDesconocido() = errorEsperado(IllegalStateException(), "Error desconocido")

    @Test fun excepcionOrdinariaLanzadaTambienSeTransforma() = runTest(dispatcher) {
        repo.respuesta = { throw IOException() }
        val vm = crear()
        vm.publicar("Título", "Contenido")
        runCurrent()
        assertEquals(UiState.Error("Sin conexión a internet.\nRevisa tu red e inténtalo otra vez."), vm.creacion.value)
    }

    @Test fun reintentoVoluntarioRecuperaLaOperacion() = runTest(dispatcher) {
        repo.respuesta = { Result.failure(IOException()) }
        val vm = crear()
        vm.publicar("Título", "Contenido")
        runCurrent()
        assertTrue(vm.creacion.value is UiState.Error)
        repo.respuesta = { Result.success(recibido) }
        vm.publicar("Título", "Contenido")
        assertEquals(UiState.Cargando, vm.creacion.value)
        runCurrent()
        assertEquals(UiState.Exito(recibido), vm.creacion.value)
        assertEquals(2, repo.enviados.size)
    }

    @Test fun errorNoRealizaReintentosAutomaticos() = runTest(dispatcher) {
        repo.respuesta = { Result.failure(IOException()) }
        val vm = crear()
        vm.publicar("Título", "Contenido")
        repeat(5) { runCurrent() }
        assertEquals(1, repo.enviados.size)
        assertTrue(vm.creacion.value is UiState.Error)
    }

    @Test fun pulsacionesDuplicadasNoEjecutanPostSimultaneos() = runTest(dispatcher) {
        val pendiente = CompletableDeferred<Result<Post>>()
        repo.respuesta = { pendiente.await() }
        val vm = crear()
        repeat(10) { vm.publicar("Título", "Contenido") }
        runCurrent()
        repeat(10) { vm.publicar("Otro", "Otro") }
        runCurrent()
        assertEquals(1, repo.enviados.size)
        pendiente.complete(Result.success(recibido))
        runCurrent()
        assertEquals(UiState.Exito(recibido), vm.creacion.value)
    }

    @Test fun cancelacionLanzadaNoSeConvierteEnError() = runTest(dispatcher) {
        repo.respuesta = { throw CancellationException("normal") }
        val vm = crear()
        vm.publicar("Título", "Contenido")
        runCurrent()
        assertEquals(UiState.Cargando, vm.creacion.value)
    }

    @Test fun cancelacionEnResultNoSeConvierteEnError() = runTest(dispatcher) {
        repo.respuesta = { Result.failure(CancellationException("normal")) }
        val vm = crear()
        vm.publicar("Título", "Contenido")
        runCurrent()
        assertEquals(UiState.Cargando, vm.creacion.value)
    }

    @Test fun cerrarCancelaElTrabajoLocalPendiente() = runTest(dispatcher) {
        var cancelada = false
        repo.respuesta = { try { awaitCancellation() } finally { cancelada = true } }
        val vm = crear()
        vm.publicar("Título", "Contenido")
        runCurrent()
        vm.cerrarCreacion()
        runCurrent()
        assertTrue(cancelada)
        assertTrue(vm.creacion.value !is UiState.Error)
    }

    @Test fun limpiarViewModelCancelaLaPublicacionPendiente() = runTest(dispatcher) {
        var cancelada = false
        repo.respuesta = { try { awaitCancellation() } finally { cancelada = true } }
        val vm = crear()
        vm.publicar("Título", "Contenido")
        runCurrent()
        store.clear()
        runCurrent()
        assertTrue(cancelada)
        assertTrue(vm.creacion.value !is UiState.Error)
    }

    @Test fun prepararLimpiaUnExitoAnteriorSinEnviarSolicitudes() = runTest(dispatcher) {
        val vm = crear()
        vm.publicar("Título", "Contenido")
        runCurrent()
        vm.prepararCreacion()
        assertNull(vm.creacion.value)
        assertEquals(1, repo.enviados.size)
    }

    @Test fun prepararLimpiaUnErrorAnterior() = runTest(dispatcher) {
        repo.respuesta = { Result.failure(IOException()) }
        val vm = crear()
        vm.publicar("Título", "Contenido")
        runCurrent()
        vm.prepararCreacion()
        assertNull(vm.creacion.value)
    }

    @Test fun respuestaTardiaNoSobrescribeUnFormularioNuevo() = runTest(dispatcher) {
        val antigua = CompletableDeferred<Result<Post>>()
        repo.respuesta = { withContext(NonCancellable) { antigua.await() } }
        val vm = crear()
        vm.publicar("Anterior", "Anterior")
        runCurrent()
        vm.prepararCreacion()
        antigua.complete(Result.success(recibido))
        runCurrent()
        assertNull(vm.creacion.value)
    }

    @Test fun respuestaAnteriorNoSobrescribeUnaPublicacionNueva() = runTest(dispatcher) {
        val antigua = CompletableDeferred<Result<Post>>()
        repo.respuesta = { nuevo ->
            if (nuevo.title == "Anterior") withContext(NonCancellable) { antigua.await() }
            else Result.success(recibido.copy(id = 903))
        }
        val vm = crear()
        vm.publicar("Anterior", "Texto")
        runCurrent()
        vm.prepararCreacion()
        vm.publicar("Nueva", "Texto")
        runCurrent()
        antigua.complete(Result.success(recibido))
        runCurrent()
        assertEquals(903, (vm.creacion.value as UiState.Exito).datos.id)
    }

    @Test fun errorTardioNoSobrescribeElFormularioNuevo() = runTest(dispatcher) {
        val antigua = CompletableDeferred<Result<Post>>()
        repo.respuesta = { withContext(NonCancellable) { antigua.await() } }
        val vm = crear()
        vm.publicar("Anterior", "Texto")
        runCurrent()
        vm.prepararCreacion()
        antigua.complete(Result.failure(IOException()))
        runCurrent()
        assertNull(vm.creacion.value)
    }

    @Test fun publicarYPrepararConservanListaFiltroYDetalleSinNuevosGet() = runTest(dispatcher) {
        val vm = crear()
        runCurrent()
        vm.filtrar(2)
        vm.abrir(1)
        runCurrent()
        val lista = vm.lista.value
        val detalle = vm.detalle.value
        val gets = repo.gets
        vm.prepararCreacion()
        vm.publicar("Título", "Contenido")
        runCurrent()
        vm.cerrarCreacion()
        assertSame(lista, vm.lista.value)
        assertSame(detalle, vm.detalle.value)
        assertEquals(2, vm.usuarioFiltro.value)
        assertEquals(gets, repo.gets)
    }

    @Test fun publicarNoInsertaLaRespuestaEnLaLista() = runTest(dispatcher) {
        val vm = crear()
        runCurrent()
        val lista = vm.lista.value
        vm.publicar("Título", "Contenido")
        runCurrent()
        assertSame(lista, vm.lista.value)
        assertEquals(1, repo.gets)
        assertEquals(1, (vm.lista.value as UiState.Exito).datos.size)
    }

    @Test fun cerrarCreacionNoCancelaLaListaNiElDetallePendientes() = runTest(dispatcher) {
        val lista = CompletableDeferred<Result<List<Post>>>()
        val detalle = CompletableDeferred<Result<Post>>()
        repo.lista = { lista.await() }
        repo.detalle = { detalle.await() }
        repo.respuesta = { awaitCancellation() }
        val vm = crear()
        vm.abrir(1)
        vm.publicar("Título", "Contenido")
        runCurrent()
        vm.cerrarCreacion()
        lista.complete(Result.success(listOf(recibido)))
        detalle.complete(Result.success(recibido))
        runCurrent()
        assertTrue(vm.lista.value is UiState.Exito)
        assertTrue(vm.detalle.value is UiState.Exito)
    }

    private fun crear() = PostsViewModel(repo).also { store.put("creacion", it) }
    private fun invalido(titulo: String, contenido: String) = runTest(dispatcher) {
        val vm = crear()
        vm.publicar(titulo, contenido)
        runCurrent()
        assertNull(vm.creacion.value)
        assertTrue(repo.enviados.isEmpty())
    }
    private fun errorEsperado(error: Exception, mensaje: String) = runTest(dispatcher) {
        repo.respuesta = { Result.failure(error) }
        val vm = crear()
        vm.publicar("Título", "Contenido")
        runCurrent()
        assertEquals(UiState.Error(mensaje), vm.creacion.value)
    }
    private fun http(codigo: Int) = HttpException(Response.error<Post>(codigo, "{}".toResponseBody("application/json".toMediaType())))

    private inner class RepositoryFalso : PostRepository {
        val enviados = mutableListOf<NuevoPost>()
        var gets = 0
        var respuesta: suspend (NuevoPost) -> Result<Post> = { Result.success(recibido) }
        var lista: suspend () -> Result<List<Post>> = { Result.success(listOf(recibido)) }
        var detalle: suspend (Int) -> Result<Post> = { Result.success(recibido) }
        override suspend fun crear(nuevo: NuevoPost): Result<Post> { enviados += nuevo; return respuesta(nuevo) }
        override suspend fun listar(): Result<List<Post>> { gets++; return lista() }
        override suspend fun porUsuario(userId: Int): Result<List<Post>> { gets++; return Result.success(listOf(recibido.copy(userId = userId))) }
        override suspend fun detalle(id: Int): Result<Post> { gets++; return detalle.invoke(id) }
        override suspend fun comentarios(id: Int): Result<List<Comentario>> { gets++; return Result.success(emptyList()) }
    }
}
