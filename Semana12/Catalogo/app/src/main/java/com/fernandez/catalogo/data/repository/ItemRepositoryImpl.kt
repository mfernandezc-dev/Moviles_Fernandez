package com.fernandez.catalogo.data.repository

import com.fernandez.catalogo.data.local.ItemDao
import com.fernandez.catalogo.data.mapper.toEntity
import com.fernandez.catalogo.data.mapper.toItem
import com.fernandez.catalogo.data.remote.ApiService
import com.fernandez.catalogo.domain.model.Item
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

class ItemRepositoryImpl(
    private val api: ApiService,
    private val dao: ItemDao
) : ItemRepository {
    override val items: Flow<List<Item>> = dao.todos().map { entidades ->
        entidades.map { it.toItem() }
    }

    override val favoritos: Flow<List<Item>> = dao.favoritos().map { entidades ->
        entidades.map { it.toItem() }
    }

    override suspend fun refrescar(): Result<Int> = try {
        val productos = api.listar().products
        val idsFavoritos = dao.idsFavoritos().toSet()
        val entidades = productos.map { it.toEntity(esFavorito = it.id in idsFavoritos) }
        dao.guardarTodos(entidades)
        Result.success(entidades.size)
    } catch (error: IOException) {
        Result.failure(error)
    } catch (error: HttpException) {
        Result.failure(error)
    } catch (error: SerializationException) {
        Result.failure(error)
    }

    override suspend fun obtenerDetalle(id: Int): Result<Item> = try {
        Result.success(api.obtenerDetalle(id).toItem())
    } catch (error: IOException) {
        Result.failure(error)
    } catch (error: HttpException) {
        Result.failure(error)
    } catch (error: SerializationException) {
        Result.failure(error)
    }

    override suspend fun alternarFavorito(id: Int) {
        dao.alternarFavorito(id)
    }

    override suspend fun cantidadGuardada(): Int = dao.contar()

    override suspend fun cantidadFavoritos(): Int = dao.contarFavoritos()

    override suspend fun limpiarCache() {
        dao.borrarTodo()
    }
}
