package com.fernandez.catalogo.data.auth

import com.fernandez.catalogo.domain.model.Sesion
import com.fernandez.catalogo.domain.model.Rol
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val sesiones: Flow<Sesion?>

    suspend fun iniciarSesion(
        correo: String,
        clave: String
    ): Result<Sesion>

    suspend fun sesionActual(): Sesion?
    suspend fun obtenerRol(uid: String): Result<Rol>
    fun cerrarSesion()
}
