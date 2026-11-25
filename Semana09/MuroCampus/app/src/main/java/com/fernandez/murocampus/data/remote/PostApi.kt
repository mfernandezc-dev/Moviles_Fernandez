package com.fernandez.murocampus.data.remote

import com.fernandez.murocampus.model.Comentario
import com.fernandez.murocampus.model.NuevoPost
import com.fernandez.murocampus.model.Post
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import com.fernandez.murocampus.model.EditarPost

interface PostApi {

    @PATCH("posts/{id}")
    suspend fun editar(@Path("id") id: Int, @Body cambios: EditarPost): Post

    @DELETE("posts/{id}")
    suspend fun eliminar(@Path("id") id: Int): Response<Unit>

    @GET("posts")
    suspend fun listar(): List<Post>

    @GET("posts/{id}")
    suspend fun detalle(
        @Path("id") id: Int
    ): Post

    @GET("posts/{id}/comments")
    suspend fun comentarios(
        @Path("id") id: Int
    ): List<Comentario>

    @GET("posts")
    suspend fun porUsuario(
        @Query("userId") userId: Int
    ): List<Post>

    @POST("posts")
    suspend fun crear(
        @Body nuevo: NuevoPost
    ): Post
}
