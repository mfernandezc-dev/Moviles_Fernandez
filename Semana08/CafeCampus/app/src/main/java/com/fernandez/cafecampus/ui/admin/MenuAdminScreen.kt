
package com.fernandez.cafecampus.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernandez.cafecampus.data.local.entity.Producto
import com.fernandez.cafecampus.ui.components.ProductoIcono

private val IndigoMenu = Color(0xFF4F46E5)
private val GrisMenu = Color(0xFF6B7280)
private val BordeMenu = Color(0xFFE5E7EB)
private val NegroMenu = Color(0xFF1F2937)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuAdminScreen(
    productos: List<Producto>,
    onAgregar: () -> Unit,
    onEditar: (Int) -> Unit,
    onEliminar: (Producto) -> Unit,
    onDisponibilidad: (Producto) -> Unit
) {
    var productoAEliminar by remember {
        mutableStateOf<Producto?>(null)
    }

    var categoriaSeleccionada by remember {
        mutableStateOf("Todo")
    }

    // Categorías principales y adicionales registradas en Room.
    val categorias = listOf(
        "Todo",
        "Desayunos",
        "Almuerzos",
        "Bebidas"
    ) + productos
        .map { it.categoria }
        .distinct()
        .filter { categoria ->
            categoria.isNotBlank() &&
                    categoria !in listOf(
                "Todo",
                "Desayunos",
                "Almuerzos",
                "Bebidas"
            )
        }

    // Filtrado según la categoría seleccionada.
    val productosFiltrados = productos.filter { producto ->
        categoriaSeleccionada == "Todo" ||
                producto.categoria.equals(
                    categoriaSeleccionada,
                    ignoreCase = true
                )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gestión del menú",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NegroMenu
                    )
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregar,
                containerColor = IndigoMenu,
                contentColor = Color.White,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar producto",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // CATEGORÍAS
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorias) { categoria ->

                    val seleccionado =
                        categoriaSeleccionada == categoria

                    FilterChip(
                        selected = seleccionado,
                        onClick = {
                            categoriaSeleccionada = categoria
                        },
                        label = {
                            Text(
                                text = categoria,
                                fontSize = 12.sp,
                                fontWeight = if (seleccionado)
                                    FontWeight.SemiBold
                                else
                                    FontWeight.Medium,
                                color = if (seleccionado)
                                    Color.White
                                else
                                    GrisMenu
                            )
                        },
                        shape = RoundedCornerShape(22.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White,
                            selectedContainerColor = IndigoMenu
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = seleccionado,
                            borderColor = BordeMenu,
                            selectedBorderColor = IndigoMenu
                        )
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // LISTA DE PRODUCTOS
            if (productosFiltrados.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay productos en esta categoría",
                        color = GrisMenu,
                        textAlign = TextAlign.Center
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = 88.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        productosFiltrados,
                        key = { it.id }
                    ) { producto ->

                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = Color.White
                            ),
                            border = BorderStroke(1.dp, BordeMenu)
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement =
                                    Arrangement.spacedBy(10.dp)
                            ) {

                                // ICONO DEL PRODUCTO
                                ProductoIcono(
                                    icono = producto.icono,
                                    size = 58.dp
                                )

                                // INFORMACIÓN DEL PRODUCTO
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement =
                                        Arrangement.spacedBy(5.dp)
                                ) {
                                    Text(
                                        text = producto.nombre,
                                        color = NegroMenu,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        text = "${producto.categoria} · S/ %.2f"
                                            .format(producto.precio),
                                        color = GrisMenu,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    // ETIQUETA DE DISPONIBILIDAD
                                    Surface(
                                        color = if (producto.disponible)
                                            Color(0xFFDCFCE7)
                                        else
                                            Color(0xFFFEE2E2),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text(
                                            text = if (producto.disponible)
                                                "Disponible"
                                            else
                                                "Agotado",
                                            color = if (producto.disponible)
                                                Color(0xFF16A34A)
                                            else
                                                Color(0xFFDC2626),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(
                                                horizontal = 9.dp,
                                                vertical = 4.dp
                                            )
                                        )
                                    }
                                }

                                // EDITAR
                                IconButton(
                                    onClick = {
                                        onEditar(producto.id)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = "Editar producto",
                                        tint = IndigoMenu,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }

                                // ELIMINAR
                                IconButton(
                                    onClick = {
                                        productoAEliminar = producto
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "Eliminar producto",
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }

                            // CONTROL DE DISPONIBILIDAD
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 12.dp,
                                        end = 12.dp,
                                        bottom = 8.dp
                                    ),
                                horizontalArrangement =
                                    Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Disponible para pedidos",
                                    fontSize = 12.sp,
                                    color = GrisMenu
                                )

                                Switch(
                                    checked = producto.disponible,
                                    onCheckedChange = {
                                        onDisponibilidad(producto)
                                    },
                                    modifier = Modifier.height(32.dp),
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = IndigoMenu,
                                        checkedThumbColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // CONFIRMACIÓN ANTES DE ELIMINAR
    productoAEliminar?.let { producto ->

        AlertDialog(
            onDismissRequest = {
                productoAEliminar = null
            },

            title = {
                Text("Eliminar producto")
            },

            text = {
                Text(
                    "¿Seguro que deseas eliminar ${producto.nombre}? " +
                            "Esta acción no se puede deshacer."
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        onEliminar(producto)
                        productoAEliminar = null
                    }
                ) {
                    Text(
                        text = "Eliminar",
                        color = Color(0xFFDC2626)
                    )
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        productoAEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
