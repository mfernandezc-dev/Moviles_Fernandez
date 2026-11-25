package com.fernandez.murocampus.data

import com.fernandez.murocampus.data.remote.PostApi
import com.fernandez.murocampus.model.*
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class EliminacionRepositoryTest {
    private fun repo(bloque: (Int) -> Response<Unit>) = PostRepositoryRemote(
        object : PostApi {
            override suspend fun listar(): List<Post> = error("Llamada inesperada a listar")
            override suspend fun detalle(id: Int): Post = error("Llamada inesperada a detalle")
            override suspend fun comentarios(id: Int): List<Comentario> = error("Llamada inesperada a comentarios")
            override suspend fun porUsuario(userId: Int): List<Post> = error("Llamada inesperada a porUsuario")
            override suspend fun crear(nuevo: NuevoPost): Post = error("Llamada inesperada a crear")
            override suspend fun eliminar(id: Int): Response<Unit> = bloque(id)
        }
    )
    @Test fun delegaIdYDevuelveUnitCon200() = runBlocking {
        var recibido = 0
        val resultado = repo { recibido = it; Response.success(Unit) }.eliminar(17)
        assertEquals(17, recibido); assertEquals(Unit, resultado.getOrThrow())
    }
    @Test fun acepta204SinCuerpo() = runBlocking {
        assertTrue(repo { Response.success<Unit>(204, null) }.eliminar(2).isSuccess)
    }
    @Test fun convierte404EnHttpExceptionConRespuestaOriginal() = runBlocking {
        val respuesta = Response.error<Unit>(404, "{}".toResponseBody())
        val error = repo { respuesta }.eliminar(3).exceptionOrNull() as HttpException
        assertEquals(404, error.code()); assertSame(respuesta, error.response())
    }
    @Test fun error500NoEsExito() = runBlocking {
        val error = repo { Response.error(500, "fallo".toResponseBody()) }.eliminar(3).exceptionOrNull()
        assertEquals(500, (error as HttpException).code())
    }
    @Test fun conservaExcepcionDeRedOriginal() = runBlocking {
        val error = IOException("sin red")
        assertSame(error, repo { throw error }.eliminar(3).exceptionOrNull())
    }
    @Test fun relanzaCancelacionOriginal() = runBlocking {
        val error = CancellationException("cancelada")
        try { repo { throw error }.eliminar(3); fail("Debe relanzarse") }
        catch (e: CancellationException) { assertSame(error, e) }
    }
}
