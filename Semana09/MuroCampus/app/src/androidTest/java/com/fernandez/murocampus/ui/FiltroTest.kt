package com.fernandez.murocampus.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FiltroTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val store = ViewModelStore()
    private val repo = RepositoryFalso()

    @After fun limpiar() { compose.runOnUiThread { store.clear() } }

    @Test fun todosExisteYEstaSeleccionadoInicialmente() {
        mostrar()
        compose.onNodeWithTag("filtro-todos").assertIsDisplayed().assertIsSelected()
        compose.onNodeWithTag("filtro-usuario-1").assertIsNotSelected()
    }

    @Test fun existenLosDiezUsuariosYLaFilaSeDesplazaHorizontalmente() {
        mostrar()
        for (id in 1..10) {
            compose.onNodeWithTag("filtros").performScrollToIndex(id)
            compose.onNodeWithTag("filtro-usuario-$id").assertIsDisplayed()
        }
        compose.onNodeWithTag("filtros").performScrollToIndex(0)
        compose.onNodeWithTag("filtro-todos").assertIsDisplayed()
    }

    @Test fun seleccionarUsuarioInvocaElFiltroYMuestraSusDatos() {
        mostrar()
        compose.onNodeWithTag("filtro-usuario-2").performScrollTo().performClick().assertIsSelected()
        compose.onNodeWithTag("filtro-todos").assertIsNotSelected()
        compose.onNodeWithText("Publicacion controlada 2").assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf(2), repo.usuarios) }
    }

    @Test fun todosVuelveAConsultarLaListaCompleta() {
        mostrar()
        compose.onNodeWithTag("filtro-usuario-2").performScrollTo().performClick()
        compose.onNodeWithTag("filtro-todos").performScrollTo().performClick().assertIsSelected()
        compose.onNodeWithText("Lista completa controlada").assertIsDisplayed()
        compose.runOnIdle { assertEquals(2, repo.llamadasLista) }
    }

    @Test fun listaVaciaMuestraSinPublicacionesYMantieneLosFiltros() {
        repo.respuestaUsuario = { Result.success(emptyList()) }
        mostrar()
        compose.onNodeWithTag("filtro-usuario-2").performScrollTo().performClick()
        compose.onNodeWithText("Sin publicaciones").assertIsDisplayed()
        compose.onNodeWithTag("filtro-usuario-2").assertIsSelected().assertIsDisplayed()
        compose.onNodeWithTag("filtro-todos").performClick()
        compose.onNodeWithText("Lista completa controlada").assertIsDisplayed()
    }

    @Test fun filtrosPermanecenDisponiblesDuranteCargando() {
        repo.respuestaUsuario = { awaitCancellation() }
        mostrar()
        compose.onNodeWithTag("filtro-usuario-2").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Cargando publicaciones").assertIsDisplayed()
        compose.onNodeWithTag("filtro-usuario-3").performScrollTo().performClick().assertIsSelected()
        compose.runOnIdle { assertEquals(listOf(2, 3), repo.usuarios) }
    }

    @Test fun filtrosPermanecenDisponiblesDuranteError() {
        repo.respuestaUsuario = { Result.failure(IOException()) }
        mostrar()
        compose.onNodeWithTag("filtro-usuario-2").performScrollTo().performClick()
        compose.onNodeWithText("Reintentar").assertIsDisplayed()
        compose.onNodeWithTag("filtro-usuario-2").assertIsSelected()
        compose.onNodeWithTag("filtro-todos").performScrollTo().performClick()
        compose.onNodeWithText("Lista completa controlada").assertIsDisplayed()
    }

    @Test fun reintentarConservaYConsultaElUsuarioSeleccionado() {
        repo.respuestaUsuario = { Result.failure(IOException()) }
        mostrar()
        compose.onNodeWithTag("filtro-usuario-3").performScrollTo().performClick()
        compose.onNodeWithText("Reintentar").assertIsDisplayed()
        compose.runOnUiThread { repo.respuestaUsuario = { Result.success(listOf(post(it))) } }
        compose.onNodeWithText("Reintentar").performClick()
        compose.onNodeWithTag("filtro-usuario-3").assertIsSelected()
        compose.onNodeWithText("Publicacion controlada 3").assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf(3, 3), repo.usuarios) }
    }

    @Test fun usuarioDiezPuedeSeleccionarseTrasDesplazarLaFila() {
        mostrar()
        compose.onNodeWithTag("filtros").performScrollToIndex(10)
        compose.onNodeWithTag("filtro-usuario-10").performClick().assertIsSelected()
        compose.onNodeWithText("Publicacion controlada 10").assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf(10), repo.usuarios) }
    }

    @Test fun regresarDelDetalleConservaFiltroYDatosSinRecargar() {
        val vm = crear()
        compose.setContent { MuroCampusTheme { PantallaInicio(vm) } }
        compose.onNodeWithTag("filtro-usuario-2").performScrollTo().performClick()
        compose.onNodeWithText("Publicacion controlada 2").performClick()
        compose.onNodeWithText("Comentarios (0)").assertIsDisplayed()
        pressBack()
        compose.onNodeWithTag("filtro-usuario-2").assertIsSelected()
        compose.onNodeWithText("Publicacion controlada 2").assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf(2), repo.usuarios); assertEquals(1, repo.llamadasLista) }
    }

    private fun crear(): PostsViewModel {
        lateinit var vm: PostsViewModel
        compose.runOnUiThread { vm = PostsViewModel(repo); store.put("filtro", vm) }
        return vm
    }

    private fun mostrar() {
        val vm = crear()
        compose.setContent { MuroCampusTheme { PantallaLista(vm) } }
    }

    private fun post(id: Int) = Post(id, id * 10, "Publicacion controlada $id", "Texto del usuario $id")

    private inner class RepositoryFalso : PostRepository {
        var llamadasLista = 0
        val usuarios = mutableListOf<Int>()
        var respuestaUsuario: suspend (Int) -> Result<List<Post>> = { Result.success(listOf(post(it))) }
        override suspend fun listar(): Result<List<Post>> {
            llamadasLista++
            return Result.success(listOf(Post(1, 1, "Lista completa controlada", "Texto de la lista")))
        }
        override suspend fun porUsuario(userId: Int): Result<List<Post>> { usuarios += userId; return respuestaUsuario(userId) }
        override suspend fun detalle(id: Int): Result<Post> = Result.success(post(id / 10))
        override suspend fun comentarios(id: Int): Result<List<Comentario>> = Result.success(emptyList())
        override suspend fun crear(nuevo: NuevoPost): Result<Post> = error("No se utiliza")
    }
}
