package com.tecsup.centrotareas.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tecsup.centrotareas.model.PrimosUi
import com.tecsup.centrotareas.model.CargaUi
import com.tecsup.centrotareas.ui.theme.ExitoFondo
import com.tecsup.centrotareas.ui.theme.ExitoTexto
import com.tecsup.centrotareas.viewmodel.TareasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(viewModel: TareasViewModel) {
    val toques by viewModel.toques.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val progreso by viewModel.progreso.collectAsState()
    val corriendo by viewModel.corriendo.collectAsState()
    val primos by viewModel.primos.collectAsState()
    val calculando = primos is PrimosUi.Calculando
    val carga by viewModel.carga.collectAsState()
    val cargando = carga is CargaUi.Cargando
    var limite by rememberSaveable { mutableStateOf("100000") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CentroTareas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Tarjeta("Tarea con progreso") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(onClick = viewModel::tocar) { Text("Tócame") }
                    Text("Toques: $toques")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = viewModel::tareaLarga, enabled = !corriendo) { Text("Iniciar") }
                    OutlinedButton(
                        onClick = viewModel::cancelar,
                        enabled = corriendo,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) { Text("Cancelar") }
                }
                LinearProgressIndicator(progress = { progreso }, modifier = Modifier.fillMaxWidth())
                Text("${(progreso * 100).toInt()} %")
                Text(mensaje)
            }
            Tarjeta("Contar números primos") {
                OutlinedTextField(
                    value = limite,
                    onValueChange = { limite = it },
                    label = { Text("Hasta qué número") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    enabled = !calculando,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { limite.toIntOrNull()?.takeIf { it >= 2 }?.let(viewModel::contar) },
                        enabled = !calculando && (limite.toIntOrNull() ?: 0) >= 2
                    ) { Text("Contar") }
                    OutlinedButton(
                        onClick = viewModel::cancelarPrimos,
                        enabled = calculando,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) { Text("Cancelar") }
                }
                when (val estado = primos) {
                    PrimosUi.Inicial -> Text("Ingresa un número mayor o igual que 2")
                    PrimosUi.Calculando -> Text("Calculando...")
                    PrimosUi.Cancelado -> Text("Cálculo cancelado")
                    is PrimosUi.Listo -> RecuadroExito {
                        Text("Primos encontrados: ${estado.cantidad}")
                        Text("Tiempo: ${estado.milisegundos} ms")
                    }
                }
            }
            Tarjeta("Dos cargas") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = viewModel::cargarUnaPorUna, enabled = !cargando) {
                        Text("Una por una")
                    }
                    OutlinedButton(
                        onClick = viewModel::cargarALaVez,
                        enabled = !cargando,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) { Text("A la vez") }
                }
                when (val estado = carga) {
                    CargaUi.Inicial -> Text("Compara las cargas secuencial y paralela")
                    CargaUi.Cargando -> {
                        CircularProgressIndicator()
                        Text("Cargando...")
                    }
                    is CargaUi.Exito -> RecuadroExito {
                        Text("Modo: ${estado.modo}")
                        Text("Perfil: ${estado.perfil}")
                        Text("Pedidos: ${estado.pedidos}")
                        Text("Tiempo: ${estado.milisegundos} ms")
                    }
                    is CargaUi.Error -> Text(estado.mensaje)
                }
            }
        }
    }
}

@Composable
private fun RecuadroExito(contenido: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = ExitoFondo,
        contentColor = ExitoTexto
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            contenido()
        }
    }
}

@Composable
private fun Tarjeta(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(titulo, style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary)
            contenido()
        }
    }
}
