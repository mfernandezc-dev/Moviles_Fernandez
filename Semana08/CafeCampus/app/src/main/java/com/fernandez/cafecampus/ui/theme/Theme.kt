package com.fernandez.cafecampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ClienteLightScheme = lightColorScheme(
    primary = ClientePrimario,
    onPrimary = Blanco,
    primaryContainer = ClienteContenedor,
    background = ClienteFondo,
    surface = Blanco,
    onBackground = TextoOscuro,
    onSurface = TextoOscuro
)

private val AdminLightScheme = lightColorScheme(
    primary = AdminPrimario,
    onPrimary = Blanco,
    primaryContainer = AdminContenedor,
    background = AdminFondo,
    surface = Blanco,
    onBackground = TextoOscuro,
    onSurface = TextoOscuro
)

private val ClienteDarkScheme = darkColorScheme(
    primary = Color(0xFFFF9B64),
    onPrimary = Color(0xFF431600),
    background = Color(0xFF17120F),
    surface = Color(0xFF241C18),
    onBackground = Blanco,
    onSurface = Blanco
)

private val AdminDarkScheme = darkColorScheme(
    primary = Color(0xFFB4AEFF),
    onPrimary = Color(0xFF211B69),
    background = Color(0xFF12121C),
    surface = Color(0xFF1E1E2B),
    onBackground = Blanco,
    onSurface = Blanco
)

@Composable
fun CafeCampusTheme(
    rol: String = "CLIENTE",
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        rol == "ADMIN" && darkTheme -> AdminDarkScheme
        rol == "ADMIN" -> AdminLightScheme
        darkTheme -> ClienteDarkScheme
        else -> ClienteLightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}