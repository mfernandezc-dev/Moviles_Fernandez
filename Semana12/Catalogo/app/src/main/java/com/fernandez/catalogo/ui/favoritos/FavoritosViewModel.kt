package com.fernandez.catalogo.ui.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandez.catalogo.data.repository.ItemRepository
import com.fernandez.catalogo.domain.model.Item
import com.fernandez.catalogo.ui.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritosViewModel(private val repository: ItemRepository) : ViewModel() {
    val estado: StateFlow<UiState<List<Item>>> = repository.favoritos
        .map<List<Item>, UiState<List<Item>>> { favoritos -> UiState.Exito(favoritos) }
        .catch { emit(UiState.Error("No se pudieron cargar los favoritos.")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Cargando)

    fun desmarcarFavorito(id: Int) {
        viewModelScope.launch {
            repository.alternarFavorito(id)
        }
    }
}
