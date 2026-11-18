package com.fernandez.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandez.campusgps.data.LugaresRepository
import com.fernandez.campusgps.data.UbicacionRepository
import com.fernandez.campusgps.model.LugarConDistancia
import com.fernandez.campusgps.model.UbicacionUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.fernandez.campusgps.model.Posicion
import com.fernandez.campusgps.util.distanciaMetros
import com.fernandez.campusgps.model.Lugar
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository
) : ViewModel() {

    private val _estado =
        MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)

    val estado: StateFlow<UbicacionUi> =
        _estado.asStateFlow()

    private val _lugares = MutableStateFlow(
        lugaresRepo.obtenerLugares().map {
            LugarConDistancia(it, null)
        }
    )

    val lugares: StateFlow<List<LugarConDistancia>> =
        _lugares.asStateFlow()

    private val _seleccionado =
        MutableStateFlow<Int?>(null)

    val seleccionado: StateFlow<Int?> =
        _seleccionado.asStateFlow()

    fun buscar() {

        _estado.value = UbicacionUi.Buscando

        viewModelScope.launch {

            ubicacionRepo.obtenerPosicion()
                .onSuccess { posicion ->

                    actualizarDistancias(posicion)

                    _estado.value = UbicacionUi.Lista(posicion)
                }
                .onFailure {
                    _estado.value = UbicacionUi.Error(
                        mensajeDe(it)
                    )
                }
        }
    }

    private fun actualizarDistancias(posicion: Posicion) {

        _lugares.value = lugaresRepo.obtenerLugares()
            .map { lugar ->

                val distancia = distanciaMetros(
                    desde = posicion,
                    hasta = lugar
                )

                LugarConDistancia(
                    lugar = lugar,
                    metros = distancia
                )
            }
            .sortedBy { it.metros }
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás"
        )
    }

    fun seleccionar(id: Int?) {
        _seleccionado.value = id
    }

    val masCercano: StateFlow<Lugar?> = lugares
        .map { lista ->

            lista.firstOrNull {
                it.metros != null
            }?.lugar

        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private fun mensajeDe(e: Throwable): String =
        when (e) {

            is SecurityException ->
                "Falta el permiso de ubicación"

            else ->
                e.message ?: "No se pudo obtener la ubicación"
        }
}