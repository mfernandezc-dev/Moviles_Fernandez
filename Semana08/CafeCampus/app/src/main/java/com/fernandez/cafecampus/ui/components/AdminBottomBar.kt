
package com.fernandez.cafecampus.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AdminBottomBar(
    seleccionado: String,
    onPanel: () -> Unit,
    onPedidos: () -> Unit,
    onMenu: () -> Unit,
    onAjustes: () -> Unit
) {
    val indigo = Color(0xFF4F46E5)
    val gris = Color(0xFF9CA3AF)

    val colores = NavigationBarItemDefaults.colors(
        selectedIconColor = indigo,
        selectedTextColor = indigo,
        indicatorColor = Color.Transparent,
        unselectedIconColor = gris,
        unselectedTextColor = gris
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        NavigationBarItem(
            selected = seleccionado == "panel",
            onClick = onPanel,
            icon = {
                Icon(
                    Icons.Default.Dashboard,
                    contentDescription = "Panel"
                )
            },
            label = { Text("Panel") },
            colors = colores
        )

        NavigationBarItem(
            selected = seleccionado == "pedidos",
            onClick = onPedidos,
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = "Pedidos"
                )
            },
            label = { Text("Pedidos") },
            colors = colores
        )

        NavigationBarItem(
            selected = seleccionado == "menu",
            onClick = onMenu,
            icon = {
                Icon(
                    Icons.Default.RestaurantMenu,
                    contentDescription = "Menú"
                )
            },
            label = { Text("Menú") },
            colors = colores
        )

        NavigationBarItem(
            selected = seleccionado == "ajustes",
            onClick = onAjustes,
            icon = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Ajustes"
                )
            },
            label = { Text("Ajustes") },
            colors = colores
        )
    }
}
