package com.fernandez.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernandez.murocampus.data.PostRepository
import com.fernandez.murocampus.data.mensajeDe
import com.fernandez.murocampus.model.Comentario
import com.fernandez.murocampus.model.NuevoPost
import com.fernandez.murocampus.model.Post
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetalleUi(
    val post: Post,
    val comentarios: List<Comentario>
)

class PostsViewModel(
    private val repo: PostRepository
) : ViewModel() {

    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista = _lista.asStateFlow()
    private var carga: Job? = null
    private var versionLista = 0L

    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro = _usuarioFiltro.asStateFlow()

    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle = _detalle.asStateFlow()
    private var cargaDetalle: Job? = null
    private var versionDetalle = 0L

    private val _creacion = MutableStateFlow<UiState<Post>?>(null)
    val creacion = _creacion.asStateFlow()
    private var cargaCreacion: Job? = null
    private var versionCreacion = 0L

    init {
        cargar()
    }

    fun cargar() {
        if (carga?.isActive == true) return

        val filtroActual = _usuarioFiltro.value
        val version = ++versionLista
        _lista.value = UiState.Cargando
        carga = viewModelScope.launch {
            val resultado = if (filtroActual == null) repo.listar() else repo.porUsuario(filtroActual)
            ensureActive()
            if (version != versionLista) return@launch
            resultado
                .onSuccess { posts ->
                    _lista.value = UiState.Exito(posts)
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    _lista.value = UiState.Error(mensajeDe(error))
                }
        }
    }

    fun filtrar(userId: Int?) {
        if (_usuarioFiltro.value == userId) return
        carga?.cancel()
        carga = null
        _usuarioFiltro.value = userId
        cargar()
    }

    fun abrir(id: Int) {
        cerrarDetalle()
        val version = versionDetalle
        _detalle.value = UiState.Cargando
        cargaDetalle = viewModelScope.launch {
            try {
                val datos = coroutineScope {
                    val post = async { repo.detalle(id).getOrThrow() }
                    val comentarios = async { repo.comentarios(id).getOrThrow() }
                    DetalleUi(post.await(), comentarios.await())
                }
                ensureActive()
                if (version == versionDetalle) _detalle.value = UiState.Exito(datos)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                ensureActive()
                if (version == versionDetalle) _detalle.value = UiState.Error(mensajeDe(e))
            }
        }
    }

    fun cerrarDetalle() {
        versionDetalle++
        cargaDetalle?.cancel()
        cargaDetalle = null
    }

    fun prepararCreacion() {
        cerrarCreacion()
        _creacion.value = null
    }

    fun publicar(titulo: String, texto: String) {
        if (titulo.isBlank() || texto.isBlank() || cargaCreacion?.isActive == true) return

        val nuevo = NuevoPost(userId = 1, title = titulo.trim(), body = texto.trim())
        val version = ++versionCreacion
        _creacion.value = UiState.Cargando
        cargaCreacion = viewModelScope.launch {
            try {
                val resultado = repo.crear(nuevo)
                ensureActive()
                if (version != versionCreacion) return@launch
                resultado
                    .onSuccess { post -> _creacion.value = UiState.Exito(post) }
                    .onFailure { error ->
                        if (error is CancellationException) throw error
                        _creacion.value = UiState.Error(mensajeDe(error))
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                ensureActive()
                if (version == versionCreacion) _creacion.value = UiState.Error(mensajeDe(e))
            }
        }
    }

    fun cerrarCreacion() {
        versionCreacion++
        cargaCreacion?.cancel()
        cargaCreacion = null
    }
}
