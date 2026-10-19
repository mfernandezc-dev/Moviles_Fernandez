
package com.fernandez.cafecampus.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ClienteBottomBar(
    seleccionado: String,
    onMenu: () -> Unit,
    onPedidos: () -> Unit,
    onPerfil: () -> Unit
) {
    val naranja = Color(0xFFEA580C)
    val gris = Color(0xFF9CA3AF)

    val coloresNavegacion = NavigationBarItemDefaults.colors(
        selectedIconColor = naranja,
        selectedTextColor = naranja,
        indicatorColor = Color.Transparent,
        unselectedIconColor = gris,
        unselectedTextColor = gris
    )
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        NavigationBarItem(
            selected = seleccionado == "menu",
            onClick = onMenu,
            icon = {
                Icon(Icons.Default.Home, contentDescription = null)
            },
            label = { Text("Menú") },
            colors = coloresNavegacion
        )

        NavigationBarItem(
            selected = seleccionado == "pedidos",
            onClick = onPedidos,
            icon = {
                Icon(Icons.Default.List, contentDescription = null)
            },
            label = { Text("Pedidos") },
            colors = coloresNavegacion
        )

        NavigationBarItem(
            selected = seleccionado == "perfil",
            onClick = onPerfil,
            icon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            label = { Text("Perfil") },
            colors = coloresNavegacion
        )
    }
}
