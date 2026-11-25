package com.fernandez.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fernandez.murocampus.R
import com.fernandez.murocampus.viewmodel.PostsViewModel
import com.fernandez.murocampus.viewmodel.UiState

@Composable
fun PantallaLista(
    vm: PostsViewModel,
    modifier: Modifier = Modifier,
    onPostClick: (Int) -> Unit = {},
    listState: LazyListState = rememberLazyListState()
) {
    val estado by vm.resultados.collectAsStateWithLifecycle()
    val busqueda by vm.busqueda.collectAsStateWithLifecycle()
    val filtro by vm.usuarioFiltro.collectAsStateWithLifecycle()
    val eliminacion by vm.eliminacion.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        if (eliminacion is UiState.Exito) Text(
            "Publicación eliminada", color = Color(0xFF176B36),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        OutlinedTextField(
            value = busqueda,
            onValueChange = vm::buscar,
            label = { Text("Buscar por título") },
            leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) },
            trailingIcon = {
                if (busqueda.isNotEmpty()) IconButton(onClick = { vm.buscar("") }) {
                    Icon(painterResource(R.drawable.ic_close), contentDescription = "Limpiar búsqueda")
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = Color(0xFFFAF9FC),
                unfocusedContainerColor = Color(0xFFFAF9FC)
            ),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                .testTag("busqueda")
        )
        val colores = FilterChipDefaults.filterChipColors(
            containerColor = Color(0xFFEDF2F6),
            labelColor = MaterialTheme.colorScheme.primary,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = Color.White
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth().testTag("filtros"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(key = "todos") {
                FilterChip(
                    selected = filtro == null,
                    onClick = { vm.filtrar(null) },
                    label = { Text("Todos") },
                    modifier = Modifier.testTag("filtro-todos"),
                    shape = RoundedCornerShape(20.dp),
                    colors = colores
                )
            }
            items(items = (1..10).toList(), key = { it }) { userId ->
                FilterChip(
                    selected = filtro == userId,
                    onClick = { vm.filtrar(userId) },
                    label = { Text("Usuario $userId") },
                    modifier = Modifier.testTag("filtro-usuario-$userId"),
                    shape = RoundedCornerShape(20.dp),
                    colors = colores
                )
            }
        }
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (val actual = estado) {
                UiState.Cargando -> Cargando()
                is UiState.Exito -> {
                    if (actual.datos.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(if (busqueda.isBlank()) "Sin publicaciones" else "No se encontraron publicaciones.")
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize().testTag("publicaciones"),
                            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(items = actual.datos, key = { post -> post.id }) { post ->
                                TarjetaPost(post = post, esLocal = vm.esLocal(post.id), onClick = { onPostClick(post.id) })
                            }
                        }
                    }
                }
                is UiState.Error -> ErrorConReintento(actual.mensaje, vm::cargar)
            }
        }
    }
}
