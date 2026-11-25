
package com.fernandez.avisoscampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaColores = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = BlancoTarjeta,

    secondary = AzulOscuro,
    onSecondary = BlancoTarjeta,

    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulOscuro,

    background = FondoPantalla,
    onBackground = TextoPrincipal,

    surface = BlancoTarjeta,
    onSurface = TextoPrincipal,

    surfaceVariant = AzulClaro,
    onSurfaceVariant = TextoSecundario,

    outline = BordeTarjeta,

    error = RojoEstado
)

@Composable
fun AvisosCampusTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsquemaColores,
        typography = Typography,
        content = content
    )
}
