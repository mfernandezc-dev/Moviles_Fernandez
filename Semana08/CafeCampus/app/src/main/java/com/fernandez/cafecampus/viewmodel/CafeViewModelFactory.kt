package com.fernandez.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fernandez.cafecampus.data.prefs.PreferenciasStore
import com.fernandez.cafecampus.data.repository.CafeRepository

class CafeViewModelFactory(
    private val repository: CafeRepository,
    private val preferencias: PreferenciasStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(SesionViewModel::class.java)) {
            return SesionViewModel(repository, preferencias) as T
        }

        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            return MenuViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(PedidosClienteViewModel::class.java)) {
            return PedidosClienteViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(PanelAdminViewModel::class.java)) {
            return PanelAdminViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(PedidosAdminViewModel::class.java)) {
            return PedidosAdminViewModel(repository) as T
        }


        throw IllegalArgumentException("ViewModel desconocido")
    }
}