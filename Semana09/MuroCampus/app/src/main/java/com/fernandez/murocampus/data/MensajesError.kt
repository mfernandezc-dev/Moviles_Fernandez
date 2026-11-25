package com.fernandez.murocampus.data

import java.io.IOException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

fun mensajeDe(e: Throwable): String = when (e) {
    is IOException -> "Sin conexión a internet.\nRevisa tu red e inténtalo otra vez."
    is HttpException -> "Error del servidor (${e.code()})"
    is SerializationException -> "Respuesta inesperada"
    else -> "Error desconocido"
}
