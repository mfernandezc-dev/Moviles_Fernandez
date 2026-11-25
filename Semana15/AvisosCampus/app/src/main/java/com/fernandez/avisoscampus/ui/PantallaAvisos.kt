
package com.fernandez.avisoscampus.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.fernandez.avisoscampus.viewmodel.AvisosViewModel
import com.fernandez.avisoscampus.notificacion.Notificador

private val Azul = Color(0xFF17659C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAvisos(vm: AvisosViewModel) {

    val contexto = LocalContext.current

    LaunchedEffect(Unit) {
        vm.eventos.collect { aviso ->
            Notificador.mostrar(contexto, aviso)

            // TODO Paso 8: manejar permiso denegado
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AvisosCampus",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults
                    .topAppBarColors(
                        containerColor = Azul,
                        titleContentColor = Color.White
                    )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            Tarjeta("Permiso de notificaciones") {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text("Estado: ")

                    Text(
                        "Sin pedir",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        // TODO Paso 2
                    }
                ) {
                    Text("Permitir notificaciones")
                }
            }

            Tarjeta("Avisos") {

                Button(
                    onClick = {
                        // TODO Paso 3
                    }
                ) {
                    Text("Mostrar aviso")
                }
            }

            Tarjeta("Recordatorio") {

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Button(
                        onClick = {
                            // TODO Paso 5
                        }
                    ) {
                        Text("Recordar en 10 s")
                    }

                    OutlinedButton(
                        onClick = {},
                        enabled = false
                    ) {
                        Text("Cancelar")
                    }
                }
            }

            Tarjeta("Receptor de anuncios") {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Text("Escuchar el modo avión")

                    Switch(
                        checked = false,
                        onCheckedChange = {
                            // TODO Paso 6
                        }
                    )
                }
            }

            Tarjeta("Historial") {

                Text("Sin avisos todavía")

                // TODO Paso 7: historial de avisos
            }
        }
    }
}

@Composable
private fun Tarjeta(
    titulo: String,
    contenido: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = titulo,
                color = Azul,
                fontWeight = FontWeight.Bold,
                style =
                    MaterialTheme.typography.titleMedium
            )

            contenido()
        }
    }
}
