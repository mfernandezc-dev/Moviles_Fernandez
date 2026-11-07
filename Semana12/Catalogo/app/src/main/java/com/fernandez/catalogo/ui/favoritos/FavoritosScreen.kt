package com.fernandez.catalogo.ui.favoritos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fernandez.catalogo.ui.UiState
import com.fernandez.catalogo.ui.components.Cargando
import com.fernandez.catalogo.ui.lista.TarjetaLaptop

@Composable
fun FavoritosScreen(
    viewModel: FavoritosViewModel,
    onSeleccionarItem: (Int) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Mis favoritos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when (val estadoActual = estado) {
                UiState.Cargando -> Cargando()
                is UiState.Error -> Text(
                    text = estadoActual.mensaje,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                is UiState.Exito -> {
                    if (estadoActual.datos.isEmpty()) {
                        Text(
                            text = "Todavía no tienes favoritos. Marca el corazón de una laptop en el catálogo.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
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
                                    onAlternarFavorito = { viewModel.desmarcarFavorito(item.id) }
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
