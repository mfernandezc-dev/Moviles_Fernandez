
package com.fernandez.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandez.cafecampus.data.local.entity.Producto
import com.fernandez.cafecampus.data.repository.CafeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MenuAdminViewModel(
    private val repository: CafeRepository
) : ViewModel() {

    val productos: StateFlow<List<Producto>> =
        repository.observarProductos()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun guardar(producto: Producto) {
        viewModelScope.launch {
            repository.guardarProducto(producto)
        }
    }

    fun eliminar(producto: Producto) {
        viewModelScope.launch {
            repository.eliminarProducto(producto)
        }
    }

    fun alternarDisponibilidad(producto: Producto) {
        viewModelScope.launch {
            repository.cambiarDisponibilidad(
                producto.id,
                !producto.disponible
            )
        }
    }
}
