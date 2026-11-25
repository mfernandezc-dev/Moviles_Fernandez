package com.fernandez.murocampus.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModelStore
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fernandez.murocampus.data.PostRepository
import com.fernandez.murocampus.model.Comentario
import com.fernandez.murocampus.model.NuevoPost
import com.fernandez.murocampus.model.Post
import com.fernandez.murocampus.ui.theme.MuroCampusTheme
import com.fernandez.murocampus.viewmodel.PostsViewModel
import com.fernandez.murocampus.viewmodel.UiState
import java.io.File
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComponentesTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun cargandoMuestraIndicadorYTexto() {
        compose.setContent { MuroCampusTheme { Cargando() } }
        compose.onNodeWithText("Cargando publicaciones…").assertIsDisplayed()
        compose.onNodeWithContentDescription("Cargando publicaciones").assertIsDisplayed()
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        File(contexto.filesDir, "cargando-controlado.png").outputStream().use { salida ->
            assertTrue(compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, salida))
        }
    }

    @Test
    fun listaMuestraCargandoMientrasElRepositoryEstaPendiente() {
        val pendiente = CompletableDeferred<Result<List<Post>>>()
        val repo = object : PostRepository {
            override suspend fun listar(): Result<List<Post>> = pendiente.await()
            override suspend fun detalle(id: Int): Result<Post> = error("No se utiliza en esta prueba")
            override suspend fun comentarios(id: Int): Result<List<Comentario>> = error("No se utiliza en esta prueba")
            override suspend fun porUsuario(userId: Int): Result<List<Post>> = error("No se utiliza en esta prueba")
            override suspend fun crear(nuevo: NuevoPost): Result<Post> = error("No se utiliza en esta prueba")
        }
        val store = ViewModelStore()
        lateinit var vm: PostsViewModel
        compose.runOnUiThread {
            vm = PostsViewModel(repo)
            store.put("cargando-controlado", vm)
        }
        try {
            compose.setContent { MuroCampusTheme { PantallaLista(vm) } }
            compose.onNodeWithText("Cargando publicaciones…").assertIsDisplayed()
            compose.onNodeWithContentDescription("Cargando publicaciones").assertIsDisplayed()
            compose.runOnIdle {
                assertEquals(UiState.Cargando, vm.lista.value)
                assertTrue(!pendiente.isCompleted)
            }
            val contexto = InstrumentationRegistry.getInstrumentation().targetContext
            File(contexto.filesDir, "cargando-lista-controlado.png").outputStream().use { salida ->
                assertTrue(compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, salida))
            }
        } finally {
            compose.runOnUiThread { store.clear() }
        }
    }

    @Test
    fun tarjetaMuestraDatosTituloNegritaYCuerpoDeDosLineas() {
        val post = Post(4, 1, "Publicacion controlada", "Primera línea\nSegunda línea\nTercera línea")
        compose.setContent { MuroCampusTheme { TarjetaPost(post) } }
        compose.onNodeWithText("Usuario 4").assertIsDisplayed()
        compose.onNodeWithText(post.title).assertIsDisplayed()
        compose.onNodeWithText(post.body).assertIsDisplayed()
        val cuerpo = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(post.body).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(cuerpo) }
        assertEquals(2, cuerpo.single().lineCount)
        assertTrue(cuerpo.single().isLineEllipsized(1))
        val titulo = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(post.title).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(titulo) }
        assertEquals(FontWeight.Bold, titulo.single().layoutInput.style.fontWeight)
    }

    @Test
    fun errorMuestraElMensajeRecibido() {
        compose.setContent { MuroCampusTheme { ErrorConReintento("Error controlado", {}) } }
        compose.onNodeWithText("Error controlado").assertIsDisplayed()
        compose.onNodeWithText("Reintentar").assertIsDisplayed()
    }

    @Test
    fun reintentarEjecutaElCallbackUnaVez() {
        var llamadas = 0
        compose.setContent { MuroCampusTheme { ErrorConReintento("Error controlado", { llamadas++ }) } }
        compose.onNodeWithText("Reintentar").performClick()
        compose.runOnIdle { assertEquals(1, llamadas) }
    }
}
