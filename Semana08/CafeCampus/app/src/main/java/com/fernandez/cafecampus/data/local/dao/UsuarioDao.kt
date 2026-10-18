package com.fernandez.cafecampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fernandez.cafecampus.data.local.entity.Usuario

@Dao
interface UsuarioDao {

    @Query("""
        SELECT * FROM usuarios
        WHERE correo = :correo AND clave = :clave
        LIMIT 1
    """)

    // Buscamos un usuario con el correo y la clave
    suspend fun login(
        correo: String,
        clave: String
    ): Usuario?

    @Insert
    suspend fun insertar(usuario: Usuario): Long  // registra un nuevo usuario

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int  // cuenta la cantidad de usuarios
}