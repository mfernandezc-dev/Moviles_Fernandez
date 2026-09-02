package com.fernandez.lab02carritokotlin

abstract class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
) {
    abstract fun calcularImporte(): Double
    open fun mostrarInfo(): String {
        return nombre
    }
}

class ProductoElectronico(
    nombre: String,
    precio: Double,
    cantidad: Int,
    val garantiaMeses: Int

) : Producto(nombre, precio, cantidad) {
    override fun calcularImporte(): Double {
        return precio * cantidad
    }
    override fun mostrarInfo(): String {
        return "$nombre - Electronico - Garantia: $garantiaMeses meses"
    }
}

class ProductoRopa(
    nombre: String,
    precio: Double,
    cantidad: Int,
    val descuento: Double
) : Producto(nombre, precio, cantidad) {

    override fun calcularImporte(): Double {
        val subtotal = precio * cantidad
        return subtotal - (subtotal * descuento)
    }
    override fun mostrarInfo(): String {
        return "$nombre - Ropa - Descuento ${(descuento * 100)}%"
    }
}


class Carrito {
    private val productos = mutableListOf<Producto>()

    fun agregarProducto(producto: Producto) {
        productos.add(producto)
    }

    fun eliminarProducto(nombre: String): Boolean {
        return productos.removeIf {
            it.nombre.equals(nombre, ignoreCase = true)
        }
    }

    fun obtenerProductos(): List<Producto> {
        return productos
    }

    fun calcularTotal(): Double {
        var total = 0.0
        for (producto in productos) {
            total += producto.calcularImporte()
        }
        return total
    }

    fun mostrarDetalle() {
        println("--------- DETALLE DEL CARRITO ---------")
        var contador = 1
        for (producto in productos) {
            println(
                "$contador. ${producto.mostrarInfo()} | " +
                        "Cantidad: ${producto.cantidad} | " +
                        "Importe: S/ ${"%.2f".format(producto.calcularImporte())}"
            )
            contador++
        }
        println("---------------------------------------")
    }
}

class Cliente(
    val nombre: String,
    val carrito: Carrito = Carrito()
)

class Factura(
    private val carrito: Carrito
) {
    fun calcularSubtotal(): Double {
        var subtotal = 0.0
        for (producto in carrito.obtenerProductos()) {
            subtotal += producto.calcularImporte()
        }
        return subtotal
    }
    fun calcularIGV(): Double {
        return calcularSubtotal() * 0.18
    }

    fun calcularTotal(): Double {
        return calcularSubtotal() + calcularIGV()
    }
    fun calcularDescuento(): Double {
        val total = calcularTotal()
        return when {
            total > 5000 -> total * 0.10
            total > 3000 -> total * 0.05
            else -> 0.0
        }
    }

    fun calcularTotalFinal(): Double {
        return calcularTotal() - calcularDescuento()
    }
    fun mostrarResumen() {
        val subtotal = calcularSubtotal()
        val igv = calcularIGV()
        val total = calcularTotal()
        val descuento = calcularDescuento()
        val totalFinal = calcularTotalFinal()
        println()
        println("========= RESUMEN DE COMPRA =========")
        println(
            "Subtotal: S/ ${"%.2f".format(subtotal)}"
        )
        println(
            "IGV (18%): S/ ${"%.2f".format(igv)}"
        )
        println(
            "Total: S/ ${"%.2f".format(total)}"
        )
        if(descuento > 0) {
            println(
                "Descuento aplicado: S/ ${"%.2f".format(descuento)}"
            )
        }
        else {
            println("Sin descuento aplicado")
        }
        println(
            "TOTAL FINAL: S/ ${"%.2f".format(totalFinal)}"
        )
        println("====================================")
    }
}

fun main() {

    val cliente = Cliente("Michael Fernandez")

    val catalogo = listOf(

        ProductoElectronico(
            "Laptop HP",
            2500.0,
            1,
            12
        ),

        ProductoElectronico(
            "Mouse Logitech",
            45.5,
            1,
            6
        ),

        ProductoElectronico(
            "Audifonos Sony",
            120.0,
            1,
            12
        ),

        ProductoRopa(
            "Polo deportivo",
            80.0,
            1,
            0.10
        )
    )
    var opcion = 0
    do {
        println()
        println("=================================")
        println("       TIENDA TECSUP")
        println("=================================")
        println("Cliente: ${cliente.nombre}")
        println()
        println("1. Mostrar catalogo")
        println("2. Agregar producto")
        println("3. Eliminar producto")
        println("4. Ver carrito")
        println("5. Ver resumen de compra")
        println("6. Salir")
        println()
        print("Seleccione una opcion: ")

        opcion = readln().toInt()
        when(opcion) {
            1 -> {
                println()
                println("--------- CATALOGO ---------")
                catalogo.forEachIndexed { index, producto ->
                    println(
                        "${index + 1}. ${producto.mostrarInfo()} - S/ ${producto.precio}"
                    )
                }
            }
            2 -> {
                println()
                println("Ingrese numero del producto:")
                catalogo.forEachIndexed { index, producto ->
                    println("${index + 1}. ${producto.nombre}")
                }
                val seleccion = readln().toInt()
                if(seleccion in 1..catalogo.size) {
                    val producto = catalogo[seleccion - 1]
                    cliente.carrito.agregarProducto(producto)
                    println("Producto agregado correctamente")
                }
                else {
                    println("Producto no existe")
                }
            }
            3 -> {
                print("Ingrese nombre del producto a eliminar: ")
                val nombre = readln()
                if(cliente.carrito.eliminarProducto(nombre)) {
                    println("Producto eliminado")
                }
                else {
                    println("No se encontró el producto")
                }
            }
            4 -> {
                cliente.carrito.mostrarDetalle()
            }
            5 -> {
                val factura = Factura(cliente.carrito)
                factura.mostrarResumen()
            }
            6 -> {
                println("Gracias por comprar")
            }
            else -> {
                println("Opción inválida")
            }
        }
    } while(opcion != 6)
}