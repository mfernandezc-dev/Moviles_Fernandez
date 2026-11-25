package com.fernandez.murocampus.data

import com.fernandez.murocampus.model.Post
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class MensajesErrorTest {
    private val conexion = "Sin conexión a internet.\nRevisa tu red e inténtalo otra vez."

    @Test fun ioExceptionEsConexion() = assertEquals(conexion, mensajeDe(IOException()))
    @Test fun dnsEsConexion() = assertEquals(conexion, mensajeDe(UnknownHostException()))
    @Test fun timeoutEsConexion() = assertEquals(conexion, mensajeDe(SocketTimeoutException()))
    @Test fun http404IncluyeCodigo() = assertEquals("Error del servidor (404)", mensajeDe(http(404)))
    @Test fun http500IncluyeCodigo() = assertEquals("Error del servidor (500)", mensajeDe(http(500)))
    @Test fun serializacionEsRespuestaInesperada() = assertEquals("Respuesta inesperada", mensajeDe(SerializationException()))
    @Test fun excepcionOrdinariaEsDesconocida() = assertEquals("Error desconocido", mensajeDe(IllegalStateException()))
    @Test fun otroThrowableEsDesconocido() = assertEquals("Error desconocido", mensajeDe(AssertionError()))

    private fun http(codigo: Int) = HttpException(
        Response.error<Post>(codigo, "{}".toResponseBody("application/json".toMediaType()))
    )
}
