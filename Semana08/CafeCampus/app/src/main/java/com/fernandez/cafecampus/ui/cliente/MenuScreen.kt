package com.fernandez.cafecampus.ui.cliente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernandez.cafecampus.ui.components.ProductoCard
import com.fernandez.cafecampus.viewmodel.MenuViewModel

@Composable
fun MenuScreen(
    vm: MenuViewModel,
    onProducto: (Int) -> Unit,
    onCarrito: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val productos by vm.productos.collectAsState()
    val busqueda by vm.busqueda.collectAsState()
    val categoriaSeleccionada by vm.categoriaSeleccionada.collectAsState()
    val carrito by vm.carrito.collectAsState()

    val cantidadCarrito = carrito.sumOf { it.cantidad }

    val categorias = listOf(
        "Todo",
        "Desayunos",
        "Almuerzos",
        "Bebidas"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ENCABEZADO

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // SALUDO
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "¡Hola, Michael!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "¿Qué se te antoja hoy?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ICONOS DEL CARRITO Y CERRAR SESIÓN
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                BadgedBox(
                    badge = {
                        if (cantidadCarrito > 0) {
                            Badge {
                                Text("$cantidadCarrito")
                            }
                        }
                    }
                ) {
                    IconButton(onClick = onCarrito) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = "Ver carrito"
                        )
                    }
                }

                IconButton(onClick = onCerrarSesion) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Cerrar sesión",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }


        Spacer(Modifier.height(24.dp))

        // BUSCADOR EDITABLE
        OutlinedTextField(
            value = busqueda,
            onValueChange = { vm.actualizarBusqueda(it) },
            placeholder = {
                Text("Buscar en el menú...")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (busqueda.isNotEmpty()) {
                    IconButton(onClick = { vm.actualizarBusqueda("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar búsqueda"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // CATEGORÍAS

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categorias.forEach { opcion ->

                FilterChip(
                    selected = categoriaSeleccionada == opcion,
                    onClick = { vm.seleccionarCategoria(opcion) },
                    label = {
                        Text(
                            text = opcion,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor =
                            MaterialTheme.colorScheme.primary,
                        selectedLabelColor =
                            MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }


        Spacer(Modifier.height(20.dp))

        Text(
            text = "Populares hoy",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(Modifier.height(12.dp))

        // PRODUCTOS O MENSAJE DE LISTA VACÍA
        if (productos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron productos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(
                    productos,
                    key = { it.id }
                ) { producto ->
                    ProductoCard(
                        producto = producto,
                        onClick = {
                            onProducto(producto.id)
                        }
                    )
                }
            }
        }
    }
}
