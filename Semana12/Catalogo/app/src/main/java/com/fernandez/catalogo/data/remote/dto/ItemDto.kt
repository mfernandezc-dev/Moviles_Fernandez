package com.fernandez.catalogo.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ItemDto(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val thumbnail: String? = null
)

@Serializable
data class ProductosResponseDto(
    val products: List<ItemDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
