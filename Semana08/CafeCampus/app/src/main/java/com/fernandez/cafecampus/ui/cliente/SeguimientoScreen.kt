
package com.fernandez.cafecampus.ui.cliente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NaranjaSeguimiento = Color(0xFFEA580C)
private val VerdeSeguimiento = Color(0xFF16A34A)
private val GrisSeguimiento = Color(0xFF9CA3AF)
private val BordeSeguimiento = Color(0xFFD1D5DB)
private val NegroSeguimiento = Color(0xFF1F2937)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeguimientoScreen(
    pedidoId: Int,
    estado: String,
    horaRecojo: String,
    total: Double,
    resumen: String,
    onVolver: () -> Unit
) {
    val estados = listOf(
        "RECIBIDO",
        "PREPARANDO",
        "LISTO",
        "ENTREGADO"
    )

    val nombres = listOf(
        "Recibido",
        "Preparando",
        "Listo",
        "Entregado"
    )

    val descripciones = listOf(
        "Tu pedido llegó a la cocina",
        "Están cocinando tu pedido",
        "Puedes pasar a recogerlo",
        "¡Buen provecho!"
    )

    val posicionActual = estados.indexOf(estado.uppercase())
    val cancelado = estado.equals("CANCELADO", ignoreCase = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Seguimiento",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = NegroSeguimiento,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = NegroSeguimiento
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
                .padding(horizontal = 16.dp)
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Spacer(Modifier.height(2.dp))

                // TARJETA NARANJA
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = NaranjaSeguimiento,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Pedido #${
                                    pedidoId.toString().padStart(4, '0')
                                }",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            Text(
                                text = "Recojo a las $horaRecojo",
                                color = Color.White,
                                fontSize = 13.sp
                            )

                            Text(
                                text = if (cancelado)
                                    "Pedido cancelado"
                                else
                                    "Estado: ${
                                        estado.lowercase()
                                            .replaceFirstChar { it.uppercase() }
                                    }",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = "Hora de recojo",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                // LÍNEA DE PROGRESO
                if (cancelado) {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Este pedido fue cancelado.",
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                } else {
                    estados.forEachIndexed { indice, _ ->

                        val completado = indice < posicionActual
                        val actual = indice == posicionActual

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {

                            // CÍRCULOS DE ESTADO
                            Box(
                                modifier = Modifier.size(34.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    completado -> {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(
                                                    VerdeSeguimiento,
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    actual -> {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .background(
                                                    Color(0xFFFFEDD5),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .background(
                                                        NaranjaSeguimiento,
                                                        CircleShape
                                                    )
                                            )
                                        }
                                    }

                                    else -> {
                                        Surface(
                                            modifier = Modifier.size(24.dp),
                                            shape = CircleShape,
                                            color = Color.White,
                                            border = BorderStroke(
                                                2.dp,
                                                BordeSeguimiento
                                            )
                                        ) {}
                                    }
                                }
                            }

                            Column {
                                Text(
                                    text = nombres[indice],
                                    fontWeight = if (actual || completado)
                                        FontWeight.Bold
                                    else
                                        FontWeight.Medium,
                                    color = if (actual || completado)
                                        NegroSeguimiento
                                    else
                                        GrisSeguimiento
                                )

                                Text(
                                    text = descripciones[indice],
                                    color = GrisSeguimiento,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // CONEXIÓN VERTICAL
                        if (indice < estados.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .width(2.dp)
                                    .height(48.dp)
                                    .background(
                                        if (indice < posicionActual)
                                            NaranjaSeguimiento
                                        else
                                            Color(0xFFE5E7EB)
                                    )
                            )
                        }
                    }
                }
            }

            // RESUMEN EN LA PARTE INFERIOR
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BordeSeguimiento)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = resumen,
                        color = GrisSeguimiento,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "Total  S/ %.2f".format(total),
                        color = NaranjaSeguimiento,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
