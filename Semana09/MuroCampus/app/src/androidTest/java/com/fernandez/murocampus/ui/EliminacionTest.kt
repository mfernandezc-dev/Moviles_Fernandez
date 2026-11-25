package com.fernandez.murocampus.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fernandez.murocampus.PantallaInicio
import com.fernandez.murocampus.data.PostRepository
import com.fernandez.murocampus.model.*
import com.fernandez.murocampus.ui.theme.MuroCampusTheme
import com.fernandez.murocampus.viewmodel.PostsViewModel
import java.io.IOException
import kotlinx.coroutines.awaitCancellation
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EliminacionTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val store = ViewModelStore()
    private val repo = Falso()
    @After fun limpiar() { compose.runOnUiThread { store.clear() } }
    private fun mostrar() {
        lateinit var vm: PostsViewModel
        compose.runOnUiThread { vm = PostsViewModel(repo); store.put("eliminar", vm) }
        compose.setContent { MuroCampusTheme { PantallaInicio(vm) } }
        compose.onNodeWithText("Título original").performClick()
        compose.onNodeWithText("Eliminar publicación").performScrollTo().performClick()
    }
    @Test fun cancelarDialogoNoEnviaDelete() {
        mostrar(); compose.onNodeWithText("¿Deseas eliminar esta publicación?").assertIsDisplayed()
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithText("Título original").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, repo.llamadas) }
    }
    @Test fun confirmarRegresaAListaConExitoYExcluyePost() {
        mostrar(); compose.onNodeWithText("Eliminar", substring = false).performClick()
        compose.onNodeWithText("Publicación eliminada").assertIsDisplayed()
        compose.onNodeWithText("Título original").assertDoesNotExist()
        compose.onNodeWithText("Otro título").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, repo.llamadas) }
    }
    @Test fun errorConservaDetalleYPermiteConfirmarNuevoIntento() {
        repo.respuesta = { Result.failure(IOException()) }; mostrar()
        compose.onNodeWithText("Eliminar", substring = false).performClick()
        compose.onNodeWithText("Sin conexión a internet.", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Título original").assertIsDisplayed()
        compose.runOnUiThread { repo.respuesta = { Result.success(Unit) } }
        compose.onNodeWithText("Eliminar publicación").performClick()
        compose.onNodeWithText("Eliminar", substring = false).performClick()
        compose.onNodeWithText("Publicación eliminada").assertIsDisplayed()
        compose.runOnIdle { assertEquals(2, repo.llamadas) }
    }
    @Test fun pendienteDeshabilitaBotonSinRetirarDatos() {
        repo.respuesta = { awaitCancellation() }; mostrar()
        compose.onNodeWithText("Eliminar", substring = false).performClick()
        compose.onNodeWithText("Eliminar publicación").assertIsNotEnabled()
        compose.onNodeWithText("Título original").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, repo.llamadas) }
    }
    private class Falso : PostRepository {
        override suspend fun editar(id: Int, cambios: EditarPost): Result<Post> = error("Llamada inesperada a editar")
        val posts = listOf(Post(1, 1, "Título original", "Contenido"), Post(1, 2, "Otro título", "Contenido"))
        var llamadas = 0
        var respuesta: suspend () -> Result<Unit> = { Result.success(Unit) }
        override suspend fun listar() = Result.success(posts)
        override suspend fun porUsuario(userId: Int) = Result.success(posts)
        override suspend fun detalle(id: Int) = Result.success(posts.single { it.id == id })
        override suspend fun comentarios(id: Int): Result<List<Comentario>> = Result.success(emptyList())
        override suspend fun crear(nuevo: NuevoPost): Result<Post> = error("Llamada inesperada a crear")
        override suspend fun eliminar(id: Int): Result<Unit> { llamadas++; return respuesta() }
    }
}
