package com.tecsup.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.centrotareas.data.TareasRepository
import com.tecsup.centrotareas.model.CargaUi
import com.tecsup.centrotareas.model.PrimosUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TareasViewModel(private val repo: TareasRepository) : ViewModel() {
    private val _toques = MutableStateFlow(0)
    val toques: StateFlow<Int> = _toques.asStateFlow()

    private val _mensaje = MutableStateFlow("Listo para iniciar")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()

    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()

    fun tocar() {
        _toques.value += 1
    }

    fun tareaLarga() {
        viewModelScope.launch {
            _mensaje.value = "Trabajando..."
            Log.d("HILO", "Antes: ${Thread.currentThread().name}")
            withContext(Dispatchers.Default) {
                delay(4000)
                Log.d("HILO", "Dentro: ${Thread.currentThread().name}")
            }
            Log.d("HILO", "Después: ${Thread.currentThread().name}")
            _mensaje.value = "Terminó"
        }
    }
}
