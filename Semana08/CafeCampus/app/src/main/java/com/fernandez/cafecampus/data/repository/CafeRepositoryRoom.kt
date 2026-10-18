package com.fernandez.cafecampus.data.repository

import com.fernandez.cafecampus.data.local.CafeDatabase
import com.fernandez.cafecampus.data.local.entity.*
import com.fernandez.cafecampus.data.local.relation.PedidoConDetalle
import com.fernandez.cafecampus.model.*
import kotlinx.coroutines.flow.Flow

class CafeRepositoryRoom(
    private val db: CafeDatabase
) : CafeRepository {

    private val productoDao = db.productoDao()
    private val usuarioDao = db.usuarioDao()
    private val pedidoDao = db.pedidoDao()

    // USUARIOS

    override suspend fun login(
        correo: String,
        clave: String
    ): Usuario? {
        return usuarioDao.login(correo, clave)
    }

    // PRODUCTOS

    override fun observarProductos(): Flow<List<Producto>> {
        return productoDao.observarTodos()
    }

    override suspend fun guardarProducto(p: Producto) {
        if (p.id == 0) {
            productoDao.insertar(p)
        } else {
            productoDao.actualizar(p)
        }
    }

    override suspend fun eliminarProducto(p: Producto) {
        productoDao.eliminar(p)
    }

    override suspend fun cambiarDisponibilidad(
        id: Int,
        disponible: Boolean
    ) {
        productoDao.cambiarDisponibilidad(id, disponible)
    }

    // PEDIDOS

    override suspend fun crearPedido(
        clienteId: Int,
        items: List<ItemCarrito>,
        horaRecojo: String,
        pago: String,
        nota: String
    ): Long {

        require(items.isNotEmpty()) {
            "El carrito no puede estar vacío"
        }

        val total = items.sumOf {
            it.producto.precio * it.cantidad
        }

        val pedido = Pedido(
            clienteId = clienteId,
            estado = EstadoPedido.RECIBIDO.name,
            horaRecojo = horaRecojo,
            pago = pago,
            nota = nota,
            total = total
        )

        val detalles = items.map { item ->
            DetallePedido(
                pedidoId = 0,
                productoId = item.producto.id,
                nombreProducto = item.producto.nombre,
                cantidad = item.cantidad,
                precioUnitario = item.producto.precio
            )
        }

        return pedidoDao.crearPedido(pedido, detalles)
    }

    override fun observarPedidosCliente(
        clienteId: Int
    ): Flow<List<PedidoConDetalle>> {
        return pedidoDao.observarPorCliente(clienteId)
    }

    override fun observarPedidosPorEstado(
        estado: EstadoPedido
    ): Flow<List<PedidoConDetalle>> {
        return pedidoDao.observarPorEstado(estado.name)
    }

    override fun observarPedido(
        id: Int
    ): Flow<PedidoConDetalle?> {
        return pedidoDao.observarUno(id)
    }

    override suspend fun cambiarEstado(
        pedidoId: Int,
        estado: EstadoPedido
    ) {
        pedidoDao.cambiarEstado(pedidoId, estado.name)
    }

    // PANEL ADMINISTRADOR

    override fun observarVentasHoy(
        inicioDia: Long
    ): Flow<Double> {
        return pedidoDao.ventasDesde(inicioDia)
    }

    override fun observarCantidadPorEstado(
        estado: EstadoPedido
    ): Flow<Int> {
        return pedidoDao.cantidadPorEstado(estado.name)
    }
}