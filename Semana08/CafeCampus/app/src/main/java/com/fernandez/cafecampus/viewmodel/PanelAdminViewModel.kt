
package com.fernandez.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandez.cafecampus.data.repository.CafeRepository
import com.fernandez.cafecampus.model.EstadoPedido
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import com.fernandez.cafecampus.data.local.relation.PedidoConDetalle

class PanelAdminViewModel(
    private val repository: CafeRepository
) : ViewModel() {


    val pedidosNuevos: StateFlow<List<PedidoConDetalle>> =
        repository.observarPedidosPorEstado(EstadoPedido.RECIBIDO)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )


    val nuevos: StateFlow<Int> =
        repository.observarCantidadPorEstado(
            EstadoPedido.RECIBIDO
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    val preparando: StateFlow<Int> =
        repository.observarCantidadPorEstado(
            EstadoPedido.PREPARANDO
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    val listos: StateFlow<Int> =
        repository.observarCantidadPorEstado(
            EstadoPedido.LISTO
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    private val inicioDia: Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val ventasHoy: StateFlow<Double> =
        repository.observarVentasHoy(inicioDia)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0.0
            )
}
