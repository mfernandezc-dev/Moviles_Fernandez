package com.fernandez.cafecampus.ui.navigation

object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val MENU_CLIENTE = "menu_cliente"
    const val PANEL_ADMIN = "panel_admin"
    const val DETALLE_PRODUCTO = "detalle_producto/{productoId}"
    const val CONFIRMAR_PEDIDO = "confirmar_pedido"

    const val MIS_PEDIDOS = "mis_pedidos"

    const val SEGUIMIENTO = "seguimiento/{pedidoId}"

    const val PEDIDOS_ADMIN = "pedidos_admin"

    const val MENU_ADMIN = "menu_admin"

    const val AJUSTES_ADMIN = "ajustes_admin"
    const val DETALLE_PEDIDO_ADMIN = "detalle_pedido_admin/{pedidoId}"

    fun detallePedidoAdmin(pedidoId: Int): String {
        return "detalle_pedido_admin/$pedidoId"
    }

    fun seguimiento(pedidoId: Int): String {
        return "seguimiento/$pedidoId"
    }
    fun detalleProducto(productoId: Int): String {
        return "detalle_producto/$productoId"
    }
}