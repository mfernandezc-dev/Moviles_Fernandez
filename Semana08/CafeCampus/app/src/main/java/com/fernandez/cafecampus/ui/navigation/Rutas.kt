package com.fernandez.cafecampus.ui.navigation

object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val MENU_CLIENTE = "menu_cliente"
    const val PANEL_ADMIN = "panel_admin"

    const val DETALLE_PRODUCTO = "detalle_producto/{productoId}"

    fun detalleProducto(productoId: Int): String {
        return "detalle_producto/$productoId"
    }
}