package com.fernandez.cafecampus.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernandez.cafecampus.data.local.relation.PedidoConDetalle

private val Indigo = Color(0xFF4F46E5)
private val GrisBorde = Color(0xFFE5E7EB)
private val Negro = Color(0xFF1F2937)

@Composable
fun PanelScreen(
    nuevos: Int,
    preparando: Int,
    listos: Int,
    ventasHoy: Double,
    pedidosNuevos: List<PedidoConDetalle>,
    onPreparar: (PedidoConDetalle) -> Unit,
    onVerPedidos: () -> Unit,
    cantidadPedidosHoy: Int,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Panel de control",
                        color = Negro,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Cafetería · Hoy",
                        color = Color(0xFF6B7280),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                IconButton(onClick = { /* pendiente */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Negro
                    )
                }
            }
        }


        // CONTADORES
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ContadorAdmin(
                    titulo = "Nuevos",
                    cantidad = nuevos,
                    fondo = Color(0xFFDBEAFE),
                    texto = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f)
                )

                ContadorAdmin(
                    titulo = "Preparando",
                    cantidad = preparando,
                    fondo = Color(0xFFFEF3C7),
                    texto = Color(0xFFD97706),
                    modifier = Modifier.weight(1f)
                )

                ContadorAdmin(
                    titulo = "Listos",
                    cantidad = listos,
                    fondo = Color(0xFFDCFCE7),
                    texto = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // VENTAS DEL DÍA
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Indigo
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Ventas del día",
                            color = Color.White.copy(alpha = 0.85f)
                        )

                        Text(
                            text = "S/ %.2f".format(ventasHoy),
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "$cantidadPedidosHoy pedidos",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        item {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, GrisBorde),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Pedidos por hora",
                        color = Negro,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        BarraHora("8h", 3, 0.35f, Color(0xFFC7D2FE))
                        BarraHora("9h", 6, 0.60f, Color(0xFFA5B4FC))
                        BarraHora("10h", 9, 1.00f, Indigo)
                        BarraHora("11h", 5, 0.55f, Color(0xFFC7D2FE))
                        BarraHora("12h", 7, 0.75f, Color(0xFFA5B4FC))
                    }
                }
            }
        }

        // ENCABEZADO DE PEDIDOS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nuevos pedidos",
                    fontSize = 18.sp,
                    color = Negro,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onVerPedidos) {
                    Text(
                        text = "Ver todos",
                        color = Indigo
                    )

                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Indigo,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        if (pedidosNuevos.isEmpty()) {
            item {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, GrisBorde)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = Indigo
                        )

                        Text(
                            text = "No hay pedidos nuevos",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(
                pedidosNuevos.take(5),
                key = { it.pedido.id }
            ) { registro ->

                val pedido = registro.pedido

                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GrisBorde),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Pedido #${
                                    pedido.id.toString().padStart(4, '0')
                                }",
                                color = Negro,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = registro.cliente.nombre,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                text = "Recojo: ${pedido.horaRecojo}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme
                                    .onSurfaceVariant
                            )

                            Text(
                                text = "S/ %.2f".format(pedido.total),
                                color = Indigo,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { onPreparar(registro) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Indigo
                            )
                        ) {
                            Text("Preparar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContadorAdmin(
    titulo: String,
    cantidad: Int,
    fondo: Color,
    texto: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = fondo,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 14.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "$cantidad",
                color = texto,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = titulo,
                color = texto,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun BarraHora(
    hora: String,
    valor: Int,
    proporcion: Float,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = valor.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = Negro
        )

        Box(
            modifier = Modifier
                .width(28.dp)
                .height((70 * proporcion).dp)
                .background(
                    color = color,
                    shape = RoundedCornerShape(6.dp)
                )
        )

        Text(
            text = hora,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF6B7280)
        )
    }
}

