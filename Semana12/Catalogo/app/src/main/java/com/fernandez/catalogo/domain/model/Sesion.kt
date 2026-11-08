package com.fernandez.catalogo.domain.model

enum class Rol { USER, ADMIN }

data class Sesion(
    val uid: String,
    val correo: String,
    val rol: Rol? = null
)
