package com.fernandez.catalogo.ui.lista

import androidx.lifecycle.ViewModel
import com.fernandez.catalogo.domain.model.Item
import com.fernandez.catalogo.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ListaViewModel : ViewModel() {
    private val itemsDePrueba = listOf(
        Item(
            id = 1,
            titulo = "Andes Pro 14",
            subtitulo = "Pantalla de 14 pulgadas, 16 GB de RAM y SSD de 512 GB.",
            imagenUrl = null,
            precio = 2899.90,
            esFavorito = false
        ),
        Item(
            id = 2,
            titulo = "Costa Air 13",
            subtitulo = "Ligera y portátil, con 8 GB de RAM y SSD de 256 GB.",
            imagenUrl = null,
            precio = 2199.00,
            esFavorito = false
        ),
        Item(
            id = 3,
            titulo = "Sierra Studio 16",
            subtitulo = "Pantalla de 16 pulgadas, 32 GB de RAM y SSD de 1 TB.",
            imagenUrl = null,
            precio = 4599.90,
            esFavorito = false
        ),
        Item(
            id = 4,
            titulo = "Valle Work 15",
            subtitulo = "Ideal para estudiar y trabajar, con 16 GB de RAM y SSD de 512 GB.",
            imagenUrl = null,
            precio = 2599.00,
            esFavorito = false
        ),
        Item(
            id = 5,
            titulo = "Pacífico Gamer 16",
            subtitulo = "Gráficos dedicados, 16 GB de RAM y SSD de 1 TB.",
            imagenUrl = null,
            precio = 5299.90,
            esFavorito = false
        )
    )

    private val _estado = MutableStateFlow<UiState<List<Item>>>(
        UiState.Exito(itemsDePrueba)
    )
    val estado: StateFlow<UiState<List<Item>>> = _estado.asStateFlow()

    fun cargarItems() {
        _estado.value = UiState.Exito(itemsDePrueba)
    }
}
