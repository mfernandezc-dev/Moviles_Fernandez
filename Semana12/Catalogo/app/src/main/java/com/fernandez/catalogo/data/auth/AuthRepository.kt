package com.fernandez.catalogo.data.auth

import com.fernandez.catalogo.domain.model.Sesion

interface AuthRepository {
    suspend fun iniciarSesion(
        correo: String,
        clave: String
    ): Result<Sesion>

    suspend fun sesionActual(): Sesion?
    fun cerrarSesion()
}
