package com.tecsup.centrotareas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.centrotareas.data.TareasRepository

class TareasViewModelFactory(private val repo: TareasRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(TareasViewModel::class.java)) {
            "ViewModel no soportado: ${modelClass.name}"
        }
        @Suppress("UNCHECKED_CAST")
        return TareasViewModel(repo) as T
    }
}
