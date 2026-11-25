package com.fernandez.murocampus.data.remote

import com.fernandez.murocampus.model.Comentario
import com.fernandez.murocampus.model.NuevoPost
import com.fernandez.murocampus.model.Post
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import kotlin.coroutines.Continuation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.DELETE
import retrofit2.Response

class PostApiTest {
    private val metodos = PostApi::class.java.declaredMethods.toList()

    @Test
    fun postApiEsUnaInterfaz() {
        assertTrue(PostApi::class.java.isInterface)
    }

    @Test
    fun exponeExactamenteLasCincoFuncionesObligatorias() {
        assertEquals(6, metodos.size)
        assertEquals(
            setOf("listar", "detalle", "comentarios", "porUsuario", "crear", "eliminar"),
            metodos.map { it.name }.toSet()
        )
    }

    @Test
    fun listarUtilizaGetPosts() {
        assertGet("listar", "posts")
    }

    @Test
    fun detalleUtilizaGetConIdEnLaRuta() {
        assertGet("detalle", "posts/{id}")
    }

    @Test
    fun detalleTienePathId() {
        assertEquals("id", anotacionParametro<Path>("detalle").value)
    }

    @Test
    fun comentariosUtilizaGetConIdEnLaRuta() {
        assertGet("comentarios", "posts/{id}/comments")
    }

    @Test
    fun comentariosTienePathId() {
        assertEquals("id", anotacionParametro<Path>("comentarios").value)
    }

    @Test
    fun porUsuarioUtilizaGetPosts() {
        assertGet("porUsuario", "posts")
    }

    @Test
    fun porUsuarioTieneQueryUserId() {
        assertEquals("userId", anotacionParametro<Query>("porUsuario").value)
    }

    @Test
    fun crearUtilizaPostPosts() {
        val metodo = metodo("crear")
        val anotacion = metodo.getAnnotation(POST::class.java)

        assertNotNull(anotacion)
        assertEquals("posts", anotacion!!.value)
        assertNull(metodo.getAnnotation(GET::class.java))
    }

    @Test
    fun crearTieneBody() {
        anotacionParametro<Body>("crear")
    }

    @Test
    fun todasLasRutasSonRelativasSinBarraInicial() {
        metodos.forEach { metodo ->
            val rutas = listOfNotNull(
                metodo.getAnnotation(GET::class.java)?.value,
                metodo.getAnnotation(POST::class.java)?.value,
                metodo.getAnnotation(DELETE::class.java)?.value
            )

            assertEquals("${metodo.name} debe tener una operacion HTTP", 1, rutas.size)
            assertTrue(rutas.single().isNotEmpty())
            assertFalse(rutas.single().startsWith("/"))
        }
    }

    @Test
    fun todasLasFuncionesTienenFirmaJvmSuspendSinImplementacion() {
        metodos.forEach { metodo ->
            assertEquals(Any::class.java, metodo.returnType)
            assertEquals(Continuation::class.java, metodo.parameterTypes.last())
            assertEquals(1, metodo.parameterTypes.count { it == Continuation::class.java })
            assertTrue(Modifier.isPublic(metodo.modifiers))
            assertTrue(Modifier.isAbstract(metodo.modifiers))
            assertFalse(Modifier.isStatic(metodo.modifiers))
            assertFalse(metodo.isDefault)
        }
    }

    @Test
    fun parametrosYRetornosCumplenLosContratos() {
        assertEquals(
            listOf(Continuation::class.java),
            metodo("listar").parameterTypes.toList()
        )
        listOf("detalle", "comentarios", "porUsuario", "eliminar").forEach { nombre ->
            assertEquals(
                listOf(Int::class.javaPrimitiveType, Continuation::class.java),
                metodo(nombre).parameterTypes.toList()
            )
        }
        assertEquals(
            listOf(NuevoPost::class.java, Continuation::class.java),
            metodo("crear").parameterTypes.toList()
        )

        assertRetornoLista("listar", Post::class.java)
        assertEquals(Post::class.java, retornoSuspend("detalle"))
        assertRetornoLista("comentarios", Comentario::class.java)
        assertRetornoLista("porUsuario", Post::class.java)
        assertEquals(Post::class.java, retornoSuspend("crear"))
        val respuesta = retornoSuspend("eliminar") as ParameterizedType
        assertEquals(Response::class.java, respuesta.rawType)
        assertEquals(Unit::class.java, sinComodin(respuesta.actualTypeArguments.single()))
    }

    @Test fun eliminarUsaDeleteConPathId() {
        assertEquals("posts/{id}", metodo("eliminar").getAnnotation(DELETE::class.java)!!.value)
        assertEquals("id", anotacionParametro<Path>("eliminar").value)
    }

    private fun metodo(nombre: String): Method = metodos.single { it.name == nombre }

    private fun assertGet(nombre: String, ruta: String) {
        val metodo = metodo(nombre)
        val anotacion = metodo.getAnnotation(GET::class.java)

        assertNotNull(anotacion)
        assertEquals(ruta, anotacion!!.value)
        assertNull(metodo.getAnnotation(POST::class.java))
    }

    private inline fun <reified T : Annotation> anotacionParametro(nombre: String): T {
        val anotaciones = metodo(nombre).parameterAnnotations.first()
        assertEquals("$nombre debe tener una anotacion en su parametro HTTP", 1, anotaciones.size)
        assertTrue(anotaciones.single() is T)
        return anotaciones.single() as T
    }

    private fun retornoSuspend(nombre: String): Type {
        // En JVM, suspend devuelve Object y conserva T en Continuation<? super T>.
        val tipo = metodo(nombre).genericParameterTypes.last()
        assertTrue(tipo is ParameterizedType)
        val continuacion = tipo as ParameterizedType
        assertEquals(Continuation::class.java, continuacion.rawType)
        return sinComodin(continuacion.actualTypeArguments.single())
    }

    private fun assertRetornoLista(nombre: String, elemento: Class<*>) {
        val tipo = retornoSuspend(nombre)
        assertTrue(tipo is ParameterizedType)
        val lista = tipo as ParameterizedType
        assertEquals(List::class.java, lista.rawType)
        assertEquals(elemento, sinComodin(lista.actualTypeArguments.single()))
    }

    private fun sinComodin(tipo: Type): Type = when (tipo) {
        is WildcardType -> sinComodin(tipo.lowerBounds.firstOrNull() ?: tipo.upperBounds.single())
        else -> tipo
    }
}
