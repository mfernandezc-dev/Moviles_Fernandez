package com.fernandez.catalogo.data.mapper

import com.fernandez.catalogo.data.local.ItemEntity
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

fun ItemDto.toEntity(esFavorito: Boolean = false): ItemEntity = ItemEntity(
    id = id,
    titulo = title,
    subtitulo = description,
    imagenUrl = thumbnail,
    precio = price,
    esFavorito = esFavorito
)

fun ItemEntity.toItem(): Item = Item(
    id = id,
    titulo = titulo,
    subtitulo = subtitulo,
    imagenUrl = imagenUrl,
    precio = precio,
    esFavorito = esFavorito
)
