package com.fernandez.campusgps.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import com.fernandez.campusgps.model.Lugar

import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.overlay.MapOverlay
import org.maplibre.compose.overlay.include
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position

@Composable
fun MapaUbicacion(
    latitud: Double,
    longitud: Double,
    lugares: List<Lugar>
) {

    // Guarda el identificador del marcador seleccionado.
    var lugarSeleccionado by remember {
        mutableStateOf<Int?>(null)
    }

    // Configura el mapa utilizando OpenFreeMap.
    val estadoMapa = rememberMapState(
        baseStyle = BaseStyle.Uri(
            "https://tiles.openfreemap.org/styles/liberty"
        ),
        initialCameraPosition = CameraPosition(
            center = Position(
                latitude = latitud,
                longitude = longitud
            ),
            zoom = 16.0
        )
    )

    // Muestra el mapa interactivo.
    MaplibreMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        state = estadoMapa
    ) {

        // Conserva los controles y atribuciones del mapa.
        include(MapOverlay.Default)

        // Recorre los cinco lugares del campus.
        lugares.forEach { lugar ->

            val seleccionado = lugarSeleccionado == lugar.id

            // Coloca el marcador en las coordenadas del lugar.
            Column(
                modifier = Modifier.placedAt(
                    position = Position(
                        latitude = lugar.latitud,
                        longitude = lugar.longitud
                    ),
                    alignment = Alignment.BottomCenter
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Muestra el nombre y la categoría al seleccionar.
                if (seleccionado) {

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp
                    ) {

                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {

                            Text(
                                text = lugar.nombre,
                                style = MaterialTheme.typography.labelMedium
                            )

                            Text(
                                text = "Categoría: ${lugar.categoria}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Dibuja el marcador azul.
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(24.dp)
                        .border(
                            width = 2.dp,
                            color = Color.White,
                            shape = CircleShape
                        )
                        .background(
                            color = Color(0xFF2196F3),
                            shape = CircleShape
                        )
                        .clickable {
                            lugarSeleccionado =
                                if (seleccionado) null
                                else lugar.id
                        }
                )
            }
        }
    }
}