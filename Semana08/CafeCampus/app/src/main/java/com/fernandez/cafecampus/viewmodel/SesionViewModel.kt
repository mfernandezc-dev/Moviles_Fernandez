
package com.fernandez.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandez.cafecampus.data.local.entity.Usuario
import com.fernandez.cafecampus.data.prefs.PreferenciasStore
import com.fernandez.cafecampus.data.repository.CafeRepository
import com.fernandez.cafecampus.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SesionViewModel(
    private val repository: CafeRepository,
    private val preferencias: PreferenciasStore
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    val perfilActivo: StateFlow<String?> =
        preferencias.perfilActivo
            .map<String, String?> { it }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                null
            )

    val modoOscuro = preferencias.modoOscuro.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    val recibirPedidos = preferencias.recibirPedidos.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true
    )

    val notificaciones = preferencias.notificaciones.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true
    )

    val usuarioId = preferencias.usuarioId.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null
    )

    fun cambiarModoOscuro(activado: Boolean) {
        viewModelScope.launch {
            preferencias.guardarModoOscuro(activado)
        }
    }

    fun cambiarNotificaciones(activado: Boolean) {
        viewModelScope.launch {
            preferencias.guardarNotificaciones(activado)
        }
    }

    fun cambiarRecibirPedidos(activado: Boolean) {
        viewModelScope.launch {
            preferencias.guardarRecibirPedidos(activado)
        }
    }

    // Cambiar entre los usuarios de prueba sin volver al Login.
    fun cambiarPerfil() {
        if (_cargando.value) return

        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                val perfilDestino = if (perfilActivo.value == "ADMIN") {
                    "CLIENTE"
                } else {
                    "ADMIN"
                }

                // Credenciales ficticias del laboratorio.
                val correo = if (perfilDestino == "CLIENTE") {
                    "michael@campus.test"
                } else {
                    "admin@cafe.test"
                }

                val clave = if (perfilDestino == "CLIENTE") {
                    "1234"
                } else {
                    "admin"
                }

                val usuario = repository.login(correo, clave)

                if (usuario == null || usuario.rol != perfilDestino) {
                    _error.value =
                        "No se encontró el usuario de práctica"
                    return@launch
                }

                _usuarioActual.value = usuario

                // Guardar primero el usuario y después el perfil.
                preferencias.guardarUsuarioId(usuario.id)
                preferencias.guardarPerfil(usuario.rol)

            } catch (e: Exception) {
                _error.value = "No se pudo cambiar de perfil"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun login(
        correo: String,
        clave: String,
        rolSeleccionado: Rol
    ) {
        if (correo.isBlank() || clave.isBlank()) {
            _error.value = "Completa todos los campos"
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                val usuario = repository.login(
                    correo.trim(),
                    clave
                )

                when {
                    usuario == null -> {
                        _error.value =
                            "Correo o contraseña incorrectos"
                    }

                    usuario.rol != rolSeleccionado.name -> {
                        _error.value =
                            "El usuario no pertenece al perfil seleccionado"
                    }

                    else -> {
                        _usuarioActual.value = usuario
                        preferencias.guardarUsuarioId(usuario.id)
                        preferencias.guardarPerfil(usuario.rol)
                    }
                }
            } catch (e: Exception) {
                _error.value = "No se pudo iniciar sesión"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    // Cerrar sesión sí debe regresar al Login.
    fun cerrarSesion() {
        viewModelScope.launch {
            preferencias.cerrarSesion()
            _usuarioActual.value = null
            _error.value = null
        }
    }
}
