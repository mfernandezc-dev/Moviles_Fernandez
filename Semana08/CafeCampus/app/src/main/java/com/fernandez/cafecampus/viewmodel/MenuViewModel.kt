
package com.fernandez.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandez.cafecampus.data.local.entity.Producto
import com.fernandez.cafecampus.data.repository.CafeRepository
import com.fernandez.cafecampus.model.ItemCarrito
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MenuViewModel(
    private val repository: CafeRepository
) : ViewModel() {

    // Productos obtenidos desde Room mediante el Repository
    val productos: StateFlow<List<Producto>> =
        repository.observarProductos().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Carrito de compras
    private val _carrito =
        MutableStateFlow<List<ItemCarrito>>(emptyList())

    val carrito: StateFlow<List<ItemCarrito>> =
        _carrito.asStateFlow()

    // Agregar producto
    fun agregar(
        producto: Producto,
        cantidad: Int,
        nota: String = ""
    ) {
        if (!producto.disponible || cantidad < 1) return

        val listaActual = _carrito.value

        val existente = listaActual.find {
            it.producto.id == producto.id
        }

        if (existente != null) {
            _carrito.value = listaActual.map { item ->
                if (item.producto.id == producto.id) {
                    item.copy(
                        cantidad = item.cantidad + cantidad
                    )
                } else {
                    item
                }
            }
        } else {
            _carrito.value = listaActual + ItemCarrito(
                producto = producto,
                cantidad = cantidad,
                nota = nota
            )
        }
    }

    // Quitar producto
    fun quitar(productoId: Int) {
        _carrito.value = _carrito.value.filter {
            it.producto.id != productoId
        }
    }

    // Vaciar carrito
    fun vaciar() {
        _carrito.value = emptyList()
    }

    // Calcular el total
    fun total(): Double {
        return _carrito.value.sumOf {
            it.producto.precio * it.cantidad
        }
    }
}
