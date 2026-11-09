package com.fernandez.catalogo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AzulNocturno,
    onPrimary = Color(0xFF15265F),
    primaryContainer = Color(0xFF293E86),
    onPrimaryContainer = Color(0xFFE0E6FF),
    secondary = Color(0xFFC0CAE7),
    secondaryContainer = Color(0xFF303E60),
    onSecondaryContainer = Color(0xFFDEE6FF),
    tertiary = Color(0xFF86D3DF),
    background = FondoOscuro,
    onBackground = Color(0xFFE1E7F5),
    surface = Color(0xFF141D30),
    onSurface = Color(0xFFE1E7F5),
    onSurfaceVariant = Color(0xFFBAC5DD),
    surfaceVariant = Color(0xFF26324A),
    surfaceContainerLowest = Color(0xFF10182A),
    surfaceContainerLow = Color(0xFF192238),
    surfaceContainer = Color(0xFF1D2840),
    surfaceContainerHigh = Color(0xFF26324A),
    surfaceContainerHighest = Color(0xFF303D57),
    outline = Color(0xFF8C9AB6),
    outlineVariant = Color(0xFF3B4964)
)

private val LightColorScheme = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = Color(0xFF102363),
    secondary = Color(0xFF58658C),
    secondaryContainer = Color(0xFFEEF0FB),
    onSecondaryContainer = Color(0xFF202B4B),
    tertiary = Color(0xFF006A76),
    background = FondoClaro,
    onBackground = IndigoTexto,
    surface = Color.White,
    onSurface = IndigoTexto,
    onSurfaceVariant = Color(0xFF4D5D78),
    surfaceVariant = Color(0xFFEDF1F8),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF0F3FA),
    surfaceContainer = Color(0xFFEDF1F8),
    surfaceContainerHigh = Color(0xFFE7ECF6),
    surfaceContainerHighest = Color(0xFFE1E6F1),
    outline = Color(0xFF74809B),
    outlineVariant = Color(0xFFD9E0EE)
)

@Composable
fun CatalogoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // La paleta propia mantiene la identidad azul/índigo en todos los dispositivos.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
