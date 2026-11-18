package com.fernandez.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.fernandez.campusgps.model.UbicacionUi
import com.fernandez.campusgps.ui.theme.AzulPrincipal
import com.fernandez.campusgps.ui.theme.Blanco
import com.fernandez.campusgps.util.formatearDistancia
import com.fernandez.campusgps.viewmodel.MapaViewModel

@Composable
fun PantallaMapa(
    vm: MapaViewModel
) {

    val estado by vm.estado.collectAsState()
    val lugares by vm.lugaresFiltrados.collectAsState()
    val categoriaSeleccionada by vm.categoriaSeleccionada.collectAsState()
    val radioSeleccionado by vm.radioSeleccionado.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()
    val masCercano by vm.masCercano.collectAsState()

    val contexto = LocalContext.current

    val lanzadorPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->

        val permisoConcedido = resultado.values.any { it }

        if (permisoConcedido) {
            vm.buscar()
        } else {
            vm.permisoDenegado()
        }
    }

    fun solicitarUbicacion() {

        val permisoFine = ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val permisoCoarse = ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (permisoFine || permisoCoarse) {

            vm.buscar()

        } else {

            lanzadorPermisos.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Surface(color = AzulPrincipal, contentColor = Blanco) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                            )
                        )
                ) {
                    Text(
                        text = "CampusGPS",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                    )
                }
            }
        }
    ) { paddingPantalla ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingPantalla)
        ) {
            // En pantallas bajas el contenido puede desplazarse sin recortar controles.
            val alturaMapa = (maxHeight * 0.43f).coerceAtLeast(240.dp)
            val alturaLista = (maxHeight * 0.24f).coerceIn(160.dp, 220.dp)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Button(
                    modifier = Modifier
                        .widthIn(min = 220.dp)
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulPrincipal,
                        contentColor = Blanco
                    ),
                    onClick = { solicitarUbicacion() },
                    enabled = estado != UbicacionUi.Buscando
                ) {
                    Text("Obtener mi ubicación")
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = vm.categorias, key = { it }) { categoria ->
                        FilterChip(
                            selected = categoria == categoriaSeleccionada,
                            onClick = { vm.seleccionarCategoria(categoria) },
                            label = { Text(categoria) },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Radio de búsqueda",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.widthIn(max = 104.dp)
                    )
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = vm.radiosDisponibles, key = { it }) { radio ->
                            FilterChip(
                                selected = radio == radioSeleccionado,
                                onClick = { vm.seleccionarRadio(radio) },
                                label = { Text("$radio m") },
                                shape = RoundedCornerShape(50),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (val actual = estado) {
                    UbicacionUi.Inicial -> {
                        Text("Presiona el botón para obtener tu ubicación")
                    }

                    UbicacionUi.Buscando -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Buscando ubicación...")
                    }

                    is UbicacionUi.Lista -> {
                        Text(
                            text = "Latitud: ${actual.posicion.latitud}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Longitud: ${actual.posicion.longitud}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        MapaUbicacion(
                            latitud = actual.posicion.latitud,
                            longitud = actual.posicion.longitud,
                            lugares = lugares.map { it.lugar },
                            seleccionadoId = seleccionado,
                            masCercanoId = masCercano?.id,
                            radioMetros = radioSeleccionado,
                            onSeleccionar = { id -> vm.seleccionar(id) },
                            modifier = Modifier.height(alturaMapa)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            modifier = Modifier.heightIn(min = 48.dp),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Blanco,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                masCercano?.let { lugar -> vm.seleccionar(lugar.id) }
                            },
                            enabled = masCercano != null
                        ) {
                            Text("Ir al más cercano")
                        }

                        masCercano?.let { lugar ->
                            val distancia = lugares.firstOrNull {
                                it.lugar.id == lugar.id
                            }?.metros

                            Text(
                                text = "Más cercano: ${lugar.nombre} " +
                                        "(${distancia?.let { formatearDistancia(it) } ?: "Sin calcular"})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        if (lugares.isEmpty()) {
                            Text(
                                text = "No hay lugares dentro del radio seleccionado",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            )
                        } else {
                            ListaLugares(
                                lugares = lugares,
                                seleccionado = seleccionado,
                                masCercanoId = masCercano?.id,
                                onClick = { id -> vm.seleccionar(id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(alturaLista)
                            )
                        }
                    }

                    is UbicacionUi.Error -> {
                        MensajeError(
                            error = actual,
                            onReintentar = { solicitarUbicacion() }
                        )
                    }
                }
            }
        }
    }
}
