package com.fernandez.lab04carritotecsup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCarrito() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var itemSeleccionado by remember { mutableStateOf("Carrito") }

    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }

    val productos = remember { mutableStateListOf<Producto>() }

    val subtotal = productos.sumOf { it.precio * it.cantidad }
    val igv = subtotal * 0.18
    val total = subtotal + igv

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "TECSUP Store",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    label = { Text("Carrito") },
                    selected = itemSeleccionado == "Carrito",
                    onClick = {
                        itemSeleccionado = "Carrito"
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text("Acerca de") },
                    selected = itemSeleccionado == "AcercaDe",
                    onClick = {
                        itemSeleccionado = "AcercaDe"
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (itemSeleccionado == "Carrito") "Carrito Tecsup" else "Acerca de") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "MENU")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (itemSeleccionado == "Carrito") {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del producto") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        OutlinedTextField(
                            value = precio,
                            onValueChange = { precio = it },
                            label = { Text("Precio") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = cantidad,
                            onValueChange = { cantidad = it },
                            label = { Text("Cantidad") },
                            modifier = Modifier.weight(1f)
                        )

                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val precioNum = precio.toDoubleOrNull() ?: 0.0
                            val cantidadNum = cantidad.toIntOrNull() ?: 0
                            if (nombre.isNotBlank() && precioNum > 0 && cantidadNum > 0) {
                                productos.add(
                                    Producto(
                                        nombre,
                                        precioNum,
                                        cantidadNum
                                    )
                                )
                                nombre = ""
                                precio = ""
                                cantidad = ""
                            }

                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("AGREGAR")
                    }

                    if (productos.isEmpty()) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = "Tu carrito está vacío"
                                )

                                Text(
                                    text = "Agrega tu primer producto"
                                )

                            }

                        }

                    } else {

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            items(productos) { producto ->

                                TarjetaProducto(
                                    producto = producto,
                                    onEliminar = {
                                        productos.remove(producto)
                                    }
                                )

                            }

                        }

                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Text("Productos: ${productos.size}")

                                Text("")
                            }


                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Text("Subtotal")

                                Text(
                                    text = "S/ %.2f".format(subtotal)
                                )

                            }


                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Text("IGV (18%)")

                                Text(
                                    text = "S/ %.2f".format(igv)
                                )

                            }


                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Text(
                                    text = "TOTAL"
                                )

                                Text(
                                    text = "S/ %.2f".format(total),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )

                            }

                        }

                    }

                }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("TECSUP Store v1.0")
                    }
                }
            }
        }
    }
}


@Composable
fun TarjetaProducto(
    producto: Producto,
    onEliminar: () -> Unit
) {
    // Estado para controlar la visibilidad del menú contextual
    var menuExpandido by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = producto.nombre)
                Text(text = "S/ ${producto.precio} x ${producto.cantidad}")
            }

            Text(
                text = "S/ %.2f".format(producto.precio * producto.cantidad)
            )

            // Ícono de tres puntos
            Box {
                IconButton(onClick = { menuExpandido = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones"
                    )
                }

                DropdownMenuItem(
                    text = { Text("Eliminar") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar producto"
                        )
                    },
                    onClick = {
                        menuExpandido = false
                        onEliminar()
                    }
                )
            }
        }
    }
}