package com.fernandez.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fernandez.murocampus.viewmodel.PostsViewModel
import com.fernandez.murocampus.viewmodel.UiState

@Composable
fun PantallaDetalle(vm: PostsViewModel, idSeleccionado: Int, modifier: Modifier = Modifier) {
    val estado by vm.detalle.collectAsStateWithLifecycle()

    when (val actual = estado) {
        UiState.Cargando -> Cargando(
            modifier = modifier,
            mensaje = "Cargando publicación y comentarios…",
            descripcion = "Cargando detalle"
        )
        is UiState.Error -> ErrorConReintento(actual.mensaje, { vm.abrir(idSeleccionado) }, modifier)
        is UiState.Exito -> {
            val datos = actual.datos
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "publicacion") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                datos.post.title,
                                color = Color(0xFF20252B),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                datos.post.body,
                                color = Color(0xFF525C66),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                item(key = "encabezado") {
                    Text(
                        "Comentarios (${datos.comentarios.size})",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(items = datos.comentarios, key = { comentario -> comentario.id }) { comentario ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF2F6))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                comentario.email,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                comentario.body,
                                color = Color(0xFF525C66),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
