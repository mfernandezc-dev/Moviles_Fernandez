package com.fernandez.cafecampus.data.local.dao

import androidx.room.*
import com.fernandez.cafecampus.data.local.entity.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY nombre")
    fun observarTodos(): Flow<List<Producto>>

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(p: Producto): Long

    @Update
    suspend fun actualizar(p: Producto)

    @Delete
    suspend fun eliminar(p: Producto)

    @Query("UPDATE productos SET disponible = :d WHERE id = :id")
    suspend fun cambiarDisponibilidad(id: Int, d: Boolean)
}