package com.fernandez.catalogo.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandez.catalogo.data.auth.AuthRepository
import com.fernandez.catalogo.domain.model.Sesion
import com.fernandez.catalogo.ui.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val cargando: Boolean = false,
    val error: String? = null
)

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    private val _sesion = MutableStateFlow<UiState<Sesion?>>(UiState.Cargando)
    val sesion: StateFlow<UiState<Sesion?>> = _sesion.asStateFlow()
    private val reintentosPerfil = MutableStateFlow(0)
    private var versionAutorizacion = 0L

    init {
        viewModelScope.launch {
            try {
                combine(repository.sesiones, reintentosPerfil) { usuario, _ -> usuario }
                    .collectLatest { usuario ->
                        val version = ++versionAutorizacion
                        if (usuario == null) {
                            _sesion.value = UiState.Exito(null)
                        } else {
                            _sesion.value = UiState.Cargando
                            val resultado = repository.obtenerRol(usuario.uid)
                            currentCoroutineContext().ensureActive()
                            if (version == versionAutorizacion && repository.sesionActual()?.uid == usuario.uid) {
                                _sesion.value = resultado.fold(
                                    onSuccess = { rol -> UiState.Exito(usuario.copy(rol = rol)) },
                                    onFailure = { error ->
                                        UiState.Error(error.message ?: "No se pudo verificar el perfil de acceso.")
                                    }
                                )
                            }
                        }
                    }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _sesion.value = UiState.Error("No se pudo comprobar la sesión y el perfil de acceso.")
            }
        }
    }

    fun revalidarPerfil() {
        val actual = _sesion.value
        if (actual == UiState.Cargando || (actual is UiState.Exito && actual.datos == null)) return
        ++versionAutorizacion
        _sesion.value = UiState.Cargando
        reintentosPerfil.value += 1
    }

    fun actualizarCorreo(correo: String) {
        if (_estado.value.cargando) return
        _estado.value = _estado.value.copy(correo = correo, error = null)
    }

    fun actualizarClave(clave: String) {
        if (_estado.value.cargando) return
        _estado.value = _estado.value.copy(clave = clave, error = null)
    }

    fun iniciarSesion() {
        val formulario = _estado.value
        if (formulario.cargando) return

        val correo = formulario.correo.trim()
        val error = when {
            correo.isBlank() -> "Ingresa tu correo electrónico."
            formulario.clave.isBlank() -> "Ingresa tu contraseña."
            !Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(correo) ->
                "Ingresa un correo electrónico válido."
            else -> null
        }
        if (error != null) {
            _estado.value = formulario.copy(error = error)
            return
        }

        _estado.value = formulario.copy(correo = correo, cargando = true, error = null)
        viewModelScope.launch {
            try {
                repository.iniciarSesion(correo, formulario.clave).fold(
                    onSuccess = {
                        _estado.value = _estado.value.copy(clave = "", cargando = false, error = null)
                    },
                    onFailure = { fallo ->
                        _estado.value = _estado.value.copy(
                            cargando = false,
                            error = fallo.message ?: "No se pudo iniciar sesión. Inténtalo de nuevo."
                        )
                    }
                )
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _estado.value = _estado.value.copy(
                    cargando = false,
                    error = "No se pudo iniciar sesión. Inténtalo de nuevo."
                )
            }
        }
    }

    fun cerrarSesion() {
        ++versionAutorizacion
        _sesion.value = UiState.Cargando
        repository.cerrarSesion()
        _estado.value = LoginUiState()
        _sesion.value = UiState.Exito(null)
    }
}
