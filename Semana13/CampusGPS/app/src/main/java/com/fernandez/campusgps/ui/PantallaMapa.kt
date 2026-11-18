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

@Composable
fun PantallaMapa(
    vm: MapaViewModel
) {

    val estado by vm.estado.collectAsState()

    val contexto = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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

                val permisoFine =
                    ContextCompat.checkSelfPermission(
                        contexto,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                val permisoCoarse =
                    ContextCompat.checkSelfPermission(
                        contexto,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                if (permisoFine || permisoCoarse) {
                    vm.buscar()
                } else {
                    vm.permisoDenegado()
                }
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
                    "Latitud: ${actual.posicion.latitud}"
                )

                Text(
                    "Longitud: ${actual.posicion.longitud}"
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