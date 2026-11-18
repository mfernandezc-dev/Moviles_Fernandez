package com.fernandez.campusgps.ui

import android.location.Location
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
import androidx.compose.runtime.remember
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
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.FillLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.Polygon
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MapaUbicacion(
    latitud: Double,
    longitud: Double,
    lugares: List<Lugar>,
    seleccionadoId: Int?,
    masCercanoId: Int?,
    onSeleccionar: (Int) -> Unit,
    radioMetros: Int = 500,
    modifier: Modifier = Modifier.height(300.dp)
) {

    val areaBusqueda = remember(latitud, longitud, radioMetros) {
        GeoJsonData.Features(crearCirculoGeografico(latitud, longitud, radioMetros))
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
    ) {
        val fuenteRadio = rememberGeoJsonSource(data = areaBusqueda)
        // Las capas pertenecen al estilo, no al overlay de componentes Compose.
        // Sin manejadores de clic: no interceptan los gestos del mapa.
        FillLayer(
            id = "radio-busqueda-relleno",
            source = fuenteRadio,
            color = const(AzulPrincipal),
            opacity = const(0.15f)
        )
        LineLayer(
            id = "radio-busqueda-borde",
            source = fuenteRadio,
            color = const(AzulPrincipal),
            width = const(2.dp)
        )
    }

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

private fun crearCirculoGeografico(
    latitud: Double,
    longitud: Double,
    radioMetros: Int
): Polygon {
    val latitudRad = Math.toRadians(latitud)
    val longitudRad = Math.toRadians(longitud)
    val segmentos = 96
    val resultado = FloatArray(1)
    val anillo = List(segmentos) { indice ->
        // Sentido antihorario para el anillo exterior GeoJSON.
        val rumbo = -2.0 * PI * indice / segmentos
        var distanciaAngular = radioMetros / 6_371_008.8
        var destino = Position(longitude = longitud, latitude = latitud)

        repeat(3) {
            val latitudDestino = asin(
                (sin(latitudRad) * cos(distanciaAngular) +
                        cos(latitudRad) * sin(distanciaAngular) * cos(rumbo)).coerceIn(-1.0, 1.0)
            )
            val longitudDestino = longitudRad + atan2(
                sin(rumbo) * sin(distanciaAngular) * cos(latitudRad),
                cos(distanciaAngular) - sin(latitudRad) * sin(latitudDestino)
            )
            destino = Position(
                latitude = Math.toDegrees(latitudDestino),
                longitude = (Math.toDegrees(longitudDestino) + 540.0) % 360.0 - 180.0
            )
            // Ajusta los vértices al radio WGS84 usado también por distanciaMetros.
            Location.distanceBetween(
                latitud, longitud, destino.latitude, destino.longitude, resultado
            )
            distanciaAngular *= radioMetros / resultado[0].toDouble()
        }
        destino
    }
    return Polygon(coordinates = listOf(anillo + listOf(anillo.first())))
}
