package com.fernandez.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandez.cafecampus.data.local.relation.PedidoConDetalle
import com.fernandez.cafecampus.data.repository.CafeRepository
import com.fernandez.cafecampus.model.ItemCarrito
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PedidosClienteViewModel(
    private val repository: CafeRepository
) : ViewModel() {

    private val _clienteId = MutableStateFlow<Int?>(null)

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _confirmando = MutableStateFlow(false)
    val confirmando: StateFlow<Boolean> = _confirmando.asStateFlow()

    private val _pedidoCreado = MutableStateFlow<Long?>(null)
    val pedidoCreado: StateFlow<Long?> = _pedidoCreado.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val misPedidos: StateFlow<List<PedidoConDetalle>> =
        _clienteId.flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.observarPedidosCliente(id)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun establecerCliente(id: Int?) {
        _clienteId.value = id
    }

    fun pedido(id: Int): StateFlow<PedidoConDetalle?> =
        repository.observarPedido(id).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    fun confirmar(
        items: List<ItemCarrito>,
        horaRecojo: String,
        pago: String,
        nota: String,
        recibirPedidos: Boolean
    ) {
        if (_confirmando.value) return

        if (!recibirPedidos) {
            _error.value = "La cafetería está cerrada. No se aceptan pedidos por el momento."
            return
        }

        val id = _clienteId.value
        if (id == null) {
            _error.value = "No se encontró el estudiante"
            return
        }
        if (items.isEmpty()) {
            _error.value = "El carrito está vacío"
            return
        }

        viewModelScope.launch {
            _confirmando.value = true
            _error.value = null
            try {
                val nuevoId = repository.crearPedido(
                    clienteId = id,
                    items = items,
                    horaRecojo = horaRecojo,
                    pago = pago,
                    nota = nota
                )
                _pedidoCreado.value = nuevoId
            } catch (e: Exception) {
                _error.value = "No se pudo confirmar el pedido"
            } finally {
                _confirmando.value = false
            }
        }
    }

    fun pedidoCreadoConsumido() {
        _pedidoCreado.value = null
    }

    fun limpiarError() {
        _error.value = null
    }
}
