package com.fernandez.catalogo.testing

import com.fernandez.catalogo.notification.Notificador

class FakeNotificador : Notificador {
    data class Notificacion(val titulo: String, val texto: String)

    private val solicitudes = mutableListOf<Notificacion>()
    val notificaciones: List<Notificacion>
        get() = solicitudes.toList()

    override fun mostrar(titulo: String, texto: String) {
        solicitudes += Notificacion(titulo, texto)
    }
}
