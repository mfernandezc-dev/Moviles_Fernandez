package com.fernandez.catalogo.data.mapper

import com.fernandez.catalogo.data.remote.dto.ItemDto
import com.fernandez.catalogo.domain.model.Item

fun ItemDto.toItem(): Item = Item(
    id = id,
    titulo = title,
    subtitulo = description,
    imagenUrl = thumbnail,
    precio = price,
    esFavorito = false
)
