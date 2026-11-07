package com.fernandez.catalogo.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandez.catalogo.data.repository.ItemRepository
import com.fernandez.catalogo.domain.model.Item
import com.fernandez.catalogo.ui.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListaViewModel(private val repository: ItemRepository) : ViewModel() {
    private val cargando = MutableStateFlow(true)
    private val mensajeError = MutableStateFlow<String?>(null)
    private var carga: Job? = null

    val estado: StateFlow<UiState<List<Item>>> = combine(
        repository.items,
        cargando,
        mensajeError
    ) { productos, estaCargando, error ->
        when {
            estaCargando -> UiState.Cargando
            productos.isNotEmpty() -> UiState.Exito(productos)
            error != null -> UiState.Error(error)
            else -> UiState.Exito(productos)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Cargando)

    init {
        cargarItems()
    }

    fun cargarItems() {
        if (carga?.isActive == true) return

        cargando.value = true
        mensajeError.value = null
        carga = viewModelScope.launch {
            try {
                val resultado = repository.refrescar()
                mensajeError.value = if (resultado.isFailure) {
                    "No se pudieron cargar las laptops. Comprueba tu conexión y vuelve a intentarlo."
                } else {
                    null
                }
            } finally {
                cargando.value = false
            }
        }
    }
}
