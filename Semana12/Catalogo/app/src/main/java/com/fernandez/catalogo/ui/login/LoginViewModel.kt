package com.fernandez.catalogo.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandez.catalogo.data.auth.AuthRepository
import com.fernandez.catalogo.domain.model.Sesion
import com.fernandez.catalogo.ui.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    val sesion: StateFlow<UiState<Sesion?>> = repository.sesiones
        .map<Sesion?, UiState<Sesion?>> { UiState.Exito(it) }
        .catch { emit(UiState.Error("No se pudo comprobar la sesión. Vuelve a abrir la aplicación.")) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Cargando)

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
        repository.cerrarSesion()
        _estado.value = LoginUiState()
    }
}
