package com.fernandez.catalogo.data.auth

import com.fernandez.catalogo.domain.model.Sesion
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val sesiones: Flow<Sesion?>

    suspend fun iniciarSesion(
        correo: String,
        clave: String
    ): Result<Sesion>

    suspend fun sesionActual(): Sesion?
    fun cerrarSesion()
}
