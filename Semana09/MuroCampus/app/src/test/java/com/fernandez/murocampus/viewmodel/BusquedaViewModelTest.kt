package com.fernandez.murocampus.viewmodel

import androidx.lifecycle.ViewModelStore
import com.fernandez.murocampus.data.PostRepository
import com.fernandez.murocampus.model.Comentario
import com.fernandez.murocampus.model.NuevoPost
import com.fernandez.murocampus.model.Post
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BusquedaViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val posts = listOf(
        Post(1, 1, "Qui campus", "Texto"),
        Post(2, 11, "Aliquid nuevo", "Texto"),
        Post(2, 12, "Otro título", "qui solo en contenido")
    )
    private val repo = RepositoryFalso()

    @Before fun configurar() { Dispatchers.setMain(dispatcher) }
    @After fun limpiar() {
        try { store.clear(); dispatcher.scheduler.runCurrent() }
        finally { Dispatchers.resetMain() }
    }

    @Test fun consultaInicialVaciaMuestraTodos() = runTest(dispatcher) {
        val vm = crear(); runCurrent()
        assertEquals("", vm.busqueda.value)
        assertEquals(posts, datos(vm))
    }

    @Test fun coincidenciaParcialSoloBuscaEnTitulo() = runTest(dispatcher) {
        val vm = crear(); vm.buscar("qui"); runCurrent()
        assertEquals(posts.take(2), datos(vm))
    }

    @Test fun coincidenciaExacta() = runTest(dispatcher) {
        val vm = crear(); vm.buscar("Qui campus"); runCurrent()
        assertEquals(listOf(posts[0]), datos(vm))
    }

    @Test fun ignoraMayusculasYMinusculas() = runTest(dispatcher) {
        val vm = crear(); vm.buscar("qui"); runCurrent(); val minusculas = datos(vm)
        vm.buscar("QUI"); runCurrent()
        assertEquals(minusculas, datos(vm))
    }

    @Test fun recortaEspaciosSinModificarLoEscrito() = runTest(dispatcher) {
        val vm = crear(); vm.buscar("  QUI  "); runCurrent()
        assertEquals(posts.take(2), datos(vm)); assertEquals("  QUI  ", vm.busqueda.value)
    }

    @Test fun soloEspaciosEquivaleAConsultaVacia() = runTest(dispatcher) {
        val vm = crear(); vm.buscar("   "); runCurrent()
        assertEquals(posts, datos(vm))
    }

    @Test fun ausenciaDeCoincidenciasEsExitoVacioNoError() = runTest(dispatcher) {
        val vm = crear(); vm.buscar("inexistente"); runCurrent()
        assertEquals(UiState.Exito(emptyList<Post>()), vm.resultados.value)
    }

    @Test fun usuarioDosCombinaBusquedaConDatosDescargados() = runTest(dispatcher) {
        val vm = crear(); runCurrent(); vm.filtrar(2); vm.buscar("qui"); runCurrent()
        assertEquals(listOf(posts[1]), datos(vm)); assertEquals(listOf(2), repo.usuarios)
    }

    @Test fun cambiarFiltroAplicaLaConsultaExistente() = runTest(dispatcher) {
        val vm = crear(); vm.buscar("QUI"); runCurrent(); vm.filtrar(2); runCurrent()
        assertEquals("QUI", vm.busqueda.value); assertEquals(listOf(posts[1]), datos(vm))
        vm.filtrar(null); runCurrent(); assertEquals(posts.take(2), datos(vm))
    }

    @Test fun limpiarRecuperaLosDatosDelFiltroActual() = runTest(dispatcher) {
        val vm = crear(); runCurrent(); vm.filtrar(2); vm.buscar("nada"); runCurrent()
        vm.buscar(""); runCurrent(); assertEquals(posts.drop(1), datos(vm))
    }

    @Test fun noModificaLaListaOriginalNiSuInstancia() = runTest(dispatcher) {
        val vm = crear(); runCurrent(); val original = vm.lista.value
        vm.buscar("qui"); runCurrent()
        assertSame(original, vm.lista.value); assertSame(posts, (vm.lista.value as UiState.Exito).datos)
    }

    @Test fun escribirYLimpiarNoSolicitaDatos() = runTest(dispatcher) {
        val vm = crear(); runCurrent()
        listOf("q", "qu", "qui", "QUI", "nada", "").forEach { vm.buscar(it); runCurrent() }
        assertEquals(1, repo.llamadasLista); assertTrue(repo.usuarios.isEmpty())
    }

    @Test fun detalleYRegresoConservanConsultaSinRecargarLista() = runTest(dispatcher) {
        val vm = crear(); vm.buscar("qui"); runCurrent(); vm.abrir(1); runCurrent(); vm.cerrarDetalle()
        assertEquals("qui", vm.busqueda.value); assertEquals(posts.take(2), datos(vm))
        assertEquals(1, repo.llamadasLista)
    }

    @Test fun errorYReintentoConservanConsultaYFiltro() = runTest(dispatcher) {
        val vm = crear(); runCurrent(); repo.fallo = true
        vm.buscar("qui"); vm.filtrar(2); runCurrent()
        assertTrue(vm.resultados.value is UiState.Error); assertEquals("qui", vm.busqueda.value)
        repo.fallo = false; vm.cargar(); runCurrent()
        assertEquals(listOf(posts[1]), datos(vm)); assertEquals(2, vm.usuarioFiltro.value)
        assertEquals(listOf(2, 2), repo.usuarios)
    }

    @Test fun buscarDuranteCargaConservaCargandoYNoDuplicaSolicitud() = runTest(dispatcher) {
        repo.pendiente = true; val vm = crear(); runCurrent(); vm.buscar("qui"); runCurrent()
        assertEquals(UiState.Cargando, vm.resultados.value); assertEquals(1, repo.llamadasLista)
    }

    @Test fun crearNoModificaBusquedaNiListaDescargada() = runTest(dispatcher) {
        val vm = crear(); vm.buscar("qui"); runCurrent(); vm.publicar("Nuevo", "Contenido"); runCurrent()
        assertEquals("qui", vm.busqueda.value); assertEquals(posts.take(2), datos(vm))
        assertEquals(101, (vm.creacion.value as UiState.Exito).datos.id)
    }

    private fun crear() = PostsViewModel(repo).also { store.put("busqueda", it) }
    private fun datos(vm: PostsViewModel) = (vm.resultados.value as UiState.Exito).datos

    private inner class RepositoryFalso : PostRepository {
        var llamadasLista = 0
        val usuarios = mutableListOf<Int>()
        var fallo = false
        var pendiente = false
        override suspend fun listar(): Result<List<Post>> {
            llamadasLista++
            if (pendiente) awaitCancellation()
            return Result.success(posts)
        }
        override suspend fun porUsuario(userId: Int): Result<List<Post>> {
            usuarios += userId
            return if (fallo) Result.failure(IOException()) else Result.success(posts.filter { it.userId == userId })
        }
        override suspend fun detalle(id: Int) = Result.success(posts.single { it.id == id })
        override suspend fun comentarios(id: Int): Result<List<Comentario>> = Result.success(emptyList())
        override suspend fun crear(nuevo: NuevoPost) = Result.success(Post(nuevo.userId, 101, nuevo.title, nuevo.body))
    }
}
