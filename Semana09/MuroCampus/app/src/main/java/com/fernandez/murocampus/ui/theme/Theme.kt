package com.fernandez.murocampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Blanco,
    secondary = AzulPrincipal,
    onSecondary = Blanco,
    background = FondoGeneral,
    onBackground = TextoPrincipal,
    surface = FondoGeneral,
    onSurface = TextoPrincipal
)

@Composable
fun MuroCampusTheme(
    content: @Composable () -> Unit
) {
    // La base mantiene la paleta de la guía, sin colores dinámicos del dispositivo.
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
