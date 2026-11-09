package com.fernandez.catalogo.ui.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.fernandez.catalogo.ui.UiState
import com.fernandez.catalogo.ui.components.Cargando
import com.fernandez.catalogo.ui.components.ErrorView
import java.util.Locale

@Composable
fun DetalleScreen(
    viewModel: DetalleViewModel,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Detalle de laptop",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (val estadoActual = estado) {
                UiState.Cargando -> Cargando()
                is UiState.Error -> ErrorView(
                    mensaje = estadoActual.mensaje,
                    onReintentar = viewModel::reintentar
                )
                is UiState.Exito -> {
                    val item = estadoActual.datos
                    val imagenUrl = item.imagenUrl?.takeIf { it.isNotBlank() }
                    var cargandoImagen by remember(imagenUrl) { mutableStateOf(true) }
                    var errorImagen by remember(imagenUrl) { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(220.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (imagenUrl != null) {
                                        AsyncImage(
                                            model = imagenUrl,
                                            contentDescription = "Fotografía de ${item.titulo}",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier.fillMaxSize(),
                                            onLoading = {
                                                cargandoImagen = true
                                                errorImagen = false
                                            },
                                            onSuccess = {
                                                cargandoImagen = false
                                                errorImagen = false
                                            },
                                            onError = {
                                                cargandoImagen = false
                                                errorImagen = true
                                            }
                                        )
                                    }
                                    val avisoImagen = when {
                                        imagenUrl == null || errorImagen -> "Imagen no disponible"
                                        cargandoImagen -> "Cargando imagen…"
                                        else -> null
                                    }
                                    avisoImagen?.let { mensaje ->
                                        Text(
                                            text = mensaje,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    text = item.titulo,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = item.subtitulo,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = String.format(
                                        Locale.forLanguageTag("es-PE"),
                                        "S/ %,.2f",
                                        item.precio
                                    ),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Importe de la API. S/ es solo una presentación temporal, sin conversión de moneda.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onVolver,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Volver al catálogo")
        }
    }
}
