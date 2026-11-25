package com.fernandez.murocampus.data.remote

import com.fernandez.murocampus.BuildConfig
import com.fernandez.murocampus.model.Comentario
import com.fernandez.murocampus.model.NuevoPost
import com.fernandez.murocampus.model.Post
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Dns
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class RetrofitClientTest {
    private val postJson = """
        {
            "userId": 2,
            "id": 7,
            "title": "Titulo de prueba",
            "body": "Contenido de prueba",
            "extra": {"origen": "respuesta controlada"}
        }
    """.trimIndent()

    private val postEsperado = Post(2, 7, "Titulo de prueba", "Contenido de prueba")

    @Test
    fun apiPuedeInicializarse() {
        assertNotNull(RetrofitClient.api)
    }

    @Test
    fun apiImplementaPostApi() {
        assertTrue(PostApi::class.java.isInstance(RetrofitClient.api))
    }

    @Test
    fun apiReutilizaLaMismaInstancia() {
        assertSame(RetrofitClient.api, RetrofitClient.api)
    }

    @Test
    fun jsonDeProduccionIgnoraCamposAdicionales() {
        val json = jsonProduccion()

        assertTrue(json.configuration.ignoreUnknownKeys)
        assertEquals(postEsperado, json.decodeFromString<Post>(postJson))
    }

    @Test
    fun clienteDeProduccionConfiguraLoggingSegunVariante() {
        val interceptores = clienteProduccion().interceptors
        val logging = interceptores.filterIsInstance<HttpLoggingInterceptor>().single()
        val nivelEsperado = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

        assertEquals(1, interceptores.size)
        assertEquals(nivelEsperado, logging.level)
    }

    @Test
    fun listarConvierteRespuestaSimulada() = runBlocking {
        val solicitud = AtomicReference<Request>()
        val api = apiSimulada("[$postJson]", solicitud)

        assertEquals(listOf(postEsperado), api.listar())
        assertSolicitud(solicitud.get(), "GET", "https://jsonplaceholder.typicode.com/posts")
    }

    @Test
    fun detalleConvierteRespuestaSimulada() = runBlocking {
        val solicitud = AtomicReference<Request>()
        val api = apiSimulada(postJson, solicitud)

        assertEquals(postEsperado, api.detalle(7))
        assertSolicitud(solicitud.get(), "GET", "https://jsonplaceholder.typicode.com/posts/7")
    }

    @Test
    fun comentariosConvierteRespuestaSimulada() = runBlocking {
        val respuesta = """
            [{
                "postId": 7,
                "id": 8,
                "name": "Comentario de prueba",
                "email": "persona@example.test",
                "body": "Respuesta de prueba",
                "extra": true
            }]
        """.trimIndent()
        val solicitud = AtomicReference<Request>()
        val api = apiSimulada(respuesta, solicitud)

        assertEquals(
            listOf(Comentario(7, 8, "Comentario de prueba", "persona@example.test", "Respuesta de prueba")),
            api.comentarios(7)
        )
        assertSolicitud(solicitud.get(), "GET", "https://jsonplaceholder.typicode.com/posts/7/comments")
    }

    @Test
    fun porUsuarioConstruyeQueryYConvierteRespuestaSimulada() = runBlocking {
        val solicitud = AtomicReference<Request>()
        val api = apiSimulada("[$postJson]", solicitud)

        assertEquals(listOf(postEsperado), api.porUsuario(2))
        assertSolicitud(solicitud.get(), "GET", "https://jsonplaceholder.typicode.com/posts?userId=2")
        assertEquals("2", solicitud.get().url.queryParameter("userId"))
    }

    @Test
    fun crearSerializaBodySinIdYConvierteRespuestaSimulada() = runBlocking {
        val respuesta = """
            {"userId": 2, "id": 101, "title": "Titulo de prueba", "body": "Contenido de prueba", "extra": true}
        """.trimIndent()
        val solicitud = AtomicReference<Request>()
        val api = apiSimulada(respuesta, solicitud, codigo = 201)
        val nuevo = NuevoPost(2, "Titulo de prueba", "Contenido de prueba")

        assertEquals(Post(2, 101, nuevo.title, nuevo.body), api.crear(nuevo))
        assertSolicitud(solicitud.get(), "POST", "https://jsonplaceholder.typicode.com/posts")

        val body = solicitud.get().body!!
        val contenido = Buffer().also { body.writeTo(it) }.readUtf8()
        val json = jsonProduccion()
        assertEquals(
            json.parseToJsonElement("""{"userId": 2, "title": "Titulo de prueba", "body": "Contenido de prueba"}"""),
            json.parseToJsonElement(contenido)
        )
        assertFalse(contenido.contains("\"id\""))
        assertEquals("application", body.contentType()!!.type)
        assertEquals("json", body.contentType()!!.subtype)
    }

    // Lectura de configuracion real, sin reemplazar campos del singleton de produccion.
    private fun jsonProduccion(): Json = RetrofitClient::class.java
        .getDeclaredField("json")
        .apply { isAccessible = true }
        .get(null) as Json

    private fun clienteProduccion(): OkHttpClient = RetrofitClient::class.java
        .getDeclaredField("cliente")
        .apply { isAccessible = true }
        .get(null) as OkHttpClient

    // Proxy alternativo para probar conversiones sin invocar RetrofitClient.api ni acceder a internet.
    // Reutiliza Json y copia OkHttp; el interceptor devuelve datos controlados antes de conectar.
    private fun apiSimulada(
        respuesta: String,
        solicitud: AtomicReference<Request>,
        codigo: Int = 200
    ): PostApi {
        val tipo = "application/json".toMediaType()
        val clientePrueba = clienteProduccion().newBuilder()
            .dns(object : Dns {
                override fun lookup(hostname: String): List<InetAddress> {
                    throw AssertionError("Las pruebas no deben resolver DNS ni usar internet")
                }
            })
            .addInterceptor { cadena ->
                solicitud.set(cadena.request())
                Response.Builder()
                    .request(cadena.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(codigo)
                    .message("Respuesta simulada")
                    .body(respuesta.toResponseBody(tipo))
                    .build()
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .client(clientePrueba)
            .addConverterFactory(jsonProduccion().asConverterFactory(tipo))
            .validateEagerly(true)
            .build()
            .create(PostApi::class.java)
    }

    private fun assertSolicitud(solicitud: Request, metodo: String, url: String) {
        assertEquals(metodo, solicitud.method)
        assertEquals(url, solicitud.url.toString())
    }
}
