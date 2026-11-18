package com.fernandez.campusgps.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AzulPrincipal,
    onPrimary = Blanco,
    primaryContainer = Color(0xFF143E54),
    onPrimaryContainer = Color(0xFFD7EBF7),
    secondary = AzulSeleccion,
    tertiary = VerdeCercano,
    background = Color(0xFF11181D),
    onBackground = Color(0xFFE3EDF3),
    surface = Color(0xFF19232B),
    onSurface = Color(0xFFE3EDF3),
    surfaceVariant = Color(0xFF25323C),
    onSurfaceVariant = Color(0xFFBBCBD6),
    outline = Color(0xFF8CA3B3),
    error = TextoError,
    errorContainer = FondoError,
    onErrorContainer = TextoError
)

private val LightColorScheme = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Blanco,
    primaryContainer = FondoSeleccion,
    onPrimaryContainer = TextoPrincipal,
    secondary = AzulSeleccion,
    onSecondary = Blanco,
    tertiary = VerdeCercano,
    background = FondoCampus,
    onBackground = TextoPrincipal,
    surface = Blanco,
    onSurface = TextoPrincipal,
    surfaceVariant = FondoTarjeta,
    onSurfaceVariant = TextoSecundario,
    outline = AzulSeleccion,
    error = TextoError,
    errorContainer = FondoError,
    onErrorContainer = TextoError
)

@Composable
fun CampusGPSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // La paleta de CampusGPS no depende del fondo de pantalla del dispositivo.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
