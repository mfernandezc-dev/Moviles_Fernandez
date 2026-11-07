package com.fernandez.catalogo.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandez.catalogo.data.repository.ItemRepository
import com.fernandez.catalogo.domain.model.Item
import com.fernandez.catalogo.ui.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalleViewModel(
    private val repository: ItemRepository,
    private val id: Int
) : ViewModel() {
    private val _estado = MutableStateFlow<UiState<Item>>(UiState.Cargando)
    val estado: StateFlow<UiState<Item>> = _estado.asStateFlow()
    private var carga: Job? = null

    init {
        cargarDetalle()
    }

    fun reintentar() {
        cargarDetalle()
    }

    private fun cargarDetalle() {
        if (id <= 0) {
            _estado.value = UiState.Error("El ID de la laptop no es válido.")
            return
        }
        if (carga?.isActive == true) return

        _estado.value = UiState.Cargando
        carga = viewModelScope.launch {
            repository.obtenerDetalle(id).fold(
                onSuccess = { item -> _estado.value = UiState.Exito(item) },
                onFailure = {
                    _estado.value = UiState.Error(
                        "No se pudo cargar el detalle. Comprueba tu conexión y vuelve a intentarlo."
                    )
                }
            )
        }
    }
}
