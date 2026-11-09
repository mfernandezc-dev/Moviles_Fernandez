package com.fernandez.catalogo.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fernandez.catalogo.ui.UiState
import com.fernandez.catalogo.ui.components.Cargando
import com.fernandez.catalogo.ui.components.ErrorView
import com.fernandez.catalogo.ui.components.EncabezadoPantalla

@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    onVerCatalogo: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { EncabezadoPantalla("Panel de administrador", "Resumen local del catálogo y tus favoritos.") }
            when (val actual = estado) {
                UiState.Cargando -> item { Cargando(Modifier.heightIn(min = 240.dp)) }
                is UiState.Error -> item { ErrorView(actual.mensaje, viewModel::reintentar, Modifier.heightIn(min = 240.dp)) }
                is UiState.Exito -> item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Datos locales de este dispositivo", style = MaterialTheme.typography.titleMedium)
                        if (LocalConfiguration.current.screenWidthDp >= 600) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                IndicadorLocal("Productos guardados", actual.datos.productosGuardados, Modifier.weight(1f))
                                IndicadorLocal("Favoritos locales", actual.datos.favoritosLocales, Modifier.weight(1f))
                            }
                        } else {
                            IndicadorLocal("Productos guardados", actual.datos.productosGuardados)
                            IndicadorLocal("Favoritos locales", actual.datos.favoritosLocales)
                        }
                        if (actual.datos.productosGuardados == 0) {
                            Text("Todavía no hay productos guardados en este dispositivo.")
                        }
                    }
                }
            }
        }
        Button(
            onClick = onVerCatalogo,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).heightIn(min = 52.dp)
        ) {
            Text("Ver catálogo")
        }
        TextButton(
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
private fun IndicadorLocal(titulo: String, cantidad: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(cantidad.toString(), style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
            Text("En este dispositivo", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
