package com.fernandez.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fernandez.murocampus.viewmodel.PostsViewModel
import com.fernandez.murocampus.viewmodel.UiState

@Composable
fun PantallaLista(
    vm: PostsViewModel,
    modifier: Modifier = Modifier,
    onPostClick: (Int) -> Unit = {},
    listState: LazyListState = rememberLazyListState()
) {
    val estado by vm.lista.collectAsStateWithLifecycle()

    when (val actual = estado) {
        UiState.Cargando -> Cargando(modifier)
        is UiState.Exito -> {
            if (actual.datos.isEmpty()) {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay publicaciones.")
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = actual.datos, key = { post -> post.id }) { post ->
                        TarjetaPost(post = post, onClick = { onPostClick(post.id) })
                    }
                }
            }
        }
        is UiState.Error -> ErrorConReintento(actual.mensaje, vm::cargar, modifier)
    }
}
