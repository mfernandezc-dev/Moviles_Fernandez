package com.fernandez.catalogo.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandez.catalogo.data.repository.ItemRepository
import com.fernandez.catalogo.domain.model.Item
import com.fernandez.catalogo.domain.model.Rol
import com.fernandez.catalogo.domain.model.Sesion
import com.fernandez.catalogo.ui.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class IndicadoresAdmin(val productosGuardados: Int, val favoritosLocales: Int)

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModel(
    repository: ItemRepository,
    sesionAutorizada: StateFlow<UiState<Sesion?>>
) : ViewModel() {
    private val reintentos = MutableStateFlow(0)

    val estado: StateFlow<UiState<IndicadoresAdmin>> = combine(sesionAutorizada, reintentos) { sesion, _ -> sesion }
        .flatMapLatest { sesion ->
            when {
                sesion == UiState.Cargando -> flowOf(UiState.Cargando)
                sesion is UiState.Exito && sesion.datos?.rol == Rol.ADMIN ->
                    repository.items.map<List<Item>, UiState<IndicadoresAdmin>> { productos ->
                        UiState.Exito(IndicadoresAdmin(productos.size, productos.count { it.esFavorito }))
                    }.catch { emit(UiState.Error("No se pudieron consultar los indicadores locales.")) }
                else -> flowOf(UiState.Error("No tienes autorización para acceder al panel."))
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Cargando)

    fun reintentar() {
        reintentos.value += 1
    }
}
