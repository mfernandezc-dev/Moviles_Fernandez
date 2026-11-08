package com.fernandez.catalogo.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fernandez.catalogo.ui.UiState
import com.fernandez.catalogo.ui.components.Cargando
import com.fernandez.catalogo.ui.components.ErrorView

@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    onVerCatalogo: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Panel de administrador",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            when (val actual = estado) {
                UiState.Cargando -> Cargando()
                is UiState.Error -> ErrorView(actual.mensaje, viewModel::reintentar)
                is UiState.Exito -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Datos locales de este dispositivo", style = MaterialTheme.typography.bodyLarge)
                    IndicadorLocal("Productos guardados", actual.datos.productosGuardados)
                    IndicadorLocal("Favoritos locales", actual.datos.favoritosLocales)
                    if (actual.datos.productosGuardados == 0) {
                        Text("Todavía no hay productos guardados en este dispositivo.")
                    }
                }
            }
        }
        Button(
            onClick = onVerCatalogo,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Text("Ver catálogo")
        }
        TextButton(
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
private fun IndicadorLocal(titulo: String, cantidad: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Text(cantidad.toString(), style = MaterialTheme.typography.headlineSmall)
        }
    }
}
