package com.fernandez.murocampus.data

import com.fernandez.murocampus.data.remote.PostApi
import com.fernandez.murocampus.model.Comentario
import com.fernandez.murocampus.model.NuevoPost
import com.fernandez.murocampus.model.Post
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class PostRepositoryTest {
    private val post = Post(2, 7, "Titulo de prueba", "Contenido de prueba")
    private val otroPost = Post(3, 8, "Otro titulo", "Otro contenido")
    private val comentario = Comentario(9, 10, "Comentario", "persona@example.test", "Respuesta")
    private val nuevo = NuevoPost(2, "Nuevo titulo", "Nuevo contenido")
    private val creado = Post(nuevo.userId, 101, nuevo.title, nuevo.body)

    @Test
    fun listarDevuelveSuccessYDelegaUnaSolaVez() = runBlocking {
        val api = PostApiFalsa()
        val repository: PostRepository = PostRepositoryRemote(api)

        val resultado = repository.listar()

        assertTrue(resultado.isSuccess)
        assertEquals(listOf(post, otroPost), resultado.getOrThrow())
        assertSame(api.publicaciones, resultado.getOrThrow())
        assertUnaLlamada(api, Llamada("listar"))
    }

    @Test
    fun detalleDevuelveSuccessYTransmiteId() = runBlocking {
        val api = PostApiFalsa()
        val repository: PostRepository = PostRepositoryRemote(api)

        val resultado = repository.detalle(7)

        assertTrue(resultado.isSuccess)
        assertEquals(post, resultado.getOrThrow())
        assertSame(api.publicacion, resultado.getOrThrow())
        assertUnaLlamada(api, Llamada("detalle", 7))
    }

    @Test
    fun comentariosDevuelveSuccessYTransmiteId() = runBlocking {
        val api = PostApiFalsa()
        val repository: PostRepository = PostRepositoryRemote(api)

        val resultado = repository.comentarios(9)

        assertTrue(resultado.isSuccess)
        assertEquals(listOf(comentario), resultado.getOrThrow())
        assertSame(api.respuestaComentarios, resultado.getOrThrow())
        assertUnaLlamada(api, Llamada("comentarios", 9))
    }

    @Test
    fun porUsuarioDevuelveSuccessYTransmiteUserId() = runBlocking {
        val api = PostApiFalsa()
        val repository: PostRepository = PostRepositoryRemote(api)

        val resultado = repository.porUsuario(2)

        assertTrue(resultado.isSuccess)
        assertEquals(listOf(post), resultado.getOrThrow())
        assertSame(api.publicacionesUsuario, resultado.getOrThrow())
        assertUnaLlamada(api, Llamada("porUsuario", 2))
    }

    @Test
    fun crearDevuelveSuccessYTransmiteElMismoNuevoPost() = runBlocking {
        val api = PostApiFalsa()
        val repository: PostRepository = PostRepositoryRemote(api)

        val resultado = repository.crear(nuevo)

        assertTrue(resultado.isSuccess)
        assertEquals(creado, resultado.getOrThrow())
        assertSame(api.publicacionCreada, resultado.getOrThrow())
        assertUnaLlamada(api, Llamada("crear", nuevo))
        assertSame(nuevo, api.llamadas.single().parametro)
    }

    @Test
    fun listarConservaLasExcepcionesOrdinarias() = assertErroresOrdinarios(Operacion.LISTAR)

    @Test
    fun detalleConservaLasExcepcionesOrdinarias() = assertErroresOrdinarios(Operacion.DETALLE)

    @Test
    fun comentariosConservaLasExcepcionesOrdinarias() = assertErroresOrdinarios(Operacion.COMENTARIOS)

    @Test
    fun porUsuarioConservaLasExcepcionesOrdinarias() = assertErroresOrdinarios(Operacion.POR_USUARIO)

    @Test
    fun crearConservaLasExcepcionesOrdinarias() = assertErroresOrdinarios(Operacion.CREAR)

    @Test
    fun listarRelanzaLaCancelacionOriginal() = assertCancelacion(Operacion.LISTAR)

    @Test
    fun detalleRelanzaLaCancelacionOriginal() = assertCancelacion(Operacion.DETALLE)

    @Test
    fun comentariosRelanzaLaCancelacionOriginal() = assertCancelacion(Operacion.COMENTARIOS)

    @Test
    fun porUsuarioRelanzaLaCancelacionOriginal() = assertCancelacion(Operacion.POR_USUARIO)

    @Test
    fun crearRelanzaLaCancelacionOriginal() = assertCancelacion(Operacion.CREAR)

    @Test
    fun listarAceptaUnaListaVaciaComoSuccess() = runBlocking {
        val api = PostApiFalsa().apply { publicaciones = emptyList() }

        val resultado = PostRepositoryRemote(api).listar()

        assertTrue(resultado.isSuccess)
        assertSame(api.publicaciones, resultado.getOrThrow())
        assertUnaLlamada(api, Llamada("listar"))
    }

    @Test
    fun comentariosAceptaUnaListaVaciaComoSuccess() = runBlocking {
        val api = PostApiFalsa().apply { respuestaComentarios = emptyList() }

        val resultado = PostRepositoryRemote(api).comentarios(9)

        assertTrue(resultado.isSuccess)
        assertSame(api.respuestaComentarios, resultado.getOrThrow())
        assertUnaLlamada(api, Llamada("comentarios", 9))
    }

    @Test
    fun porUsuarioAceptaUnaListaVaciaComoSuccess() = runBlocking {
        val api = PostApiFalsa().apply { publicacionesUsuario = emptyList() }

        val resultado = PostRepositoryRemote(api).porUsuario(2)

        assertTrue(resultado.isSuccess)
        assertSame(api.publicacionesUsuario, resultado.getOrThrow())
        assertUnaLlamada(api, Llamada("porUsuario", 2))
    }

    @Test
    fun cancelarUnaCorrutinaSuspendidaNoSeConvierteEnFailure() = runBlocking {
        val inicio = CompletableDeferred<Unit>()
        val limpieza = CompletableDeferred<Unit>()
        val api = PostApiFalsa().apply {
            antesDeResponder = {
                inicio.complete(Unit)
                try {
                    awaitCancellation()
                } finally {
                    limpieza.complete(Unit)
                }
            }
        }
        val repository: PostRepository = PostRepositoryRemote(api)
        var resultadoDevuelto: Result<List<Post>>? = null
        var cancelacionObservada: CancellationException? = null
        val tarea = launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                resultadoDevuelto = repository.listar()
            } catch (e: CancellationException) {
                cancelacionObservada = e
                throw e
            }
        }

        // UNDISPATCHED alcanza la suspension antes de continuar: no se usan sleeps ni delays.
        assertTrue(inicio.isCompleted)
        assertTrue(tarea.isActive)
        val motivo = CancellationException("Cancelacion real controlada")
        tarea.cancel(motivo)
        tarea.join()

        assertTrue(tarea.isCancelled)
        assertTrue(limpieza.isCompleted)
        assertNull(resultadoDevuelto)
        // La recuperacion de stacktraces puede copiar la excepcion al reanudar la suspension.
        assertEquals(motivo.message, cancelacionObservada?.message)
        assertTrue(
            generateSequence<Throwable>(cancelacionObservada) { it.cause }.any { it === motivo }
        )
        assertUnaLlamada(api, Llamada("listar"))
    }

    @Test
    fun erroresFatalesNoSeOcultanComoResult() = runBlocking {
        Operacion.entries.forEach { operacion ->
            val original = AssertionError("Error fatal controlado")
            val api = PostApiFalsa().apply { error = original }

            try {
                ejecutar(PostRepositoryRemote(api), operacion)
                fail("$operacion debe propagar Error")
            } catch (e: AssertionError) {
                assertSame(original, e)
            }
            assertUnaLlamada(api, llamadaEsperada(operacion))
        }
    }

    private fun assertErroresOrdinarios(operacion: Operacion) = runBlocking {
        excepcionesOrdinarias().forEach { original ->
            val api = PostApiFalsa().apply { error = original }

            val resultado = ejecutar(PostRepositoryRemote(api), operacion)

            assertTrue("$operacion debe fallar con ${original.javaClass.simpleName}", resultado.isFailure)
            assertNull(resultado.getOrNull())
            assertSame(original, resultado.exceptionOrNull())
            assertUnaLlamada(api, llamadaEsperada(operacion))
        }
    }

    private fun assertCancelacion(operacion: Operacion) = runBlocking {
        val original = CancellationException("Cancelacion controlada de $operacion")
        val api = PostApiFalsa().apply { error = original }

        try {
            ejecutar(PostRepositoryRemote(api), operacion)
            fail("$operacion debe relanzar CancellationException, no devolver Result")
        } catch (e: CancellationException) {
            assertSame(original, e)
        }
        assertUnaLlamada(api, llamadaEsperada(operacion))
    }

    private fun excepcionesOrdinarias(): List<Exception> = listOf(
        IOException("Error de entrada/salida controlado"),
        UnknownHostException("host de prueba"),
        SocketTimeoutException("Tiempo agotado controlado"),
        HttpException(Response.error<Post>(404, "{}".toResponseBody("application/json".toMediaType()))),
        HttpException(Response.error<Post>(500, "{}".toResponseBody("application/json".toMediaType()))),
        SerializationException("JSON incompatible controlado"),
        IllegalStateException("Estado invalido controlado")
    )

    private suspend fun ejecutar(repository: PostRepository, operacion: Operacion): Result<*> =
        when (operacion) {
            Operacion.LISTAR -> repository.listar()
            Operacion.DETALLE -> repository.detalle(7)
            Operacion.COMENTARIOS -> repository.comentarios(9)
            Operacion.POR_USUARIO -> repository.porUsuario(2)
            Operacion.CREAR -> repository.crear(nuevo)
        }

    private fun llamadaEsperada(operacion: Operacion): Llamada = when (operacion) {
        Operacion.LISTAR -> Llamada("listar")
        Operacion.DETALLE -> Llamada("detalle", 7)
        Operacion.COMENTARIOS -> Llamada("comentarios", 9)
        Operacion.POR_USUARIO -> Llamada("porUsuario", 2)
        Operacion.CREAR -> Llamada("crear", nuevo)
    }

    private fun assertUnaLlamada(api: PostApiFalsa, esperada: Llamada) {
        assertEquals(1, api.llamadas.size)
        assertEquals(esperada, api.llamadas.single())
    }

    private enum class Operacion {
        LISTAR, DETALLE, COMENTARIOS, POR_USUARIO, CREAR
    }

    private data class Llamada(val metodo: String, val parametro: Any? = null)

    private inner class PostApiFalsa : PostApi {
        override suspend fun eliminar(id: Int): retrofit2.Response<Unit> = error("Llamada inesperada a eliminar")
        var publicaciones = listOf(post, otroPost)
        var publicacion = post
        var respuestaComentarios = listOf(comentario)
        var publicacionesUsuario = listOf(post)
        var publicacionCreada = creado
        var error: Throwable? = null
        var antesDeResponder: suspend () -> Unit = {}
        val llamadas = mutableListOf<Llamada>()

        override suspend fun listar(): List<Post> =
            responder("listar", null, publicaciones)

        override suspend fun detalle(id: Int): Post =
            responder("detalle", id, publicacion)

        override suspend fun comentarios(id: Int): List<Comentario> =
            responder("comentarios", id, respuestaComentarios)

        override suspend fun porUsuario(userId: Int): List<Post> =
            responder("porUsuario", userId, publicacionesUsuario)

        override suspend fun crear(nuevo: NuevoPost): Post =
            responder("crear", nuevo, publicacionCreada)

        private suspend fun <T> responder(metodo: String, parametro: Any?, valor: T): T {
            llamadas += Llamada(metodo, parametro)
            antesDeResponder()
            error?.let { throw it }
            return valor
        }
    }
}
