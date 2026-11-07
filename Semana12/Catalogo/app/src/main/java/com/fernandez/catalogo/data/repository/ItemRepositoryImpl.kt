package com.fernandez.catalogo.data.repository

import com.fernandez.catalogo.data.mapper.toItem
import com.fernandez.catalogo.data.remote.ApiService
import com.fernandez.catalogo.domain.model.Item
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

class ItemRepositoryImpl(private val api: ApiService) : ItemRepository {
    // Fuente temporal de H6; Room la reemplazará como fuente única de verdad.
    private val itemsEnMemoria = MutableStateFlow<List<Item>>(emptyList())
    override val items: Flow<List<Item>> = itemsEnMemoria.asStateFlow()

    // Los favoritos todavía no están implementados.
    override val favoritos: Flow<List<Item>> = flowOf(emptyList())

    override suspend fun refrescar(): Result<Int> = try {
        val productos = api.listar().products.map { it.toItem() }
        itemsEnMemoria.value = productos
        Result.success(productos.size)
    } catch (error: IOException) {
        Result.failure(error)
    } catch (error: HttpException) {
        Result.failure(error)
    } catch (error: SerializationException) {
        Result.failure(error)
    }

    override suspend fun obtenerDetalle(id: Int): Result<Item> = Result.failure(
        UnsupportedOperationException("El detalle de producto no está disponible en H6.")
    )

    override suspend fun alternarFavorito(id: Int) {
        throw UnsupportedOperationException("Los favoritos no están disponibles en H6.")
    }

    override suspend fun cantidadGuardada(): Int = itemsEnMemoria.value.size

    override suspend fun cantidadFavoritos(): Int = 0

    override suspend fun limpiarCache() {
        itemsEnMemoria.value = emptyList()
    }
}
