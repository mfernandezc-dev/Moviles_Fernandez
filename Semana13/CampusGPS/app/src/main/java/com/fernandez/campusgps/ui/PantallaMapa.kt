package com.fernandez.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.fernandez.campusgps.model.UbicacionUi
import com.fernandez.campusgps.viewmodel.MapaViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun PantallaMapa(
    vm: MapaViewModel
) {

    val estado by vm.estado.collectAsState()
    val lugares by vm.lugares.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "CampusGPS",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {
                solicitarUbicacion()
            },
            enabled = estado != UbicacionUi.Buscando
        ) {
            Text("Obtener mi ubicación")
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        when (val actual = estado) {

            UbicacionUi.Inicial -> {

                Text(
                    "Presiona el botón para obtener tu ubicación"
                )
            }

            UbicacionUi.Buscando -> {

                CircularProgressIndicator()

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text("Buscando ubicación...")
            }

            is UbicacionUi.Lista -> {

                Text(
                    text = "Latitud: ${actual.posicion.latitud}"
                )

                Text(
                    text = "Longitud: ${actual.posicion.longitud}"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                MapaUbicacion(
                    latitud = actual.posicion.latitud,
                    longitud = actual.posicion.longitud,
                    lugares = lugares.map { it.lugar },
                    seleccionadoId = seleccionado,
                    onSeleccionar = { id ->
                        vm.seleccionar(id)
                    },
                    modifier = Modifier.weight(1f)
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                ListaLugares(
                    lugares = lugares,
                    seleccionado = seleccionado,
                    onClick = { id ->
                        vm.seleccionar(id)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }

            is UbicacionUi.Error -> {

                Text(
                    text = actual.mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}