package com.fernandez.cafecampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fernandez.cafecampus.data.local.dao.*
import com.fernandez.cafecampus.data.local.entity.*

@Database( // Registra las 4 entidades
    entities = [
        Producto::class,
        Usuario::class,
        Pedido::class,
        DetallePedido::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CafeDatabase : RoomDatabase() { // permite administrar la base de datos

    // Proporciona acceso a los DAO (consultas)
    abstract fun productoDao(): ProductoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun pedidoDao(): PedidoDao

    companion object {
        @Volatile
        private var INSTANCE: CafeDatabase? = null

        fun obtener(context: Context): CafeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    CafeDatabase::class.java,
                    "cafecampus.db"
                ).build()

                INSTANCE = instancia
                instancia
            }
        }
    }
}