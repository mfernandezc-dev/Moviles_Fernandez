package com.fernandez.catalogo.ui.favoritos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fernandez.catalogo.ui.UiState
import com.fernandez.catalogo.ui.components.Cargando
import com.fernandez.catalogo.ui.components.AvisoInformativo
import com.fernandez.catalogo.ui.components.EncabezadoPantalla
import com.fernandez.catalogo.ui.components.EstadoVacio
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
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "encabezado") {
                EncabezadoPantalla("Mis favoritos", "Tus laptops guardadas en este dispositivo.")
            }
            when (val estadoActual = estado) {
                UiState.Cargando -> item(key = "cargando") { Cargando(Modifier.heightIn(min = 240.dp)) }
                is UiState.Error -> item(key = "error") { AvisoInformativo(estadoActual.mensaje, esError = true) }
                is UiState.Exito -> {
                    if (estadoActual.datos.isEmpty()) {
                        item(key = "vacio") {
                            EstadoVacio("Tu selección empieza aquí", "Todavía no tienes favoritos. Marca el corazón de una laptop en el catálogo.")
                        }
                    } else {
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

        OutlinedButton(
            onClick = onVolver,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp).heightIn(min = 52.dp)
        ) {
            Text("Volver al catálogo")
        }
    }
}
