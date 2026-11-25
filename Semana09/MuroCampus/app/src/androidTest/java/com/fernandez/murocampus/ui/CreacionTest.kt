package com.fernandez.murocampus.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModelStore
import androidx.test.espresso.Espresso.closeSoftKeyboard
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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreacionTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val store = ViewModelStore()
    private val repo = RepositoryFalso()

    @After fun limpiar() { compose.runOnUiThread { store.clear() } }

    @Test fun fabVisibleSoloEnLaListaYAbreElFormularioSinPost() {
        mostrar()
        compose.onNodeWithContentDescription("Crear publicación").assertIsDisplayed().performClick()
        compose.onNodeWithText("Título").assertIsDisplayed()
        compose.onNodeWithText("Contenido").assertIsDisplayed()
        compose.onNodeWithTag("publicar").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Crear publicación").assertDoesNotExist()
        compose.runOnIdle { assertEquals(0, repo.enviados.size) }
    }

    @Test fun formularioInicialVacioYBotonDeshabilitado() {
        abrir()
        compose.onNodeWithTag("titulo").assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("")))
        compose.onNodeWithTag("contenido").assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("")))
        compose.onNodeWithTag("publicar").assertIsNotEnabled()
        compose.onNodeWithText("Publicando…").assertDoesNotExist()
    }

    @Test fun soloTituloNoHabilitaPublicar() {
        abrir()
        compose.onNodeWithTag("titulo").performTextInput("Título controlado")
        compose.onNodeWithTag("publicar").assertIsNotEnabled()
    }

    @Test fun soloContenidoNoHabilitaPublicar() {
        abrir()
        compose.onNodeWithTag("contenido").performTextInput("Contenido controlado")
        compose.onNodeWithTag("publicar").assertIsNotEnabled()
    }

    @Test fun camposConEspaciosNoHabilitanPublicar() {
        abrir()
        completar("   ", "   ")
        compose.onNodeWithTag("publicar").assertIsNotEnabled()
        compose.runOnIdle { assertEquals(0, repo.enviados.size) }
    }

    @Test fun ambosCamposValidosHabilitanPublicar() {
        abrir()
        completar()
        compose.onNodeWithTag("publicar").assertIsEnabled()
    }

    @Test fun publicandoMuestraIndicadorYDeshabilitaCamposYBoton() {
        repo.respuesta = { awaitCancellation() }
        abrir()
        completar()
        compose.onNodeWithTag("publicar").performScrollTo().performClick()
        compose.onNodeWithText("Publicando…").performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Publicando publicación").assertIsDisplayed()
        compose.onNodeWithTag("publicar").assertIsNotEnabled()
        compose.onNodeWithTag("titulo").assertIsNotEnabled()
        compose.onNodeWithTag("contenido").assertIsNotEnabled()
    }

    @Test fun solicitudPendienteNoPermiteEnviosDuplicados() {
        repo.respuesta = { awaitCancellation() }
        val vm = abrir()
        completar()
        compose.onNodeWithTag("publicar").performScrollTo().performClick()
        compose.onNodeWithTag("publicar").assertIsNotEnabled()
        compose.runOnUiThread { repeat(10) { vm.publicar("Título controlado", "Contenido controlado") } }
        compose.runOnIdle { assertEquals(1, repo.enviados.size) }
    }

    @Test fun exitoMuestraElIdentificadorRecibidoYConservaLosCampos() {
        abrir()
        completar()
        compose.onNodeWithTag("publicar").performScrollTo().performClick()
        compose.onNodeWithText("Publicado con id 742").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("titulo").assertTextContains("Título controlado")
        compose.onNodeWithTag("contenido").assertTextContains("Contenido controlado")
        compose.runOnIdle { assertEquals(NuevoPost(1, "Título controlado", "Contenido controlado"), repo.enviados.single()) }
    }

    @Test fun errorConservaCamposYPermiteUnNuevoIntento() {
        repo.respuesta = { Result.failure(IOException()) }
        abrir()
        completar()
        compose.onNodeWithTag("publicar").performScrollTo().performClick()
        compose.onNodeWithText("Sin conexión a internet.\nRevisa tu red e inténtalo otra vez.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("titulo").assertTextContains("Título controlado")
        compose.onNodeWithTag("contenido").assertTextContains("Contenido controlado")
        compose.onNodeWithTag("publicar").assertIsEnabled()
        compose.runOnUiThread { repo.respuesta = { Result.success(Post(1, 843, it.title, it.body)) } }
        compose.onNodeWithTag("publicar").performScrollTo().performClick()
        compose.onNodeWithText("Publicado con id 843").performScrollTo().assertIsDisplayed()
        compose.runOnIdle { assertEquals(2, repo.enviados.size) }
    }

    @Test fun regresarConservaElFiltroYLaListaSinDescargar() {
        mostrar()
        compose.onNodeWithTag("filtro-usuario-2").performScrollTo().performClick()
        compose.onNodeWithText("Publicacion usuario 2").assertIsDisplayed()
        compose.onNodeWithContentDescription("Crear publicación").performClick()
        pressBack()
        compose.onNodeWithTag("filtro-usuario-2").assertIsSelected()
        compose.onNodeWithText("Publicacion usuario 2").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, repo.llamadasLista); assertEquals(listOf(2), repo.usuarios) }
    }

    @Test fun fabNoApareceEnElDetalle() {
        mostrar()
        compose.onNodeWithText("Publicacion controlada").performClick()
        compose.onNodeWithText("Comentarios (0)").assertIsDisplayed()
        compose.onNodeWithContentDescription("Crear publicación").assertDoesNotExist()
    }

    @Test fun nuevoFormularioNoConservaCamposNiExitoAnterior() {
        abrir()
        completar()
        compose.onNodeWithTag("publicar").performScrollTo().performClick()
        compose.onNodeWithText("Publicado con id 742").performScrollTo().assertIsDisplayed()
        pressBack()
        compose.onNodeWithContentDescription("Crear publicación").performClick()
        compose.onNodeWithTag("publicar").assertIsNotEnabled()
        compose.onNodeWithText("Publicado con id 742").assertDoesNotExist()
        compose.onNodeWithTag("titulo").assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("")))
        compose.onNodeWithTag("contenido").assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("")))
    }

    @Test fun restauracionConservaPantallaYCamposSinPublicar() {
        val vm = crear()
        val restauracion = StateRestorationTester(compose)
        restauracion.setContent { MuroCampusTheme { PantallaInicio(vm) } }
        compose.onNodeWithContentDescription("Crear publicación").performClick()
        completar()
        closeSoftKeyboard()
        restauracion.emulateSavedInstanceStateRestore()
        compose.onNodeWithTag("titulo").assertTextContains("Título controlado")
        compose.onNodeWithTag("contenido").assertTextContains("Contenido controlado")
        compose.onNodeWithTag("publicar").assertIsEnabled()
        compose.runOnIdle { assertEquals(0, repo.enviados.size) }
    }

    @Test fun restauracionConservaExitoSinRepetirPost() {
        val vm = crear()
        val restauracion = StateRestorationTester(compose)
        restauracion.setContent { MuroCampusTheme { PantallaInicio(vm) } }
        compose.onNodeWithContentDescription("Crear publicación").performClick()
        completar()
        compose.onNodeWithTag("publicar").performScrollTo().performClick()
        compose.onNodeWithText("Publicado con id 742").performScrollTo().assertIsDisplayed()
        restauracion.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("Publicado con id 742").performScrollTo().assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, repo.enviados.size) }
    }

    private fun crear(): PostsViewModel {
        lateinit var vm: PostsViewModel
        compose.runOnUiThread { vm = PostsViewModel(repo); store.put("creacion", vm) }
        return vm
    }
    private fun mostrar(): PostsViewModel {
        val vm = crear()
        compose.setContent { MuroCampusTheme { PantallaInicio(vm) } }
        return vm
    }
    private fun abrir(): PostsViewModel = mostrar().also {
        compose.onNodeWithContentDescription("Crear publicación").performClick()
    }
    private fun completar(titulo: String = "Título controlado", contenido: String = "Contenido controlado") {
        compose.onNodeWithTag("titulo").performTextInput(titulo)
        compose.onNodeWithTag("contenido").performTextInput(contenido)
    }

    private inner class RepositoryFalso : PostRepository {
        val enviados = mutableListOf<NuevoPost>()
        val usuarios = mutableListOf<Int>()
        var llamadasLista = 0
        var respuesta: suspend (NuevoPost) -> Result<Post> = { Result.success(Post(1, 742, it.title, it.body)) }
        override suspend fun crear(nuevo: NuevoPost): Result<Post> { enviados += nuevo; return respuesta(nuevo) }
        override suspend fun listar(): Result<List<Post>> { llamadasLista++; return Result.success(listOf(Post(1, 1, "Publicacion controlada", "Texto"))) }
        override suspend fun porUsuario(userId: Int): Result<List<Post>> { usuarios += userId; return Result.success(listOf(Post(userId, 11, "Publicacion usuario $userId", "Texto"))) }
        override suspend fun detalle(id: Int): Result<Post> = Result.success(Post(1, id, "Publicacion controlada", "Texto completo"))
        override suspend fun comentarios(id: Int): Result<List<Comentario>> = Result.success(emptyList())
    }
}
