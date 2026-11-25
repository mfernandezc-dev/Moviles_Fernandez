package com.fernandez.murocampus.data

import com.fernandez.murocampus.data.remote.PostApi
import com.fernandez.murocampus.model.Comentario
import com.fernandez.murocampus.model.NuevoPost
import com.fernandez.murocampus.model.Post
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

interface PostRepository {

    suspend fun listar(): Result<List<Post>>

    suspend fun detalle(id: Int): Result<Post>

    suspend fun comentarios(id: Int): Result<List<Comentario>>

    suspend fun porUsuario(userId: Int): Result<List<Post>>

    suspend fun crear(nuevo: NuevoPost): Result<Post>

    suspend fun eliminar(id: Int): Result<Unit>
}

class PostRepositoryRemote(
    private val api: PostApi
) : PostRepository {

    override suspend fun eliminar(id: Int): Result<Unit> = llamar {
        val respuesta = api.eliminar(id)
        if (!respuesta.isSuccessful) throw HttpException(respuesta)
    }

    override suspend fun listar(): Result<List<Post>> =
        llamar { api.listar() }

    override suspend fun detalle(id: Int): Result<Post> =
        llamar { api.detalle(id) }

    override suspend fun comentarios(
        id: Int
    ): Result<List<Comentario>> =
        llamar { api.comentarios(id) }

    override suspend fun porUsuario(
        userId: Int
    ): Result<List<Post>> =
        llamar { api.porUsuario(userId) }

    override suspend fun crear(
        nuevo: NuevoPost
    ): Result<Post> =
        llamar { api.crear(nuevo) }

    private suspend fun <T> llamar(
        bloque: suspend () -> T
    ): Result<T> =
        try {
            Result.success(bloque())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
