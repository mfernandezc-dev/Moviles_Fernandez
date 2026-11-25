package com.fernandez.murocampus.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
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
import java.io.IOException
import kotlinx.coroutines.awaitCancellation
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetalleTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val store = ViewModelStore()
    private val post = Post(2, 17, "Titulo completo controlado", "Primera línea\nSegunda línea\nTercera línea")
    private val comentarios = listOf(
        Comentario(17, 81, "Uno", "uno@test.com", "Comentario completo\nSegunda línea\nTercera línea"),
        Comentario(17, 82, "Dos", "dos@test.com", "Otro comentario")
    )
    private val repo = RepositoryFalso()

    @After fun limpiar() { compose.runOnUiThread { store.clear() } }

    @Test fun tarjetaEjecutaSuCallback() {
        var llamadas = 0
        compose.setContent { MuroCampusTheme { TarjetaPost(post, onClick = { llamadas++ }) } }
        compose.onNodeWithText(post.title).performClick()
        compose.runOnIdle { assertEquals(1, llamadas) }
    }

    @Test fun listaEmiteElIdRealDeLaPublicacion() {
        var seleccionado: Int? = null
        val vm = crear()
        compose.setContent { MuroCampusTheme { PantallaLista(vm, onPostClick = { seleccionado = it }) } }
        compose.onNodeWithText(post.title).performClick()
        compose.runOnIdle { assertEquals(17, seleccionado) }
    }

    @Test fun detalleMuestraTituloYCuerpoSinTruncar() {
        mostrarDetalle()
        compose.onNodeWithText(post.title).assertIsDisplayed()
        compose.onNodeWithText(post.body).assertIsDisplayed()
        val resultados = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(post.body).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(resultados) }
        assertEquals(3, resultados.single().lineCount)
        assertFalse(resultados.single().isLineEllipsized(2))
    }

    @Test fun encabezadoUsaLaCantidadRealDeComentarios() {
        mostrarDetalle()
        compose.onNodeWithText("Comentarios (2)").performScrollTo().assertIsDisplayed()
    }

    @Test fun comentariosMuestranCorreoYContenidoCompleto() {
        mostrarDetalle()
        compose.onNodeWithText(comentarios[0].email).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(comentarios[0].body).performScrollTo().assertIsDisplayed()
        val resultados = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(comentarios[0].body).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(resultados) }
        assertEquals(3, resultados.single().lineCount)
        assertFalse(resultados.single().isLineEllipsized(2))
        compose.onNodeWithText(comentarios[1].email).performScrollTo().assertIsDisplayed()
    }

    @Test fun detalleMuestraCargandoMientrasElRepositoryEstaPendiente() {
        repo.respuestaPost = { awaitCancellation() }
        mostrarDetalle()
        compose.onNodeWithContentDescription("Cargando detalle").assertIsDisplayed()
        compose.onNodeWithText("Cargando publicación y comentarios…").assertIsDisplayed()
    }

    @Test fun errorYReintentoUsanElIdDelDetalleSinRecargarLista() {
        repo.respuestaPost = { Result.failure(IOException()) }
        mostrarDetalle()
        compose.onNodeWithText("Sin conexión a internet.\nRevisa tu red e inténtalo otra vez.").assertIsDisplayed()
        compose.runOnUiThread { repo.respuestaPost = { Result.success(post) } }
        compose.onNodeWithText("Reintentar").performClick()
        compose.onNodeWithText(post.title).assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(listOf(17, 17), repo.ids)
            assertEquals(1, repo.llamadasLista)
        }
    }

    @Test fun navegacionYAtrasConservanLaListaSinDescargarla() {
        val vm = crear()
        compose.setContent { MuroCampusTheme { PantallaInicio(vm) } }
        compose.onNodeWithText(post.title).performClick()
        compose.onNodeWithText("Comentarios (2)").performScrollTo().assertIsDisplayed()
        pressBack()
        compose.onNode(hasText("Usuario 2") and hasText(post.title)).assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, repo.llamadasLista) }
    }

    @Test fun regresarDuranteCargaCancelaElDetalleSinMostrarError() {
        repo.respuestaPost = { awaitCancellation() }
        val vm = crear()
        compose.setContent { MuroCampusTheme { PantallaInicio(vm) } }
        compose.onNodeWithText(post.title).performClick()
        compose.onNodeWithContentDescription("Cargando detalle").assertIsDisplayed()
        pressBack()
        compose.onNode(hasText("Usuario 2") and hasText(post.title)).assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, repo.llamadasLista) }
    }

    private fun crear(): PostsViewModel {
        lateinit var vm: PostsViewModel
        compose.runOnUiThread { vm = PostsViewModel(repo); store.put("detalle", vm) }
        return vm
    }

    private fun mostrarDetalle() {
        val vm = crear()
        compose.runOnUiThread { vm.abrir(17) }
        compose.setContent { MuroCampusTheme { PantallaDetalle(vm, 17) } }
    }

    private inner class RepositoryFalso : PostRepository {
        override suspend fun eliminar(id: Int): Result<Unit> = error("Llamada inesperada a eliminar")
        var llamadasLista = 0
        val ids = mutableListOf<Int>()
        var respuestaPost: suspend () -> Result<Post> = { Result.success(post) }
        override suspend fun listar(): Result<List<Post>> { llamadasLista++; return Result.success(listOf(post)) }
        override suspend fun detalle(id: Int): Result<Post> { ids += id; return respuestaPost() }
        override suspend fun comentarios(id: Int): Result<List<Comentario>> = Result.success(this@DetalleTest.comentarios)
        override suspend fun porUsuario(userId: Int): Result<List<Post>> = error("No se utiliza")
        override suspend fun crear(nuevo: NuevoPost): Result<Post> = error("No se utiliza")
    }
}
