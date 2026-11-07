package com.fernandez.catalogo.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fernandez.catalogo.domain.model.Item
import com.fernandez.catalogo.ui.UiState
import com.fernandez.catalogo.ui.components.Cargando
import com.fernandez.catalogo.ui.components.ErrorView
import java.util.Locale

@Composable
fun ListaScreen(
    viewModel: ListaViewModel,
    onSeleccionarItem: (Int) -> Unit,
    onVerFavoritos: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val actualizacionFallida by viewModel.actualizacionFallida.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Catálogo de laptops",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            TextButton(onClick = onVerFavoritos) {
                Text("Mis favoritos")
            }
            TextButton(onClick = onCerrarSesion) {
                Text("Cerrar sesión")
            }
        }

        when (val estadoActual = estado) {
            UiState.Cargando -> Cargando(modifier = Modifier.weight(1f))
            is UiState.Exito -> {
                if (estadoActual.datos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay productos disponibles.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    if (actualizacionFallida) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "No se pudo actualizar. Mostrando datos guardados.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = viewModel::cargarItems) {
                                Text("Reintentar")
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(estadoActual.datos, key = { it.id }) { item ->
                            TarjetaLaptop(
                                item = item,
                                onSeleccionar = { onSeleccionarItem(item.id) },
                                onAlternarFavorito = { viewModel.alternarFavorito(item.id) }
                            )
                        }
                    }
                }
            }
            is UiState.Error -> ErrorView(
                mensaje = estadoActual.mensaje,
                onReintentar = viewModel::cargarItems,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
internal fun TarjetaLaptop(
    item: Item,
    onSeleccionar: () -> Unit,
    onAlternarFavorito: () -> Unit
) {
    Card(
        onClick = onSeleccionar,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.titulo,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                IconToggleButton(
                    checked = item.esFavorito,
                    onCheckedChange = { onAlternarFavorito() }
                ) {
                    Icon(
                        imageVector = if (item.esFavorito) CorazonActivo else CorazonVacio,
                        contentDescription = if (item.esFavorito) {
                            "Quitar ${item.titulo} de favoritos"
                        } else {
                            "Agregar ${item.titulo} a favoritos"
                        },
                        tint = if (item.esFavorito) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            Text(
                text = item.subtitulo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = String.format(
                    Locale.forLanguageTag("es-PE"),
                    "S/ %,.2f",
                    item.precio
                ),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private val CorazonActivo = crearCorazon(activo = true)
private val CorazonVacio = crearCorazon(activo = false)

private fun crearCorazon(activo: Boolean): ImageVector = ImageVector.Builder(
    name = if (activo) "CorazonActivo" else "CorazonVacio",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = if (activo) SolidColor(Color.Black) else null,
        stroke = if (activo) null else SolidColor(Color.Black),
        strokeLineWidth = 2f
    ) {
        moveTo(12f, 21f)
        lineTo(10.55f, 19.68f)
        curveTo(5.4f, 15.01f, 2f, 11.92f, 2f, 8.5f)
        curveTo(2f, 5.41f, 4.42f, 3f, 7.5f, 3f)
        curveTo(9.24f, 3f, 10.91f, 3.81f, 12f, 5.09f)
        curveTo(13.09f, 3.81f, 14.76f, 3f, 16.5f, 3f)
        curveTo(19.58f, 3f, 22f, 5.41f, 22f, 8.5f)
        curveTo(22f, 11.92f, 18.6f, 15.01f, 13.45f, 19.68f)
        lineTo(12f, 21f)
        close()
    }
}.build()
