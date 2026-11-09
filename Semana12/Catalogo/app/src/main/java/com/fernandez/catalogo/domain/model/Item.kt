package com.fernandez.catalogo.domain.model

data class Item(
    val id: Int,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String?,
    val precio: Double,
    val esFavorito: Boolean = false
)
