package com.fernandez.cafecampus.data.repository

import com.fernandez.cafecampus.data.local.entity.Producto
import com.fernandez.cafecampus.data.local.entity.Usuario
import com.fernandez.cafecampus.data.local.relation.PedidoConDetalle
import com.fernandez.cafecampus.model.EstadoPedido
import com.fernandez.cafecampus.model.ItemCarrito
import com.fernandez.cafecampus.model.PedidosPorHora
import kotlinx.coroutines.flow.Flow

interface CafeRepository {

    // Usuarios
    suspend fun login(
        correo: String,
        clave: String
    ): Usuario?

    // Productos
    fun observarProductos(): Flow<List<Producto>>

    suspend fun guardarProducto(p: Producto)

    suspend fun eliminarProducto(p: Producto)

    suspend fun cambiarDisponibilidad(
        id: Int,
        disponible: Boolean
    )

    // Pedidos
    suspend fun crearPedido(
        clienteId: Int,
        items: List<ItemCarrito>,
        horaRecojo: String,
        pago: String,
        nota: String
    ): Long

    fun observarPedidosCliente(
        clienteId: Int
    ): Flow<List<PedidoConDetalle>>

    fun observarPedidosPorEstado(
        estado: EstadoPedido
    ): Flow<List<PedidoConDetalle>>

    fun observarPedido(
        id: Int
    ): Flow<PedidoConDetalle?>

    suspend fun cambiarEstado(
        pedidoId: Int,
        estado: EstadoPedido
    )

    // Panel de administrador
    fun observarVentasHoy(
        inicioDia: Long
    ): Flow<Double>

    fun observarCantidadPorEstado(
        estado: EstadoPedido
    ): Flow<Int>

    fun observarPedidosPorHora(
        inicioDia: Long,
        finDia: Long,
        offsetMs: Long
    ): Flow<List<PedidosPorHora>>
}
