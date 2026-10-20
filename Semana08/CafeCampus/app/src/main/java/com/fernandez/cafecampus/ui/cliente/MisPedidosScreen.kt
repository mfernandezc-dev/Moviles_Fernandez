
package com.fernandez.cafecampus.ui.cliente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernandez.cafecampus.data.local.entity.Producto
import com.fernandez.cafecampus.data.local.relation.PedidoConDetalle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val Naranja = Color(0xFFEA580C)
private val Negro = Color(0xFF1F2937)
private val Gris = Color(0xFF9CA3AF)
private val Borde = Color(0xFFE5E7EB)

private fun fechaPedido(fecha: Long): String {
    val hoy = Calendar.getInstance()

    val pedido = Calendar.getInstance().apply {
        timeInMillis = fecha
    }

    val hora = SimpleDateFormat(
        "HH:mm",
        Locale.getDefault()
    ).format(Date(fecha))

    fun mismoDia(a: Calendar, b: Calendar): Boolean {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
    }

    if (mismoDia(hoy, pedido)) {
        return "Hoy $hora"
    }

    val ayer = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
    }

    if (mismoDia(ayer, pedido)) {
        return "Ayer $hora"
    }

    val fechaFormateada = SimpleDateFormat(
        "dd/MM/yyyy",
        Locale.getDefault()
    ).format(Date(fecha))

    return "$fechaFormateada $hora"
}

@Composable
private fun EstadoPedidoEtiqueta(estado: String) {

    val (fondo, texto) = when (estado.uppercase()) {
        "RECIBIDO" ->
            Color(0xFFDBEAFE) to Color(0xFF1D4ED8)

        "PREPARANDO" ->
            Color(0xFFFEF3C7) to Color(0xFFD97706)

        "LISTO" ->
            Color(0xFFDCFCE7) to Color(0xFF16A34A)

        "ENTREGADO" ->
            Color(0xFFE5E7EB) to Color(0xFF6B7280)

        "CANCELADO" ->
            Color(0xFFFEE2E2) to Color(0xFFDC2626)

        else ->
            Color(0xFFE5E7EB) to Color(0xFF374151)
    }

    val nombre = estado.lowercase()
        .replaceFirstChar { it.uppercase() }

    Surface(
        color = fondo,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = nombre,
            color = texto,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 5.dp
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPedidosScreen(
    pedidos: List<PedidoConDetalle>,
    productos: List<Producto>,
    onPedido: (Int) -> Unit,
    onVolver: () -> Unit
) {
    var pestana by remember { mutableIntStateOf(0) }

    val estadosActivos = listOf(
        "RECIBIDO",
        "PREPARANDO",
        "LISTO"
    )

    val pedidosFiltrados = pedidos
        .filter {
            val activo = it.pedido.estado.uppercase() in estadosActivos

            if (pestana == 0) activo else !activo
        }
        .sortedByDescending { it.pedido.fecha }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis pedidos",
                        color = Negro,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Negro
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

            // SELECTOR ACTIVOS / HISTORIAL
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF3EDE7),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    listOf("Activos", "Historial")
                        .forEachIndexed { indice, nombre ->

                            val seleccionado = pestana == indice

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clickable {
                                        pestana = indice
                                    },
                                color = if (seleccionado)
                                    Color.White
                                else
                                    Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = nombre,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (seleccionado)
                                            Naranja
                                        else
                                            Color(0xFF6B7280)
                                    )
                                }
                            }
                        }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (pedidosFiltrados.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (pestana == 0)
                            "No tienes pedidos activos"
                        else
                            "No tienes pedidos en el historial",
                        color = Gris
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        pedidosFiltrados,
                        key = { it.pedido.id }
                    ) { registro ->

                        val pedido = registro.pedido

                        // RESUMEN REAL DE PRODUCTOS
                        val resumen = registro.detalles
                            .mapIndexed { indice, detalle ->

                                val nombreProducto = productos
                                    .find {
                                        it.id == detalle.productoId
                                    }?.nombre ?: "Producto"

                                "$nombreProducto x${detalle.cantidad}"
                            }

                        val descripcion = when {
                            resumen.isEmpty() ->
                                "Sin productos"

                            resumen.size == 1 ->
                                resumen.first()

                            else ->
                                "${resumen.first()} + ${resumen.size - 1} más"
                        }

                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onPedido(pedido.id)
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = Color.White
                            ),
                            border = BorderStroke(1.dp, Borde)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(7.dp)
                            ) {

                                // PEDIDO Y FLECHA
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement =
                                        Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Pedido #${
                                            pedido.id.toString()
                                                .padStart(4, '0')
                                        }",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Negro
                                    )

                                    Icon(
                                        imageVector = Icons.AutoMirrored
                                            .Filled.KeyboardArrowRight,
                                        contentDescription = "Ver pedido",
                                        tint = Gris
                                    )
                                }

                                // PRODUCTOS
                                Text(
                                    text = descripcion,
                                    color = Color(0xFF6B7280),
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                // FECHA
                                Text(
                                    text = fechaPedido(pedido.fecha),
                                    color = Gris,
                                    fontSize = 12.sp
                                )

                                // ESTADO Y TOTAL
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement =
                                        Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    EstadoPedidoEtiqueta(
                                        estado = pedido.estado
                                    )

                                    Text(
                                        text = "S/ %.2f".format(pedido.total),
                                        color = Naranja,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
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
