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
    private val resultadoCarga = MutableStateFlow<Result<Int>?>(null)
    private var carga: Job? = null

    val estado: StateFlow<UiState<List<Item>>> = combine(
        repository.items,
        resultadoCarga
    ) { productos, resultado ->
        when {
            productos.isNotEmpty() -> UiState.Exito(productos)
            resultado == null -> UiState.Cargando
            resultado.isFailure -> UiState.Error(
                "No se pudieron cargar las laptops. Comprueba tu conexión y vuelve a intentarlo."
            )
            // El guardado puede terminar antes de que Room emita la lista actualizada.
            resultado.getOrDefault(0) > 0 -> UiState.Cargando
            else -> UiState.Exito(productos)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Cargando)

    init {
        cargarItems()
    }

    fun alternarFavorito(id: Int) {
        viewModelScope.launch {
            repository.alternarFavorito(id)
        }
    }

    fun cargarItems() {
        if (carga?.isActive == true) return

        resultadoCarga.value = null
        carga = viewModelScope.launch {
            resultadoCarga.value = repository.refrescar()
        }
    }
}
