
package com.fernandez.cafecampus.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fernandez.cafecampus.data.local.entity.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioProductoScreen(
    producto: Producto?,
    onGuardar: (Producto) -> Unit,
    onVolver: () -> Unit
) {
    var nombre by rememberSaveable(producto?.id) {
        mutableStateOf(producto?.nombre.orEmpty())
    }

    var descripcion by rememberSaveable(producto?.id) {
        mutableStateOf(producto?.descripcion.orEmpty())
    }

    var categoria by rememberSaveable(producto?.id) {
        mutableStateOf(producto?.categoria ?: "Bebidas")
    }

    var precio by rememberSaveable(producto?.id) {
        mutableStateOf(producto?.precio?.toString().orEmpty())
    }

    var disponible by rememberSaveable(producto?.id) {
        mutableStateOf(producto?.disponible ?: true)
    }

    var icono by rememberSaveable(producto?.id) {
        mutableStateOf(producto?.icono ?: "cafe")
    }

    var error by rememberSaveable(producto?.id) {
        mutableStateOf<String?>(null)
    }

    val categorias = listOf(
        "Desayunos",
        "Almuerzos",
        "Bebidas",
        "Postres"
    )

    val iconos = listOf(
        "cafe", "plato", "jugo", "pan", "botella", "dulce"
    )

    var categoriasAbiertas by remember {
        mutableStateOf(false)
    }

    var iconosAbiertos by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (producto == null)
                            "Nuevo producto"
                        else
                            "Editar producto",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    Spacer(Modifier.width(48.dp))
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    error = null
                },
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = categoriasAbiertas,
                onExpandedChange = {
                    categoriasAbiertas = !categoriasAbiertas
                }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = categoriasAbiertas
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = categoriasAbiertas,
                    onDismissRequest = {
                        categoriasAbiertas = false
                    }
                ) {
                    categorias.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                categoria = opcion
                                categoriasAbiertas = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = precio,
                onValueChange = {
                    precio = it
                    error = null
                },
                label = { Text("Precio (S/)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = {
                    descripcion = it
                },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 4
            )

            ExposedDropdownMenuBox(
                expanded = iconosAbiertos,
                onExpandedChange = {
                    iconosAbiertos = !iconosAbiertos
                }
            ) {
                OutlinedTextField(
                    value = icono,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Icono") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = iconosAbiertos
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = iconosAbiertos,
                    onDismissRequest = {
                        iconosAbiertos = false
                    }
                ) {
                    iconos.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                icono = opcion
                                iconosAbiertos = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Disponible para pedidos",
                    fontWeight = FontWeight.Medium
                )

                Switch(
                    checked = disponible,
                    onCheckedChange = {
                        disponible = it
                    }
                )
            }

            if (error != null) {
                Text(
                    text = error.orEmpty(),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val precioNumero = precio
                        .replace(',', '.')
                        .toDoubleOrNull()

                    when {
                        nombre.isBlank() -> {
                            error = "El nombre es obligatorio"
                        }

                        precioNumero == null ||
                                !precioNumero.isFinite() ||
                                precioNumero <= 0.0 -> {
                            error = "Ingresa un precio válido mayor que cero"
                        }

                        else -> {
                            val nuevoProducto = Producto(
                                id = producto?.id ?: 0,
                                nombre = nombre.trim(),
                                descripcion = descripcion.trim(),
                                categoria = categoria,
                                precio = precioNumero,
                                disponible = disponible,
                                icono = icono
                            )

                            onGuardar(nuevoProducto)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4F46E5)
                )
            ) {
                Text(
                    text = if (producto == null)
                        "Guardar producto"
                    else
                        "Guardar cambios"
                )
            }

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancelar")
            }
        }
    }
}
