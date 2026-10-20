
package com.fernandez.cafecampus.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val IndigoAjustes = Color(0xFF4F46E5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(
    modoOscuro: Boolean,
    notificaciones: Boolean,
    recibirPedidos: Boolean,
    onModoOscuro: (Boolean) -> Unit,
    onNotificaciones: (Boolean) -> Unit,
    onRecibirPedidos: (Boolean) -> Unit,
    onCambiarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ajustes",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    Spacer(Modifier.width(48.dp))
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE0E7FF),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = IndigoAjustes
                            )
                        }
                    }

                    Column {
                        Text(
                            "Administrador",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "admin@cafe.test",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                        Text(
                            "Perfil activo",
                            color = IndigoAjustes,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCambiarPerfil() },
                shape = RoundedCornerShape(12.dp),
                color = IndigoAjustes
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = Color.White
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Cambiar de perfil",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Modo práctica: ir a vista Estudiante",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }

                    Text("›", color = Color.White, fontSize = 24.sp)
                }
            }

            Text(
                "Preferencias",
                fontWeight = FontWeight.Bold
            )

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    AjusteInterruptor(
                        titulo = "Modo oscuro",
                        descripcion = "Cambia el tema de la app",
                        activado = modoOscuro,
                        onCambio = onModoOscuro
                    )

                    HorizontalDivider()

                    AjusteInterruptor(
                        titulo = "Notificaciones",
                        descripcion = "Avisar nuevos pedidos",
                        activado = notificaciones,
                        onCambio = onNotificaciones
                    )

                    HorizontalDivider()

                    AjusteInterruptor(
                        titulo = "Recibir pedidos",
                        descripcion = "Abrir o cerrar la cafetería",
                        activado = recibirPedidos,
                        onCambio = onRecibirPedidos
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFEEF2FF),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        "Se guardan en el dispositivo",
                        color = Color(0xFF6B7280),
                        fontSize = 12.sp
                    )
                    Text(
                        "con DataStore",
                        color = IndigoAjustes,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onCerrarSesion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                border = BorderStroke(
                    1.dp,
                    Color(0xFFDC2626)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFDC2626)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}

@Composable
private fun AjusteInterruptor(
    titulo: String,
    descripcion: String,
    activado: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                titulo,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                descripcion,
                color = Color(0xFF6B7280),
                fontSize = 12.sp
            )
        }

        Switch(
            checked = activado,
            onCheckedChange = onCambio
        )
    }
}
