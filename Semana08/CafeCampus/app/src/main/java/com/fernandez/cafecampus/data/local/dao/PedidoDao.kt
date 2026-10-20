package com.fernandez.cafecampus.data.local.dao

import androidx.room.*
import com.fernandez.cafecampus.data.local.entity.*
import com.fernandez.cafecampus.data.local.relation.PedidoConDetalle
import com.fernandez.cafecampus.model.PedidosPorHora
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {

    @Insert
    suspend fun insertarPedido(pedido: Pedido): Long

    @Insert
    suspend fun insertarDetalles(
        detalles: List<DetallePedido>
    )

    @Query("""
        UPDATE pedidos
        SET estado = :estado
        WHERE id = :id
    """)
    suspend fun cambiarEstado(
        id: Int,
        estado: String
    )

    @Transaction
    suspend fun crearPedido(
        pedido: Pedido,
        detalles: List<DetallePedido>
    ): Long {
        val pedidoId = insertarPedido(pedido)

        val detallesConId = detalles.map {
            it.copy(pedidoId = pedidoId.toInt())
        }

        insertarDetalles(detallesConId)

        return pedidoId
    }

    @Transaction
    @Query("""
        SELECT * FROM pedidos
        WHERE clienteId = :clienteId
        ORDER BY fecha DESC
    """)
    fun observarPorCliente(
        clienteId: Int
    ): Flow<List<PedidoConDetalle>>

    @Transaction
    @Query("""
        SELECT * FROM pedidos
        WHERE estado = :estado
        ORDER BY fecha DESC
    """)
    fun observarPorEstado(
        estado: String
    ): Flow<List<PedidoConDetalle>>

    @Transaction
    @Query("""
        SELECT * FROM pedidos
        WHERE id = :id
    """)
    fun observarUno(
        id: Int
    ): Flow<PedidoConDetalle?>

    @Query("""
        SELECT COALESCE(SUM(total), 0.0)
        FROM pedidos
        WHERE fecha >= :inicio
        AND estado != 'CANCELADO'
    """)
    fun ventasDesde(
        inicio: Long
    ): Flow<Double>

    @Query("""
        SELECT COUNT(*) FROM pedidos
        WHERE estado = :estado
    """)
    fun cantidadPorEstado(
        estado: String
    ): Flow<Int>

    @Query("""
        SELECT CAST(strftime('%H', (fecha + :offsetMs) / 1000, 'unixepoch') AS INTEGER) AS hora,
               COUNT(*) AS cantidad
        FROM pedidos
        WHERE fecha >= :inicioDia AND fecha < :finDia
        GROUP BY hora
        ORDER BY hora ASC
    """)
    fun observarPedidosPorHora(
        inicioDia: Long,
        finDia: Long,
        offsetMs: Long
    ): Flow<List<PedidosPorHora>>
}
