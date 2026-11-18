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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class TareasViewModel(private val repo: TareasRepository) : ViewModel() {
    private val _toques = MutableStateFlow(0)
    val toques: StateFlow<Int> = _toques.asStateFlow()

    private val _mensaje = MutableStateFlow("Listo para iniciar")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _progreso = MutableStateFlow(0f)
    val progreso: StateFlow<Float> = _progreso.asStateFlow()

    private var trabajo: Job? = null
    private val _corriendo = MutableStateFlow(false)
    val corriendo: StateFlow<Boolean> = _corriendo.asStateFlow()

    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()
    private var trabajoPrimos: Job? = null

    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()
    private var trabajoCarga: Job? = null

    private val _simularError = MutableStateFlow(false)
    val simularError: StateFlow<Boolean> = _simularError.asStateFlow()

    fun cambiarSimularError(valor: Boolean) {
        _simularError.value = valor
    }

    fun tocar() {
        _toques.value += 1
    }

    fun tareaLarga() {
        if (trabajo?.isActive == true || _corriendo.value) return
        trabajo = viewModelScope.launch {
            _corriendo.value = true
            try {
                _progreso.value = 0f
                _mensaje.value = "Trabajando..."
                Log.d("HILO", "Antes: ${Thread.currentThread().name}")
                repo.progresoTarea().collect { avance ->
                    _progreso.value = avance
                }
                Log.d("HILO", "Después: ${Thread.currentThread().name}")
                _mensaje.value = "Terminó"
            } catch (cancelacion: CancellationException) {
                _mensaje.value = "Cancelado"
                throw cancelacion
            } finally {
                _corriendo.value = false
            }
        }
    }

    fun cancelar() {
        trabajo?.cancel()
    }

    fun contar(hasta: Int) {
        if (hasta < 2 || trabajoPrimos?.isActive == true) return
        trabajoPrimos = viewModelScope.launch {
            _primos.value = PrimosUi.Calculando
            try {
                val inicio = System.currentTimeMillis()
                val cantidad = repo.contarPrimos(hasta)
                _primos.value = PrimosUi.Listo(cantidad, System.currentTimeMillis() - inicio)
            } catch (cancelacion: CancellationException) {
                _primos.value = PrimosUi.Cancelado
                throw cancelacion
            }
        }
    }

    fun cancelarPrimos() {
        trabajoPrimos?.cancel()
    }

    fun cargarUnaPorUna() {
        cargar(paralela = false)
    }

    fun cargarALaVez() {
        cargar(paralela = true)
    }

    private fun cargar(paralela: Boolean) {
        if (trabajoCarga?.isActive == true) return
        trabajoCarga = viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            val provocarError = _simularError.value
            try {
                val inicio = System.currentTimeMillis()
                val (perfil, pedidos) = if (paralela) {
                    coroutineScope {
                        val perfilPendiente = async { repo.cargarPerfil() }
                        val pedidosPendientes = async { repo.cargarPedidos(provocarError) }
                        perfilPendiente.await() to pedidosPendientes.await()
                    }
                } else {
                    val perfil = repo.cargarPerfil()
                    perfil to repo.cargarPedidos(provocarError)
                }
                _carga.value = CargaUi.Exito(
                    modo = if (paralela) "A la vez" else "Una por una",
                    perfil = perfil,
                    pedidos = pedidos.size,
                    milisegundos = System.currentTimeMillis() - inicio
                )
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (error: Exception) {
                _carga.value = CargaUi.Error("No se pudo cargar: " + error.message)
            }
        }
    }
}
