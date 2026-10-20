
package com.fernandez.cafecampus.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernandez.cafecampus.data.local.relation.PedidoConDetalle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val IndigoDetalle = Color(0xFF4F46E5)
private val GrisDetalle = Color(0xFF6B7280)
private val BordeDetalle = Color(0xFFE5E7EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePedidoAdminScreen(
    registro: PedidoConDetalle,
    onVolver: () -> Unit,
    onAvanzar: () -> Unit,
    onCancelar: () -> Unit
) {
    val pedido = registro.pedido

    val estados = listOf(
        "RECIBIDO", "PREPARANDO", "LISTO", "ENTREGADO"
    )

    val posicion = estados.indexOf(pedido.estado)
    val finalizado = pedido.estado == "ENTREGADO" ||
            pedido.estado == "CANCELADO"

    val siguiente = when (pedido.estado) {
        "RECIBIDO" -> "Preparando"
        "PREPARANDO" -> "Listo"
        "LISTO" -> "Entregado"
        else -> null
    }

    val fecha = SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale.getDefault()
    ).format(Date(pedido.fecha))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle del pedido",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // INFORMACIÓN PRINCIPAL
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BordeDetalle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Pedido #${pedido.id.toString().padStart(4, '0')}",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "$fecha · Recojo ${pedido.horaRecojo}",
                            color = GrisDetalle,
                            fontSize = 12.sp
                        )
                    }

                    Surface(
                        color = when (pedido.estado) {
                            "RECIBIDO" -> Color(0xFFDBEAFE)
                            "PREPARANDO" -> Color(0xFFFEF3C7)
                            "LISTO" -> Color(0xFFDCFCE7)
                            "CANCELADO" -> Color(0xFFFEE2E2)
                            else -> Color(0xFFE5E7EB)
                        },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            pedido.estado.lowercase()
                                .replaceFirstChar { it.uppercase() },
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // CLIENTE Y PRODUCTOS
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BordeDetalle)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Cliente",
                        color = GrisDetalle,
                        fontSize = 12.sp
                    )
                    Text(
                        "${registro.cliente.nombre} · Estudiante",
                        fontWeight = FontWeight.SemiBold
                    )

                    HorizontalDivider()

                    registro.detalles.forEach { detalle ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                detalle.nombreProducto,
                                modifier = Modifier.weight(1f)
                            )
                            Text("x${detalle.cantidad}")
                            Spacer(Modifier.width(16.dp))
                            Text(
                                "S/ %.2f".format(
                                    detalle.cantidad * detalle.precioUnitario
                                ),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", fontWeight = FontWeight.Bold)
                        Text(
                            "S/ %.2f".format(pedido.total),
                            color = IndigoDetalle,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // NOTA DEL CLIENTE
            if (pedido.nota.isNotBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            "Nota del cliente",
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            pedido.nota,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            Text(
                "Cambiar estado",
                fontWeight = FontWeight.Bold
            )



            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                estados.forEachIndexed { indice, estado ->

                    val seleccionado = indice == posicion

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = if (seleccionado)
                            Color(0xFF2563EB)
                        else
                            Color.White,
                        border = BorderStroke(
                            1.dp,
                            if (seleccionado)
                                Color(0xFF2563EB)
                            else
                                Color(0xFFE5E7EB)
                        )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = estado.lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                fontSize = 10.sp,
                                fontWeight = if (seleccionado)
                                    FontWeight.SemiBold
                                else
                                    FontWeight.Normal,
                                color = if (seleccionado)
                                    Color.White
                                else
                                    Color(0xFF6B7280),
                                maxLines = 1
                            )
                        }
                    }
                }
            }



            if (!finalizado && siguiente != null) {
                Button(
                    onClick = onAvanzar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IndigoDetalle
                    )
                ) {
                    Text("Pasar a $siguiente")
                }

                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        1.dp,
                        Color(0xFFDC2626)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFDC2626)
                    )
                ) {
                    Text("Cancelar pedido")
                }
            }
        }
    }
}