
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
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.fernandez.avisoscampus.model.PermisoUi

private val Azul = Color(0xFF17659C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAvisos(vm: AvisosViewModel) {

    val contexto = LocalContext.current

    val permiso by vm.permiso.collectAsState()

    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        vm.permisoRespondido(concedido)
    }

    LaunchedEffect(Unit) {
        if (Notificador.tienePermiso(contexto)) {
            vm.permisoRespondido(true)
        }
    }

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

                val textoEstado = when (permiso) {
                    PermisoUi.SinPedir -> "Sin pedir"
                    PermisoUi.Concedido -> "Permitido"
                    PermisoUi.Denegado -> "Denegado"
                }

                val colorEstado = when (permiso) {
                    PermisoUi.SinPedir -> Color.Gray
                    PermisoUi.Concedido -> Color(0xFF2E7D32)
                    PermisoUi.Denegado -> Color(0xFFC62828)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Estado: ")

                    Text(
                        text = textoEstado,
                        color = colorEstado,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        if (
                            Build.VERSION.SDK_INT >=
                            Build.VERSION_CODES.TIRAMISU
                        ) {
                            lanzador.launch(
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        } else {
                            vm.permisoRespondido(true)
                        }
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
