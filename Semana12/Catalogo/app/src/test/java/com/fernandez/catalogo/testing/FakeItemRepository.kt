package com.fernandez.catalogo.testing

import com.fernandez.catalogo.data.repository.ItemRepository
import com.fernandez.catalogo.domain.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/** Repositorio de prueba completamente en memoria, sin servicios externos. */
class FakeItemRepository(
    productosIniciales: List<Item> = emptyList(),
    var resultadoRefrescar: Result<Int> = Result.success(0),
    var productosTrasRefrescar: List<Item>? = null
) : ItemRepository {
    private val productos = MutableStateFlow(productosIniciales.toList())
    override val items: StateFlow<List<Item>> = productos.asStateFlow()
    override val favoritos: Flow<List<Item>> = productos.map { lista ->
        lista.filter { it.esFavorito }
    }

    var llamadasRefrescar: Int = 0
        private set

    private val alternados = mutableListOf<Int>()
    val idsFavoritosAlternados: List<Int>
        get() = alternados.toList()

    fun emitirProductos(lista: List<Item>) {
        productos.value = lista.toList()
    }

    override suspend fun refrescar(): Result<Int> {
        llamadasRefrescar += 1
        if (resultadoRefrescar.isSuccess) {
            productosTrasRefrescar?.let(::emitirProductos)
        }
        return resultadoRefrescar
    }

    override suspend fun obtenerDetalle(id: Int): Result<Item> {
        val producto = productos.value.firstOrNull { it.id == id }
        return if (producto != null) {
            Result.success(producto)
        } else {
            Result.failure(NoSuchElementException("Producto de prueba no encontrado: $id"))
        }
    }

    override suspend fun alternarFavorito(id: Int) {
        alternados += id
        emitirProductos(productos.value.map { producto ->
            if (producto.id == id) producto.copy(esFavorito = !producto.esFavorito) else producto
        })
    }

    override suspend fun cantidadGuardada(): Int = productos.value.size

    override suspend fun cantidadFavoritos(): Int = productos.value.count { it.esFavorito }

    override suspend fun limpiarCache() {
        emitirProductos(emptyList())
    }
}
