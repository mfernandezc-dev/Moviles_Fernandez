package com.fernandez.murocampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fernandez.murocampus.viewmodel.PostsViewModel
import com.fernandez.murocampus.viewmodel.UiState

@Composable
fun PantallaCrear(vm: PostsViewModel, modifier: Modifier = Modifier) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var contenido by rememberSaveable { mutableStateOf("") }
    val estado by vm.creacion.collectAsStateWithLifecycle()
    val publicando = estado == UiState.Cargando
    val foco = LocalFocusManager.current

    Column(
        modifier = modifier.fillMaxSize().imePadding()
            .verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            singleLine = true,
            enabled = !publicando,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("titulo")
        )
        OutlinedTextField(
            value = contenido,
            onValueChange = { contenido = it },
            label = { Text("Contenido") },
            minLines = 5,
            enabled = !publicando,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("contenido")
        )
        Button(
            onClick = {
                foco.clearFocus()
                vm.publicar(titulo, contenido)
            },
            enabled = titulo.isNotBlank() && contenido.isNotBlank() && !publicando,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().testTag("publicar")
        ) {
            Text("Publicar")
        }
        when (val actual = estado) {
            null -> Unit
            UiState.Cargando -> Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp).semantics { contentDescription = "Publicando publicación" },
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Publicando…")
            }
            is UiState.Exito -> Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE9F7ED),
                border = BorderStroke(1.dp, Color(0xFF14823D))
            ) {
                Text(
                    "Publicado con id ${actual.datos.id}",
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF176B37)
                )
            }
            is UiState.Error -> Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFDE2E2)
            ) {
                Text(actual.mensaje, modifier = Modifier.padding(16.dp), color = Color(0xFF9E3232))
            }
        }
    }
}
