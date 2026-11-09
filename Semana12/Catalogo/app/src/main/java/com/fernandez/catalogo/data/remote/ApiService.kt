package com.fernandez.catalogo.data.remote

import com.fernandez.catalogo.data.remote.dto.ItemDto
import com.fernandez.catalogo.data.remote.dto.ProductosResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("products/category/laptops")
    suspend fun listar(): ProductosResponseDto

    @GET("products/{id}")
    suspend fun obtenerDetalle(@Path("id") id: Int): ItemDto
}
