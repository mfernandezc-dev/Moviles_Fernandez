package com.fernandez.murocampus.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelStore
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fernandez.murocampus.PantallaInicio
import com.fernandez.murocampus.data.PostRepository
import com.fernandez.murocampus.model.Comentario
import com.fernandez.murocampus.model.EditarPost
import com.fernandez.murocampus.model.NuevoPost
import com.fernandez.murocampus.model.Post
import com.fernandez.murocampus.ui.theme.MuroCampusTheme
import com.fernandez.murocampus.viewmodel.PostsViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CorreccionesTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val store = ViewModelStore()
    private val repo = Falso()
    @After fun limpiar() { compose.runOnUiThread { store.clear() } }
    private fun crear(): PostsViewModel {
        lateinit var vm: PostsViewModel
        compose.runOnUiThread { vm = PostsViewModel(repo); store.put("correcciones", vm) }
        return vm
    }
    private fun mostrar() {
        val vm = crear(); compose.setContent { MuroCampusTheme { PantallaInicio(vm) } }
    }
    private fun volver() { compose.onNodeWithContentDescription("Volver").performClick() }
    private fun publicar(titulo: String = "Tecsup") {
        compose.onNodeWithContentDescription("Crear publicación").performClick()
        compose.onNodeWithTag("titulo").performTextInput(titulo)
        compose.onNodeWithTag("contenido").performTextInput("moviles")
        compose.onNodeWithText("Publicar", substring = false).performClick()
        compose.onNodeWithText("Publicado con id 101").assertIsDisplayed()
        volver()
    }
    @Test fun flechaSoloEnSecundariasYRegresaTrasPostExitoso() {
        mostrar(); compose.onNodeWithContentDescription("Volver").assertDoesNotExist(); publicar()
        compose.onNodeWithText("Tecsup").assertIsDisplayed()
        compose.onNodeWithContentDescription("Volver").assertDoesNotExist()
        compose.onNodeWithText("Local · solo durante esta sesión").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, repo.getLista); assertEquals(1, repo.posts) }
    }
    @Test fun busquedaDetalleYEdicionLocalRegresanSinHttpAdicional() {
        mostrar(); publicar(); compose.onNodeWithTag("busqueda").performTextInput("Tecsup")
        compose.onNode(hasText("Tecsup") and hasClickAction() and !hasTestTag("busqueda")).performClick()
        compose.onNodeWithText("Publicación local · solo durante esta sesión").assertIsDisplayed()
        compose.onNodeWithText("Comentarios (0)").assertIsDisplayed()
        compose.onNodeWithText("Editar publicación").performScrollTo().performClick()
        compose.onNodeWithText("Título").performTextReplacement("Tecsup actualizado")
        compose.onNodeWithText("Guardar cambios").performClick()
        compose.onNodeWithText("Publicación actualizada").assertIsDisplayed()
        volver(); compose.onNodeWithText("Comentarios (0)").assertIsDisplayed()
        compose.onNodeWithText("Tecsup actualizado").assertIsDisplayed()
        volver(); compose.onNodeWithTag("busqueda").assertTextContains("Tecsup")
        compose.onNodeWithText("Tecsup actualizado").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, repo.getLista); assertEquals(0, repo.getDetalle) }
    }
    @Test fun eliminacionLocalConfirmadaLaRetiraSinDeleteRemoto() {
        mostrar(); publicar(); compose.onNodeWithText("Tecsup").performClick()
        compose.onNodeWithText("Eliminar publicación").performScrollTo().performClick()
        compose.onNodeWithText("Eliminar", substring = false).performClick()
        compose.onNodeWithText("Publicación eliminada").assertIsDisplayed()
        compose.onNodeWithText("Tecsup").assertDoesNotExist()
        compose.onNodeWithContentDescription("Volver").assertDoesNotExist()
    }
    @Test fun dosPost101SinClavesDuplicadasYFiltroPorAutor() {
        mostrar(); publicar("Primera local"); publicar("Segunda local")
        compose.onNodeWithTag("publicaciones").performScrollToIndex(0)
        compose.onNodeWithText("Primera local").assertIsDisplayed()
        compose.onNodeWithText("Segunda local").assertIsDisplayed()
        compose.onNodeWithTag("filtro-usuario-2").performScrollTo().performClick()
        compose.onNodeWithText("Primera local").assertDoesNotExist()
        compose.onNodeWithText("Segunda local").assertDoesNotExist()
        compose.onNodeWithTag("filtro-usuario-1").performScrollTo().performClick()
        compose.onNodeWithTag("publicaciones").performScrollToIndex(0)
        compose.onNodeWithText("Primera local").assertIsDisplayed()
        compose.onNodeWithText("Segunda local").assertIsDisplayed()
        compose.runOnIdle { assertEquals(2, repo.posts) }
    }
    @Test fun flechaYBackCompartenDestinoDesdeCrearDetalleYEditar() {
        mostrar(); compose.onNodeWithContentDescription("Crear publicación").performClick(); volver()
        compose.onNodeWithContentDescription("Crear publicación").assertIsDisplayed()
        compose.onNodeWithContentDescription("Crear publicación").performClick(); pressBack()
        compose.onNodeWithContentDescription("Volver").assertDoesNotExist()
        compose.onNodeWithText("Original 1").performClick()
        compose.onNodeWithText("Editar publicación").performScrollTo().performClick(); volver()
        compose.onNodeWithText("Comentarios (0)").assertIsDisplayed()
        compose.onNodeWithText("Editar publicación").performClick(); pressBack()
        compose.onNodeWithText("Comentarios (0)").assertIsDisplayed(); pressBack()
        compose.onNodeWithContentDescription("Volver").assertDoesNotExist()
        compose.runOnIdle { assertEquals(1, repo.getLista); assertEquals(1, repo.getDetalle) }
    }
    @Test fun regresoConservaFiltroConsultaYPosicionDeDesplazamiento() {
        mostrar(); compose.onNodeWithTag("filtro-usuario-1").performClick()
        compose.onNodeWithTag("busqueda").performTextInput("Original")
        compose.onNodeWithTag("publicaciones").performScrollToIndex(8)
        compose.onNodeWithText("Original 9").performClick()
        volver(); compose.onNodeWithText("Original 9").assertIsDisplayed()
        compose.onNodeWithTag("busqueda").assertTextContains("Original")
        compose.onNodeWithTag("filtro-usuario-1").assertIsSelected()
        compose.runOnIdle { assertEquals(listOf(1), repo.filtros); assertEquals(1, repo.getLista) }
    }
    @Test fun restauracionConViewModelRetenidoConservaLocalesYNavegacion() {
        val vm = crear(); val restauracion = StateRestorationTester(compose)
        restauracion.setContent { MuroCampusTheme { PantallaInicio(vm) } }; publicar()
        compose.onNodeWithText("Tecsup").performClick()
        restauracion.emulateSavedInstanceStateRestore()
        compose.onNodeWithContentDescription("Volver").assertIsDisplayed(); volver()
        compose.onNodeWithText("Tecsup").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, repo.posts); assertEquals(0, repo.getDetalle) }
    }
    @Test fun crearDesdeListaDesplazadaConservaElElementoVisibleAlVolver() {
        mostrar(); compose.onNodeWithTag("publicaciones").performScrollToIndex(8)
        publicar()
        compose.onNodeWithText("Original 9").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, repo.getLista); assertEquals(1, repo.posts) }
    }
    private class Falso : PostRepository {
        val remotos = (1..12).map { Post(1, it, "Original $it", "Texto $it") }
        var getLista = 0
        var getDetalle = 0
        var posts = 0
        val filtros = mutableListOf<Int>()
        override suspend fun listar(): Result<List<Post>> { getLista++; return Result.success(remotos) }
        override suspend fun porUsuario(userId: Int): Result<List<Post>> { filtros += userId; return Result.success(remotos.filter { it.userId == userId }) }
        override suspend fun detalle(id: Int): Result<Post> { check(id > 0); getDetalle++; return Result.success(remotos.single { it.id == id }) }
        override suspend fun comentarios(id: Int): Result<List<Comentario>> { check(id > 0); return Result.success(emptyList()) }
        override suspend fun crear(nuevo: NuevoPost): Result<Post> { posts++; return Result.success(Post(nuevo.userId, 101, nuevo.title, nuevo.body)) }
        override suspend fun editar(id: Int, cambios: EditarPost): Result<Post> = error("PATCH inesperado: $id")
        override suspend fun eliminar(id: Int): Result<Unit> = error("DELETE inesperado: $id")
    }
}
