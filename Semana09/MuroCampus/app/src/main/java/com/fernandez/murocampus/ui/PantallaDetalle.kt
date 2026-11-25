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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fernandez.murocampus.viewmodel.PostsViewModel
import com.fernandez.murocampus.viewmodel.UiState

@Composable
fun PantallaDetalle(vm: PostsViewModel, idSeleccionado: Int, modifier: Modifier = Modifier, onEditar: () -> Unit = {}) {
    val estado by vm.detalle.collectAsStateWithLifecycle()
    val eliminacion by vm.eliminacion.collectAsStateWithLifecycle()
    var confirmar by rememberSaveable(idSeleccionado) { mutableStateOf(false) }
    val eliminando = eliminacion is UiState.Cargando
    if (confirmar) AlertDialog(
        onDismissRequest = { if (!eliminando) confirmar = false },
        title = { Text("Eliminar publicación") },
        text = { Text("¿Deseas eliminar esta publicación?") },
        dismissButton = { TextButton(enabled = !eliminando, onClick = { confirmar = false }) { Text("Cancelar") } },
        confirmButton = {
            TextButton(enabled = !eliminando, onClick = { confirmar = false; vm.eliminar(idSeleccionado) }) { Text("Eliminar") }
        }
    )

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
                            if (vm.esLocal(datos.post.id)) Text(
                                "Publicación local · solo durante esta sesión",
                                color = MaterialTheme.colorScheme.primary
                            )
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
                item(key = "eliminar") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onEditar, enabled = !eliminando, modifier = Modifier.fillMaxWidth()) {
                            Text("Editar publicación")
                        }
                        Button(
                            onClick = { confirmar = true }, enabled = !eliminando,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E)),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Eliminar publicación") }
                        if (eliminando) CircularProgressIndicator()
                        val fallo = eliminacion as? UiState.Error
                        if (fallo != null) Text(fallo.mensaje, color = MaterialTheme.colorScheme.error)
                    }
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
