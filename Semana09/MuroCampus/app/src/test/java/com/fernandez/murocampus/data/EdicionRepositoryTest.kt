package com.fernandez.murocampus.data

import com.fernandez.murocampus.data.remote.PostApi
import com.fernandez.murocampus.model.*
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class EdicionRepositoryTest {
    private val post = Post(2, 12, "Título devuelto", "Cuerpo original")
    private fun repo(bloque: (Int, EditarPost) -> Post) = PostRepositoryRemote(object : PostApi {
        override suspend fun listar(): List<Post> = error("Llamada inesperada")
        override suspend fun detalle(id: Int): Post = error("Llamada inesperada")
        override suspend fun comentarios(id: Int): List<Comentario> = error("Llamada inesperada")
        override suspend fun porUsuario(userId: Int): List<Post> = error("Llamada inesperada")
        override suspend fun crear(nuevo: NuevoPost): Post = error("Llamada inesperada")
        override suspend fun eliminar(id: Int): Response<Unit> = error("Llamada inesperada")
        override suspend fun editar(id: Int, cambios: EditarPost) = bloque(id, cambios)
    })
    @Test fun serializaExclusivamenteTitle() {
        assertEquals("{\"title\":\"Título nuevo\"}", Json.encodeToString(EditarPost("Título nuevo")))
    }
    @Test fun delegaIdYModeloSinAlterarYUsaPostDevuelto() = runBlocking {
        val cambios = EditarPost("Solicitud")
        val resultado = repo { id, body -> assertEquals(12, id); assertSame(cambios, body); post }.editar(12, cambios)
        assertSame(post, resultado.getOrThrow())
        assertEquals(12, resultado.getOrThrow().id); assertEquals(2, resultado.getOrThrow().userId)
        assertEquals("Cuerpo original", resultado.getOrThrow().body)
    }
    @Test fun conservaErrorOriginal() = runBlocking {
        val error = IOException("sin red")
        assertSame(error, repo { _, _ -> throw error }.editar(12, EditarPost("Nuevo")).exceptionOrNull())
    }
    @Test fun relanzaCancelacion() = runBlocking {
        val error = CancellationException()
        try { repo { _, _ -> throw error }.editar(12, EditarPost("Nuevo")); fail("Debe relanzar") }
        catch (e: CancellationException) { assertSame(error, e) }
    }
    @Test fun patchPorHttpLocalCompruebaMetodoRutaJsonYRespuesta() = runBlocking {
        conServidor(200, Json.encodeToString(post)) { api, requests ->
            assertEquals(post, PostRepositoryRemote(api).editar(12, EditarPost("Título nuevo")).getOrThrow())
            assertEquals(listOf("PATCH", "/posts/12", "{\"title\":\"Título nuevo\"}"), requests.single())
        }
    }
    @Test fun patchHttp500EsFailureConCodigo() = runBlocking {
        conServidor(500, "{\"error\":\"controlado\"}") { api, requests ->
            val error = PostRepositoryRemote(api).editar(12, EditarPost("Nuevo")).exceptionOrNull() as retrofit2.HttpException
            assertEquals(500, error.code()); assertEquals("PATCH", requests.single()[0])
        }
    }
    @Test fun deletePorHttpLocalCompruebaResponseUnitYMetodo() = runBlocking {
        conServidor(200, "{}") { api, requests ->
            assertEquals(Unit, PostRepositoryRemote(api).eliminar(12).getOrThrow())
            assertEquals(listOf("DELETE", "/posts/12", ""), requests.single())
        }
    }
    private suspend fun conServidor(codigo: Int, body: String, test: suspend (PostApi, MutableList<List<String>>) -> Unit) {
        val requests = java.util.Collections.synchronizedList(mutableListOf<List<String>>())
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            requests += listOf(exchange.requestMethod, exchange.requestURI.toString(), exchange.requestBody.readBytes().toString(Charsets.UTF_8))
            val bytes = body.toByteArray(Charsets.UTF_8)
            exchange.responseHeaders.add("Content-Type", "application/json; charset=utf-8")
            exchange.sendResponseHeaders(codigo, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }
        server.start()
        try {
            val api = Retrofit.Builder().baseUrl("http://127.0.0.1:${server.address.port}/")
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType())).build().create(PostApi::class.java)
            test(api, requests)
        } finally { server.stop(0) }
    }
}
