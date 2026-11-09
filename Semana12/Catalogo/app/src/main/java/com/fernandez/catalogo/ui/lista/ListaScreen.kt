package com.fernandez.catalogo.ui.lista

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.fernandez.catalogo.domain.model.Item
import com.fernandez.catalogo.ui.UiState
import com.fernandez.catalogo.ui.components.Cargando
import com.fernandez.catalogo.ui.components.ErrorView
import com.fernandez.catalogo.ui.components.AvisoInformativo
import com.fernandez.catalogo.ui.components.EncabezadoPantalla
import com.fernandez.catalogo.ui.components.EstadoVacio
import com.fernandez.catalogo.ui.components.ImagenLaptop
import java.util.Locale

@Composable
fun ListaScreen(
    viewModel: ListaViewModel,
    onSeleccionarItem: (Int) -> Unit,
    onVerFavoritos: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val actualizacionFallida by viewModel.actualizacionFallida.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val managerNotificaciones = remember(context.applicationContext) {
        NotificationManagerCompat.from(context.applicationContext)
    }
    var permisoPendiente by rememberSaveable { mutableStateOf(false) }
    var avisoNotificacion by rememberSaveable { mutableStateOf<String?>(null) }

    fun enviarNotificacion() {
        if (managerNotificaciones.areNotificationsEnabled()) {
            avisoNotificacion = null
            viewModel.probarNotificacion()
        } else {
            avisoNotificacion = "Las notificaciones están desactivadas para esta aplicación."
        }
    }

    val solicitarPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (permisoPendiente) {
            permisoPendiente = false
            if (concedido) {
                enviarNotificacion()
            } else {
                avisoNotificacion = "Permiso de notificaciones denegado. No se envió ninguna notificación."
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "encabezado") {
            EncabezadoPantalla("Catálogo de laptops", "Explora las laptops disponibles y guarda tus favoritas.")
        }
        item(key = "acciones") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                FilledTonalButton(onClick = onVerFavoritos) { Text("Mis favoritos") }
                TextButton(onClick = onCerrarSesion) { Text("Cerrar sesión") }
                OutlinedButton(
                    onClick = {
                        avisoNotificacion = null
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            permisoPendiente = true
                            solicitarPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            enviarNotificacion()
                        }
                    },
                    enabled = !permisoPendiente
                ) { Text("Probar notificación") }
            }
        }
        avisoNotificacion?.let { mensaje ->
            item(key = "aviso-notificacion") { AvisoInformativo(mensaje) }
        }

        when (val estadoActual = estado) {
            UiState.Cargando -> item(key = "cargando") { Cargando(Modifier.heightIn(min = 240.dp)) }
            is UiState.Exito -> {
                if (estadoActual.datos.isEmpty()) {
                    item(key = "vacio") {
                        EstadoVacio("Sin productos", "No hay productos disponibles.")
                    }
                } else {
                    if (actualizacionFallida) {
                        item(key = "actualizacion-fallida") {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                AvisoInformativo("No se pudo actualizar. Mostrando datos guardados.")
                                TextButton(onClick = viewModel::cargarItems) { Text("Reintentar") }
                            }
                        }
                    }
                    items(estadoActual.datos, key = { it.id }) { item ->
                        TarjetaLaptop(
                            item = item,
                            onSeleccionar = { onSeleccionarItem(item.id) },
                            onAlternarFavorito = { viewModel.alternarFavorito(item.id) }
                        )
                    }
                }
            }
            is UiState.Error -> item(key = "error") {
                ErrorView(estadoActual.mensaje, viewModel::cargarItems, Modifier.heightIn(min = 240.dp))
            }
        }
    }
}

@Composable
internal fun TarjetaLaptop(
    item: Item,
    onSeleccionar: () -> Unit,
    onAlternarFavorito: () -> Unit
) {
    Card(
        onClick = onSeleccionar,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ImagenLaptop(item.imagenUrl, item.titulo, altura = 200.dp)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = item.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconToggleButton(
                    checked = item.esFavorito,
                    onCheckedChange = { onAlternarFavorito() },
                    modifier = Modifier.size(48.dp).background(
                        if (item.esFavorito) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                        CircleShape
                    )
                ) {
                    Icon(
                        imageVector = if (item.esFavorito) CorazonActivo else CorazonVacio,
                        contentDescription = if (item.esFavorito) {
                            "Quitar ${item.titulo} de favoritos"
                        } else {
                            "Agregar ${item.titulo} a favoritos"
                        },
                        tint = if (item.esFavorito) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            Text(
                text = item.subtitulo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Text("Precio", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = String.format(
                    Locale.forLanguageTag("es-PE"),
                    "S/ %,.2f",
                    item.precio
                ),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private val CorazonActivo = crearCorazon(activo = true)
private val CorazonVacio = crearCorazon(activo = false)

private fun crearCorazon(activo: Boolean): ImageVector = ImageVector.Builder(
    name = if (activo) "CorazonActivo" else "CorazonVacio",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = if (activo) SolidColor(Color.Black) else null,
        stroke = if (activo) null else SolidColor(Color.Black),
        strokeLineWidth = 2f
    ) {
        moveTo(12f, 21f)
        lineTo(10.55f, 19.68f)
        curveTo(5.4f, 15.01f, 2f, 11.92f, 2f, 8.5f)
        curveTo(2f, 5.41f, 4.42f, 3f, 7.5f, 3f)
        curveTo(9.24f, 3f, 10.91f, 3.81f, 12f, 5.09f)
        curveTo(13.09f, 3.81f, 14.76f, 3f, 16.5f, 3f)
        curveTo(19.58f, 3f, 22f, 5.41f, 22f, 8.5f)
        curveTo(22f, 11.92f, 18.6f, 15.01f, 13.45f, 19.68f)
        lineTo(12f, 21f)
        close()
    }
}.build()
