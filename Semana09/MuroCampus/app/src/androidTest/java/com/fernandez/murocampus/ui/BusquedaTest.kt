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
class BusquedaTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val store = ViewModelStore()
    private val repo = RepositoryFalso()

    @After fun limpiar() { compose.runOnUiThread { store.clear() } }

    @Test fun campoAccesibleYConsultaVaciaMuestranLaLista() {
        mostrar()
        compose.onNodeWithText("Buscar por título").assertIsDisplayed()
        compose.onNodeWithText("Qui campus").assertIsDisplayed()
        compose.onNodeWithContentDescription("Limpiar búsqueda").assertDoesNotExist()
    }

    @Test fun escribirFiltraSinSolicitudesYLimpiarRecuperaLaLista() {
        mostrar()
        compose.onNodeWithTag("busqueda").performTextInput("QUI")
        compose.onNodeWithText("Qui campus").assertIsDisplayed()
        compose.onNodeWithText("Otro título").assertDoesNotExist()
        compose.onNodeWithContentDescription("Limpiar búsqueda").performClick()
        compose.onNodeWithTag("busqueda").assertTextContains("")
        compose.onNodeWithText("Otro título").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, repo.llamadasLista); assertEquals(emptyList<Int>(), repo.usuarios) }
    }

    @Test fun sinCoincidenciasMuestraMensajeNoError() {
        mostrar()
        compose.onNodeWithTag("busqueda").performTextInput("inexistente")
        compose.onNodeWithText("No se encontraron publicaciones.").assertIsDisplayed()
        compose.onNodeWithText("Reintentar").assertDoesNotExist()
    }

    @Test fun filtroUsuarioDosConservaConsultaYLimpiarConservaFiltro() {
        mostrar()
        compose.onNodeWithTag("busqueda").performTextInput("qui")
        compose.onNodeWithTag("filtro-usuario-2").performScrollTo().performClick()
        compose.onNodeWithText("Aliquid nuevo").assertIsDisplayed()
        compose.onNodeWithText("Qui campus").assertDoesNotExist()
        compose.onNodeWithContentDescription("Limpiar búsqueda").performClick()
        compose.onNodeWithTag("filtro-usuario-2").assertIsSelected()
        compose.onNodeWithText("Otro título").assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf(2), repo.usuarios) }
    }

    @Test fun navegarAlDetalleYVolverConservaConsultaSinGetLista() {
        mostrar()
        compose.onNodeWithTag("busqueda").performTextInput("campus")
        compose.onNodeWithText("Qui campus").performClick()
        compose.onNodeWithText("Comentarios (0)").assertIsDisplayed()
        pressBack()
        compose.onNodeWithTag("busqueda").assertTextContains("campus")
        compose.onNodeWithText("Otro título").assertDoesNotExist()
        compose.runOnIdle { assertEquals(1, repo.llamadasLista) }
    }

    @Test fun recreacionDeComposicionConViewModelRetenidoConservaBusqueda() {
        val vm = crear()
        val restauracion = StateRestorationTester(compose)
        restauracion.setContent { MuroCampusTheme { PantallaInicio(vm) } }
        compose.onNodeWithTag("busqueda").performTextInput("campus")
        restauracion.emulateSavedInstanceStateRestore()
        compose.onNodeWithTag("busqueda").assertTextContains("campus")
        compose.onNodeWithText("Qui campus").assertIsDisplayed()
        compose.onNodeWithText("Otro título").assertDoesNotExist()
        compose.runOnIdle { assertEquals(1, repo.llamadasLista) }
    }

    private fun crear(): PostsViewModel {
        lateinit var vm: PostsViewModel
        compose.runOnUiThread { vm = PostsViewModel(repo); store.put("busqueda", vm) }
        return vm
    }
    private fun mostrar() {
        val vm = crear()
        compose.setContent { MuroCampusTheme { PantallaInicio(vm) } }
    }
    private class RepositoryFalso : PostRepository {
        override suspend fun editar(id: Int, cambios: com.fernandez.murocampus.model.EditarPost): Result<Post> = error("Llamada inesperada a editar")
        override suspend fun eliminar(id: Int): Result<Unit> = error("Llamada inesperada a eliminar")
        val posts = listOf(Post(1, 1, "Qui campus", "Texto"), Post(2, 11, "Aliquid nuevo", "Texto"), Post(2, 12, "Otro título", "qui"))
        var llamadasLista = 0
        val usuarios = mutableListOf<Int>()
        override suspend fun listar(): Result<List<Post>> { llamadasLista++; return Result.success(posts) }
        override suspend fun porUsuario(userId: Int): Result<List<Post>> { usuarios += userId; return Result.success(posts.filter { it.userId == userId }) }
        override suspend fun detalle(id: Int) = Result.success(posts.single { it.id == id })
        override suspend fun comentarios(id: Int): Result<List<Comentario>> = Result.success(emptyList())
        override suspend fun crear(nuevo: NuevoPost): Result<Post> = error("Llamada inesperada a crear")
    }
}
