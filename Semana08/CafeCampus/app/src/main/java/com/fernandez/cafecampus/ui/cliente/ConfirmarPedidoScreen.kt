package com.fernandez.cafecampus.ui.cliente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernandez.cafecampus.model.ItemCarrito
import com.fernandez.cafecampus.ui.components.ProductoIcono

private val Naranja = Color(0xFFEA580C)
private val NaranjaClaro = Color(0xFFFFF1E8)
private val GrisBorde = Color(0xFFE5E7EB)
private val GrisTexto = Color(0xFF6B7280)
private val Negro = Color(0xFF111827)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmarPedidoScreen(
    items: List<ItemCarrito>,
    confirmando: Boolean,
    recibirPedidos: Boolean,
    error: String?,
    onConfirmar: (String, String, String) -> Unit,
    onVolver: () -> Unit
) {
    var horaRecojo by rememberSaveable { mutableStateOf("10:30") }
    var pago by rememberSaveable { mutableStateOf("CAJA") }

    val horas = listOf("Ahora", "10:30", "11:00", "11:30")
    val subtotal = items.sumOf { it.producto.precio * it.cantidad }
    val servicio = 0.0
    val total = subtotal + servicio

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Confirmar pedido",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Negro,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Negro
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 6.dp,
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            onConfirmar(horaRecojo, pago, "")
                        },
                        enabled = items.isNotEmpty() && !confirmando && recibirPedidos,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Naranja
                        )
                    ) {
                        Text(
                            text = when {
                                !recibirPedidos -> "Cafetería cerrada"
                                confirmando -> "Confirmando..."
                                else -> "Confirmar pedido"
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = onVolver,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, Naranja),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Naranja
                        )
                    ) {
                        Text(
                            text = "Seguir pidiendo",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            if (!recibirPedidos) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEE2E2)
                ) {
                    Text(
                        text = "La cafetería está cerrada. No se aceptan pedidos por el momento.",
                        color = Color(0xFFB91C1C),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            // CONTENEDOR DE PRODUCTOS
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, GrisBorde)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProductoIcono(
                                icono = item.producto.icono,
                                size = 54.dp
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = item.producto.nombre,
                                    color = Negro,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = "x${item.cantidad}",
                                    color = GrisTexto,
                                    fontSize = 13.sp
                                )
                            }

                            Text(
                                text = "S/ %.2f".format(item.producto.precio * item.cantidad),
                                color = Negro,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (index < items.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = GrisBorde
                            )
                        }
                    }
                }
            }

            // HORA DE RECOJO
            Text(
                text = "Hora de recojo",
                color = Negro,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                horas.forEach { hora ->
                    val seleccionado = horaRecojo == hora

                    FilterChip(
                        selected = seleccionado,
                        onClick = { horaRecojo = hora },
                        label = {
                            Text(
                                text = hora,
                                color = if (seleccionado) Color.White else GrisTexto
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Naranja,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = GrisTexto
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = seleccionado,
                            borderColor = if (seleccionado) Naranja else GrisBorde,
                            selectedBorderColor = Naranja
                        )
                    )
                }
            }

            // FORMA DE PAGO
            Text(
                text = "Forma de pago",
                color = Negro,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val pagoCaja = pago == "CAJA"
                val pagoYape = pago == "YAPE"

                OutlinedButton(
                    onClick = { pago = "CAJA" },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.5.dp,
                        if (pagoCaja) Naranja else GrisBorde
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (pagoCaja) NaranjaClaro else Color.White,
                        contentColor = if (pagoCaja) Naranja else GrisTexto
                    )
                ) {
                    Text(
                        text = "Pagar en caja",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = { pago = "YAPE" },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.5.dp,
                        if (pagoYape) Naranja else GrisBorde
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (pagoYape) NaranjaClaro else Color.White,
                        contentColor = if (pagoYape) Naranja else GrisTexto
                    )
                ) {
                    Text(
                        text = "Yape / Plin",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // RESUMEN DE COSTOS
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, GrisBorde)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", color = GrisTexto)
                        Text(
                            "S/ %.2f".format(subtotal),
                            color = Negro,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Servicio", color = GrisTexto)
                        Text(
                            "S/ %.2f".format(servicio),
                            color = Negro,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    HorizontalDivider(color = GrisBorde)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Total",
                            color = Negro,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "S/ %.2f".format(total),
                            color = Naranja,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
