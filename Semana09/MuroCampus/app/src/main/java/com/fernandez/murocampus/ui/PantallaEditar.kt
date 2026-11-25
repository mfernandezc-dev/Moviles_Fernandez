package com.fernandez.murocampus.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fernandez.murocampus.viewmodel.PostsViewModel
import com.fernandez.murocampus.viewmodel.UiState

@Composable
fun PantallaEditar(vm: PostsViewModel, id: Int, modifier: Modifier = Modifier, onVolver: () -> Unit = {}) {
    val detalle by vm.detalle.collectAsStateWithLifecycle()
    val estado by vm.edicion.collectAsStateWithLifecycle()
    val post = (detalle as? UiState.Exito)?.datos?.post ?: return
    var titulo by rememberSaveable(id) { mutableStateOf(post.title) }
    val pendiente = estado is UiState.Cargando
    Column(
        modifier = modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Editar publicación", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        OutlinedTextField(
            value = titulo, onValueChange = { titulo = it }, label = { Text("Título") },
            enabled = !pendiente, isError = titulo.isBlank(), modifier = Modifier.fillMaxWidth()
        )
        Text("Contenido original", style = MaterialTheme.typography.titleMedium)
        Text(post.body, color = Color(0xFF525C66))
        Button(
            onClick = { vm.editar(id, titulo) },
            enabled = titulo.isNotBlank() && titulo.trim() != post.title && !pendiente,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar cambios") }
        if (pendiente) CircularProgressIndicator()
        when (val actual = estado) {
            is UiState.Error -> Text(actual.mensaje, color = MaterialTheme.colorScheme.error)
            is UiState.Exito -> Text("Publicación actualizada", color = Color(0xFF176B36))
            else -> Unit
        }
        TextButton(onClick = onVolver) { Text("Volver al detalle") }
    }
}
