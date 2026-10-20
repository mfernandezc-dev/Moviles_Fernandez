
package com.fernandez.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandez.cafecampus.data.local.relation.PedidoConDetalle
import com.fernandez.cafecampus.data.repository.CafeRepository
import com.fernandez.cafecampus.model.EstadoPedido
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PedidosAdminViewModel(
    private val repository: CafeRepository
) : ViewModel() {

    private val _estadoSeleccionado =
        MutableStateFlow(EstadoPedido.RECIBIDO)

    val estadoSeleccionado: StateFlow<EstadoPedido> =
        _estadoSeleccionado.asStateFlow()

    val pedidos: StateFlow<List<PedidoConDetalle>> =
        _estadoSeleccionado.flatMapLatest { estado ->
            repository.observarPedidosPorEstado(estado)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun seleccionarEstado(estado: EstadoPedido) {
        _estadoSeleccionado.value = estado
    }

    fun avanzar(pedido: PedidoConDetalle) {
        val siguiente = when (pedido.pedido.estado) {
            EstadoPedido.RECIBIDO.name ->
                EstadoPedido.PREPARANDO

            EstadoPedido.PREPARANDO.name ->
                EstadoPedido.LISTO

            EstadoPedido.LISTO.name ->
                EstadoPedido.ENTREGADO

            else -> null
        }

        if (siguiente != null) {
            viewModelScope.launch {
                repository.cambiarEstado(
                    pedidoId = pedido.pedido.id,
                    estado = siguiente
                )
            }
        }
    }

    fun cancelar(pedido: PedidoConDetalle) {
        if (pedido.pedido.estado == EstadoPedido.ENTREGADO.name ||
            pedido.pedido.estado == EstadoPedido.CANCELADO.name
        ) {
            return
        }

        viewModelScope.launch {
            repository.cambiarEstado(
                pedidoId = pedido.pedido.id,
                estado = EstadoPedido.CANCELADO
            )
        }
    }
}
