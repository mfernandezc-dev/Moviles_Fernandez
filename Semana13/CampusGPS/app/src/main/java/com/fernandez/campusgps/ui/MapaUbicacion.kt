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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

import com.fernandez.campusgps.model.Lugar
import com.fernandez.campusgps.ui.theme.AzulPrincipal
import com.fernandez.campusgps.ui.theme.Blanco
import com.fernandez.campusgps.ui.theme.RojoUbicacion
import com.fernandez.campusgps.ui.theme.VerdeCercano

import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.overlay.MapOverlay
import org.maplibre.compose.overlay.include
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import androidx.compose.runtime.LaunchedEffect
import org.maplibre.compose.camera.CameraUpdate

@Composable
fun MapaUbicacion(
    latitud: Double,
    longitud: Double,
    lugares: List<Lugar>,
    seleccionadoId: Int?,
    masCercanoId: Int?,
    onSeleccionar: (Int) -> Unit,
    modifier: Modifier = Modifier.height(300.dp)
) {


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

    LaunchedEffect(seleccionadoId) {

        val lugar = lugares.firstOrNull {
            it.id == seleccionadoId
        }

        if (lugar != null) {

            estadoMapa.animateCamera(
                update = CameraUpdate(
                    center = Position(
                        latitude = lugar.latitud,
                        longitude = lugar.longitud
                    ),
                    zoom = 17.0
                )
            )
        }
    }

    // Muestra el mapa interactivo.
    MaplibreMap(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        state = estadoMapa
    ) {

        // Conserva los controles y atribuciones del mapa.
        include(MapOverlay.Default)

        // Representa la posición recibida sin cambiar la cámara ni solicitar otra ubicación.
        Box(
            modifier = Modifier
                .placedAt(
                    position = Position(latitude = latitud, longitude = longitud),
                    alignment = Alignment.Center
                )
                .size(28.dp)
                .background(RojoUbicacion, CircleShape)
                .border(3.dp, Blanco, CircleShape)
                .semantics { contentDescription = "Tu ubicación actual" }
        )

        // Recorre los cinco lugares del campus.
        lugares.forEach { lugar ->

            val seleccionado = seleccionadoId == lugar.id

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
                        modifier = Modifier.widthIn(max = 200.dp),
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

                // Mantiene el verde del más cercano y el azul de los demás lugares.
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(24.dp)
                        .border(
                            width = 2.dp,
                            color = Blanco,
                            shape = CircleShape
                        )
                        .background(
                            color = if (lugar.id == masCercanoId) {
                                VerdeCercano
                            } else {
                                AzulPrincipal
                            },
                            shape = CircleShape
                        )
                        .clickable {
                            onSeleccionar(lugar.id)
                        }
                        .semantics {
                            contentDescription = if (lugar.id == masCercanoId) {
                                "${lugar.nombre}, lugar más cercano"
                            } else {
                                lugar.nombre
                            }
                        }
                )
            }
        }
    }
}
