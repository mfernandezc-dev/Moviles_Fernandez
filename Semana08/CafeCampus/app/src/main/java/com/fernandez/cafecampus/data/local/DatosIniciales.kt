package com.fernandez.cafecampus.data.local

import com.fernandez.cafecampus.data.local.entity.Producto
import com.fernandez.cafecampus.data.local.entity.Usuario

object DatosIniciales {

    suspend fun sembrar(db: CafeDatabase) {

        if (db.usuarioDao().contar() > 0) {
            return
        }

        val productos = listOf(
            Producto(
                nombre = "Café americano",
                descripcion = "Café caliente tradicional",
                categoria = "Bebidas",
                precio = 4.00,
                icono = "cafe"
            ),
            Producto(
                nombre = "Sándwich de pollo",
                descripcion = "Sándwich preparado con pollo",
                categoria = "Comidas",
                precio = 9.00,
                icono = "pan"
            ),
            Producto(
                nombre = "Menú del día",
                descripcion = "Almuerzo completo",
                categoria = "Comidas",
                precio = 12.00,
                icono = "plato"
            ),
            Producto(
                nombre = "Jugo de naranja",
                descripcion = "Jugo natural de naranja",
                categoria = "Bebidas",
                precio = 5.00,
                icono = "jugo"
            ),
            Producto(
                nombre = "Empanada",
                descripcion = "Empanada recién horneada",
                categoria = "Snacks",
                precio = 4.50,
                icono = "pan"
            ),
            Producto(
                nombre = "Chicha morada",
                descripcion = "Bebida tradicional peruana",
                categoria = "Bebidas",
                precio = 3.50,
                icono = "botella"
            )
        )

        productos.forEach { producto ->
            db.productoDao().insertar(producto)
        }

        db.usuarioDao().insertar(
            Usuario(
                nombre = "Michael",
                correo = "michael@campus.test",
                clave = "1234",
                rol = "CLIENTE"
            )
        )

        db.usuarioDao().insertar(
            Usuario(
                nombre = "Administrador",
                correo = "admin@cafe.test",
                clave = "admin",
                rol = "ADMIN"
            )
        )
    }
}