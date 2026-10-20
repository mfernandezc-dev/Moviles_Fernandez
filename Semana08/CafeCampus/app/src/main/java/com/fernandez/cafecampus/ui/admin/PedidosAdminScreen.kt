
package com.fernandez.cafecampus.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernandez.cafecampus.data.local.relation.PedidoConDetalle
import com.fernandez.cafecampus.model.EstadoPedido

private val IndigoPedidos = Color(0xFF4F46E5)
private val NegroPedidos = Color(0xFF1F2937)
private val GrisPedidos = Color(0xFF9CA3AF)
private val BordePedidos = Color(0xFFE5E7EB)

@Composable
private fun EstadoAdminChip(estado: String) {
    val (fondo, texto) = when (estado) {
        "RECIBIDO" -> Color(0xFFDBEAFE) to Color(0xFF2563EB)
        "PREPARANDO" -> Color(0xFFFEF3C7) to Color(0xFFD97706)
        "LISTO" -> Color(0xFFDCFCE7) to Color(0xFF16A34A)
        "ENTREGADO" -> Color(0xFFE5E7EB) to Color(0xFF6B7280)
        "CANCELADO" -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
        else -> Color(0xFFE5E7EB) to NegroPedidos
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = fondo
    ) {
        Text(
            text = estado.lowercase()
                .replaceFirstChar { it.uppercase() },
            color = texto,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 5.dp
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosAdminScreen(
    estadoSeleccionado: EstadoPedido,
    pedidos: List<PedidoConDetalle>,
    onEstado: (EstadoPedido) -> Unit,
    onAvanzar: (PedidoConDetalle) -> Unit,
    onCancelar: (PedidoConDetalle) -> Unit,
    onDetalle: (Int) -> Unit,
    onVolver: () -> Unit
) {
    val estados = listOf(
        EstadoPedido.RECIBIDO,
        EstadoPedido.PREPARANDO,
        EstadoPedido.LISTO,
        EstadoPedido.ENTREGADO,
        EstadoPedido.CANCELADO
    )

    val nombres = mapOf(
        EstadoPedido.RECIBIDO to "Nuevos",
        EstadoPedido.PREPARANDO to "Preparando",
        EstadoPedido.LISTO to "Listos",
        EstadoPedido.ENTREGADO to "Entregados",
        EstadoPedido.CANCELADO to "Cancelados"
    )

    val siguientes = mapOf(
        EstadoPedido.RECIBIDO to "Pasar a Preparando",
        EstadoPedido.PREPARANDO to "Pasar a Listo",
        EstadoPedido.LISTO to "Pasar a Entregado"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pedidos",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = NegroPedidos
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
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                estados.forEach { estado ->
                    FilterChip(
                        selected = estado == estadoSeleccionado,
                        onClick = { onEstado(estado) },
                        label = {
                            Text(
                                nombres[estado] ?: estado.name,
                                fontSize = 12.sp
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IndigoPedidos,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (pedidos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No hay pedidos en este estado",
                        color = GrisPedidos
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(pedidos, key = { it.pedido.id }) { registro ->

                        val pedido = registro.pedido
                        val estadoActual = EstadoPedido.valueOf(
                            pedido.estado
                        )

                        val siguiente = siguientes[estadoActual]

                        val resumen = registro.detalles
                            .joinToString(" · ") { detalle ->
                                "${detalle.cantidad} x ${detalle.nombreProducto}"
                            }

                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = Color.White
                            ),
                            border = BorderStroke(1.dp, BordePedidos)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(9.dp)
                            ) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFE0E7FF),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.PersonOutline,
                                                contentDescription = null,
                                                tint = IndigoPedidos,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement =
                                            Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            text = "#${
                                                pedido.id.toString()
                                                    .padStart(4, '0')
                                            } ${registro.cliente.nombre}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = NegroPedidos
                                        )

                                        Text(
                                            text = resumen,
                                            color = GrisPedidos,
                                            fontSize = 12.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    EstadoAdminChip(pedido.estado)
                                }

                                val informacion = buildString {
                                    append("Recojo ${pedido.horaRecojo}")
                                    append("  ·  S/ %.2f".format(pedido.total))
                                    if (pedido.nota.isNotBlank()) {
                                        append("  ·  Nota: ${pedido.nota}")
                                    }
                                }

                                Text(
                                    text = informacion,
                                    color = GrisPedidos,
                                    fontSize = 11.sp,
                                    maxLines = 2
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement =
                                        Arrangement.spacedBy(8.dp)
                                ) {
                                    if (siguiente != null) {
                                        Button(
                                            onClick = {
                                                onAvanzar(registro)
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(20.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = IndigoPedidos
                                            ),
                                            contentPadding = PaddingValues(
                                                horizontal = 8.dp
                                            )
                                        ) {
                                            Text(
                                                siguiente,
                                                fontSize = 11.sp,
                                                maxLines = 1
                                            )
                                        }
                                    }

                                    if (estadoActual != EstadoPedido.ENTREGADO &&
                                        estadoActual != EstadoPedido.CANCELADO
                                    ) {
                                        Button(
                                            onClick = {
                                                onCancelar(registro)
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(20.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFFEE2E2),
                                                contentColor = Color(0xFFDC2626)
                                            )
                                        ) {
                                            Text("Cancelar", fontSize = 11.sp)
                                        }
                                    }
                                }

                                TextButton(
                                    onClick = { onDetalle(pedido.id) },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        "Ver detalle",
                                        color = IndigoPedidos,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}