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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.fernandez.campusgps.model.Posicion
import com.fernandez.campusgps.util.distanciaMetros
import com.fernandez.campusgps.model.Lugar
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import com.fernandez.campusgps.model.TipoError
import com.fernandez.campusgps.data.GpsApagadoException
import com.fernandez.campusgps.data.PlayServicesNoDisponibleException

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

    val categorias: List<String> =
        listOf("Todos", "Estudio", "Comida", "Eventos", "Servicios")

    private val _categoriaSeleccionada = MutableStateFlow(categorias.first())

    val categoriaSeleccionada: StateFlow<String> =
        _categoriaSeleccionada.asStateFlow()

    val radiosDisponibles: List<Int> = listOf(100, 200, 500)

    private val _radioSeleccionado = MutableStateFlow(500)

    val radioSeleccionado: StateFlow<Int> = _radioSeleccionado.asStateFlow()

    val lugaresFiltrados: StateFlow<List<LugarConDistancia>> =
        combine(lugares, categoriaSeleccionada, radioSeleccionado) { lista, categoria, radio ->
            filtrarLugares(lista, categoria, radio)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

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
                .onFailure { error ->
                    _estado.value = errorDe(error)
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

        // Una nueva posición puede dejar la selección anterior fuera del radio.
        seleccionar(_seleccionado.value)
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            mensaje = "Sin permiso de ubicación no puedo mostrar dónde estás",
            tipo = TipoError.PERMISO_DENEGADO
        )
    }

    fun seleccionar(id: Int?) {
        // Valida ambos filtros incluso si un clic llega durante un cambio de categoría o radio.
        _seleccionado.value = id?.takeIf { candidato ->
            filtrarLugares(
                _lugares.value,
                _categoriaSeleccionada.value,
                _radioSeleccionado.value
            ).any {
                it.lugar.id == candidato
            }
        }
    }

    fun seleccionarCategoria(categoria: String) {
        if (categoria !in categorias || categoria == _categoriaSeleccionada.value) return

        _categoriaSeleccionada.value = categoria
        seleccionar(_seleccionado.value)
    }

    fun seleccionarRadio(radio: Int) {
        if (radio !in radiosDisponibles || radio == _radioSeleccionado.value) return

        _radioSeleccionado.value = radio
        seleccionar(_seleccionado.value)
    }

    private fun filtrarLugares(
        lista: List<LugarConDistancia>,
        categoria: String,
        radio: Int
    ): List<LugarConDistancia> = lista.filter { item ->
        (categoria == categorias.first() || item.lugar.categoria == categoria) &&
                (item.metros?.let { it <= radio } == true)
    }

    val masCercano: StateFlow<Lugar?> = lugaresFiltrados
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

    private fun errorDe(e: Throwable): UbicacionUi.Error {

        return when (e) {

            is SecurityException ->
                UbicacionUi.Error(
                    mensaje = "Falta el permiso de ubicación",
                    tipo = TipoError.PERMISO_DENEGADO
                )

            is GpsApagadoException ->
                UbicacionUi.Error(
                    mensaje = e.message ?: "El GPS está apagado",
                    tipo = TipoError.GPS_APAGADO
                )

            is PlayServicesNoDisponibleException ->
                UbicacionUi.Error(
                    mensaje = e.message ?: "Google Play Services no está disponible",
                    tipo = TipoError.SIN_PLAY_SERVICES
                )

            else ->
                UbicacionUi.Error(
                    mensaje = e.message ?: "No se pudo obtener la ubicación",
                    tipo = TipoError.DESCONOCIDO
                )
        }
    }
}
