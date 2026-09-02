package com.fernandez.lab02carritokotlin

data class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
)


// Función para calcular subtotal
fun calcularSubtotal(productos: List<Producto>): Double {

    var subtotal = 0.0

    for (p in productos) {

        subtotal += p.precio * p.cantidad

    }

    return subtotal
}


// Función para calcular IGV
fun calcularIGV(subtotal: Double): Double {
    return subtotal * 0.18
}

// Función para calcular total
fun calcularTotal(subtotal: Double, igv: Double): Double {
    return subtotal + igv
}

// Función para calcular descuento
fun calcularDescuento(total: Double): Double {
    return when {
        total > 5000 -> total * 0.10
        total > 3000 -> total * 0.05
        else -> 0.0
    }
}

fun mostrarDetalle(productos: List<Producto>) {
    println("--------- DETALLE DEL CARRITO ---------")
    var i = 1
    for (p in productos) {
        val importe = p.precio * p.cantidad
        println(
            String.format(
                "%d. %-20s x%d S/ %8.2f",
                i,
                p.nombre,
                p.cantidad,
                importe
            )
        )
        i++
    }
    println("---------------------------------------")
}

fun main() {


    println("=========================================")
    println(" CARRITO DE COMPRAS - TIENDA TECSUP ")
    println("=========================================")


    val nombreCliente = "Michael Fernandez"

    val carrito = mutableListOf<Producto>()


    println("Cliente: $nombreCliente")
    println()


    carrito.add(Producto("Laptop HP", 2500.0, 1))
    carrito.add(Producto("Mouse Logitech", 45.5, 2))
    carrito.add(Producto("Audifonos Sony", 120.0, 1))


    mostrarDetalle(carrito)

    println()

    println("Cantidad de productos: ${carrito.size}")

    println()


    val subtotal = calcularSubtotal(carrito)

    val igv = calcularIGV(subtotal)

    val total = calcularTotal(subtotal, igv)

    val descuento = calcularDescuento(total)

    val totalConDescuento = total - descuento



    println("Subtotal : S/ ${"%.2f".format(subtotal)}")

    println("IGV (18%): S/ ${"%.2f".format(igv)}")

    println("TOTAL    : S/ ${"%.2f".format(total)}")
    println()

    val masCaro = carrito.maxByOrNull { it.precio }
    if (masCaro != null) {
        println(
            "Producto mas caro: ${masCaro.nombre} " +
                    String.format("(S/ %.2f)", masCaro.precio)
        )
    }
    println()

    if (descuento > 0) {
        println("Descuento aplicado: S/ ${"%.2f".format(descuento)}")
    }
    else {
        println("Sin descuento aplicado")
    }
    println("TOTAL CON DESCUENTO: S/ ${"%.2f".format(totalConDescuento)}")


}