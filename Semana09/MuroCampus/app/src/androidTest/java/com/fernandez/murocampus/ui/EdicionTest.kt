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
class EdicionTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val store = ViewModelStore()
    private val repo = Falso()
    @After fun limpiar() { compose.runOnUiThread { store.clear() } }
    private fun crear(): PostsViewModel {
        lateinit var vm: PostsViewModel
        compose.runOnUiThread { vm = PostsViewModel(repo); store.put("editar", vm) }
        return vm
    }
    private fun abrir() {
        compose.onNodeWithText("Título original").performClick()
        compose.onNodeWithText("Editar publicación").performScrollTo().performClick()
    }
    private fun mostrar() {
        val vm = crear(); compose.setContent { MuroCampusTheme { PantallaInicio(vm) } }; abrir()
    }
    @Test fun precargaTituloYCuerpoYDeshabilitaSinCambios() {
        mostrar(); compose.onNodeWithText("Título").assertTextContains("Título original")
        compose.onAllNodesWithText("Contenido original").assertCountEquals(2)
        compose.onAllNodesWithText("Contenido original").onLast().assertIsDisplayed()
        compose.onNodeWithText("Guardar cambios").assertIsNotEnabled()
    }
    @Test fun tituloVacioOEspaciosNoEnviaPatch() {
        mostrar(); compose.onNodeWithText("Título").performTextReplacement("   ")
        compose.onNodeWithText("Guardar cambios").assertIsNotEnabled()
        compose.runOnIdle { assertEquals(0, repo.llamadas) }
    }
    @Test fun guardarMuestraConfirmacionYActualizaDetalleComentariosYLista() {
        mostrar(); compose.onNodeWithText("Título").performTextReplacement("Título nuevo")
        compose.onNodeWithText("Guardar cambios").performScrollTo().performClick()
        compose.onNodeWithText("Publicación actualizada").assertIsDisplayed()
        compose.onNodeWithText("Guardar cambios").assertIsNotEnabled()
        compose.onNodeWithText("Volver al detalle").performClick()
        compose.onNodeWithText("Título nuevo").assertIsDisplayed()
        compose.onNodeWithText("Comentarios (1)").assertIsDisplayed()
        pressBack(); compose.onNodeWithText("Título nuevo").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, repo.llamadas); assertEquals(1, repo.getLista) }
    }
    @Test fun falloConservaBorradorYPermiteReintentar() {
        repo.respuesta = { Result.failure(IOException()) }; mostrar()
        compose.onNodeWithText("Título").performTextReplacement("Borrador conservado")
        compose.onNodeWithText("Guardar cambios").performClick()
        compose.onNodeWithText("Sin conexión a internet.", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Título").assertTextContains("Borrador conservado")
        compose.runOnUiThread { repo.respuesta = { Result.success(repo.post.copy(title = it.title)) } }
        compose.onNodeWithText("Guardar cambios").performClick()
        compose.onNodeWithText("Publicación actualizada").assertIsDisplayed()
        compose.runOnIdle { assertEquals(2, repo.llamadas) }
    }
    @Test fun pendienteDeshabilitaTituloYGuardar() {
        repo.respuesta = { awaitCancellation() }; mostrar()
        compose.onNodeWithText("Título").performTextReplacement("Nuevo")
        compose.onNodeWithText("Guardar cambios").performClick()
        compose.onNodeWithText("Guardar cambios").assertIsNotEnabled()
        compose.onNodeWithText("Título").assertIsNotEnabled()
        compose.runOnIdle { assertEquals(1, repo.llamadas) }
    }
    @Test fun restauracionConservaBorradorSinPatchAutomatico() {
        val vm = crear(); val restauracion = StateRestorationTester(compose)
        restauracion.setContent { MuroCampusTheme { PantallaInicio(vm) } }; abrir()
        compose.onNodeWithText("Título").performTextReplacement("Borrador rotado")
        restauracion.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("Título").assertTextContains("Borrador rotado")
        compose.runOnIdle { assertEquals(0, repo.llamadas); assertEquals(1, repo.getLista) }
    }
    @Test fun volverSinGuardarConservaDetalleSinPatch() {
        mostrar(); compose.onNodeWithText("Título").performTextReplacement("Descartar")
        compose.onNodeWithText("Volver al detalle").performScrollTo().performClick()
        compose.onNodeWithText("Título original").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, repo.llamadas) }
    }
    private class Falso : PostRepository {
        val post = Post(1, 2, "Título original", "Contenido original")
        var llamadas = 0
        var getLista = 0
        var respuesta: suspend (EditarPost) -> Result<Post> = { Result.success(post.copy(title = it.title)) }
        override suspend fun listar(): Result<List<Post>> { getLista++; return Result.success(listOf(post)) }
        override suspend fun porUsuario(userId: Int) = Result.success(listOf(post))
        override suspend fun detalle(id: Int) = Result.success(post)
        override suspend fun comentarios(id: Int) = Result.success(listOf(Comentario(id, 1, "Nombre", "a@test", "Comentario original")))
        override suspend fun crear(nuevo: NuevoPost): Result<Post> = error("Llamada inesperada")
        override suspend fun eliminar(id: Int): Result<Unit> = error("Llamada inesperada")
        override suspend fun editar(id: Int, cambios: EditarPost): Result<Post> { llamadas++; return respuesta(cambios) }
    }
}
