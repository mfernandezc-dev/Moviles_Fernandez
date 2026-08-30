package com.fernandez.prestamoconsole

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun main() {

    println("==========================================")
    println("          SISTEMA DE PRESTAMO")
    println("==========================================")

    // INGRESO DEL NOMBRE DEL PRODUCTO
    print("Ingrese el nombre del producto: ")
    val producto = readln()

    // INGRESO Y VALIDACION DEL PRECIO
    var precio: Double

    while (true) {

        print("Ingrese el precio del producto: S/ ")

        val entradaPrecio = readln().toDoubleOrNull()

        if (entradaPrecio != null && entradaPrecio > 0) {
            precio = entradaPrecio
            break
        } else {
            println("Error: ingrese un precio valido mayor a 0.")
        }
    }

    // INGRESO Y VALIDACION DE LA CANTIDAD
    var cantidad: Int

    while (true) {

        print("Ingrese la cantidad de productos: ")

        val entradaCantidad = readln().toIntOrNull()

        if (entradaCantidad != null && entradaCantidad > 0) {
            cantidad = entradaCantidad
            break
        } else {
            println("Error: ingrese una cantidad valida mayor a 0.")
        }
    }

    // INGRESO Y VALIDACION DEL NUMERO DE CUOTAS
    var cuotas: Int

    while (true) {

        println()
        println("Seleccione el numero de cuotas:")
        println("6 cuotas  -> 20% de interes")
        println("12 cuotas -> 40% de interes")
        println("24 cuotas -> 60% de interes")

        print("Ingrese el numero de cuotas: ")

        val entradaCuotas = readln().toIntOrNull()

        if (entradaCuotas == 6 || entradaCuotas == 12 || entradaCuotas == 24) {
            cuotas = entradaCuotas
            break
        } else {
            println("Error: solo puede ingresar 6, 12 o 24 cuotas.")
        }
    }

    // ASIGNAR INTERES SEGUN EL NUMERO DE CUOTAS
    val interesPorcentaje = when (cuotas) {
        6 -> 20
        12 -> 40
        24 -> 60
        else -> 0
    }

    // CALCULAR MONTO INICIAL
    val montoInicial = precio * cantidad

    // CALCULAR INTERES EN DINERO
    val interesDinero = montoInicial * interesPorcentaje / 100

    // CALCULAR MONTO TOTAL A PAGAR
    val montoPagar = montoInicial + interesDinero

    // CALCULAR PAGO MENSUAL
    val pagoMensual = montoPagar / cuotas

    // MOSTRAR RESUMEN
    println()
    println("==========================================")
    println("              RESUMEN DE COMPRA")
    println("==========================================")

    println("Producto: $producto")
    println("Precio: S/ ${String.format(Locale.US, "%.2f", precio)}")
    println("Cantidad: $cantidad")
    println("Cuotas: $cuotas")
    println("Monto inicial: S/ ${String.format(Locale.US, "%.2f", montoInicial)}")
    println("Interes: $interesPorcentaje%")
    println("Interes en dinero: S/ ${String.format(Locale.US, "%.2f", interesDinero)}")
    println("Monto a pagar: S/ ${String.format(Locale.US, "%.2f", montoPagar)}")
    println("Pago mensual: S/ ${String.format(Locale.US, "%.2f", pagoMensual)}")

    // CALENDARIO DE PAGOS
    println()
    println("======================================================================")
    println("                         CALENDARIO DE PAGOS")
    println("======================================================================")

    println(
        String.format(
            "%-5s %-12s %-15s %-15s %-15s",
            "N°",
            "Fecha",
            "Monto",
            "P.Mensual",
            "RestaPago"
        )
    )

    println("----------------------------------------------------------------------")

    // FECHA ACTUAL
    val fechaActual = LocalDate.now()

    // FORMATO DE LA FECHA
    val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // EL SALDO INICIA CON EL MONTO TOTAL A PAGAR
    var saldo = montoPagar

    // GENERAR LAS CUOTAS
    for (numero in 1..cuotas) {

        // FECHA DE CADA PAGO
        val fechaPago = fechaActual.plusMonths(numero.toLong())

        // CALCULAR LO QUE QUEDA DESPUES DEL PAGO
        val restaPago = if (numero == cuotas) {
            0.0
        } else {
            saldo - pagoMensual
        }

        println(
            String.format(
                Locale.US,
                "%-5d %-12s S/ %-12.2f S/ %-12.2f S/ %-12.2f",
                numero,
                fechaPago.format(formatoFecha),
                saldo,
                pagoMensual,
                restaPago
            )
        )

        // ACTUALIZAR EL SALDO
        saldo = restaPago
    }

    println("======================================================================")
}