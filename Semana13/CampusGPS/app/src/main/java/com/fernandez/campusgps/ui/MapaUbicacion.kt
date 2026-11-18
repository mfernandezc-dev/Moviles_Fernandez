package com.fernandez.campusgps.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position

@Composable
//Recibe las coordenadas:
fun MapaUbicacion(
    latitud: Double,
    longitud: Double
) {

    val estadoMapa = rememberMapState(
        //Indica de dónde se obtiene el estilo y contenido del mapa.
        baseStyle = BaseStyle.Uri(
            "https://tiles.openfreemap.org/styles/liberty"
        ),
        //El mapa empezará centrado en las coordenadas recibidas.
        initialCameraPosition = CameraPosition(
            center = Position(
                latitude = latitud,
                longitude = longitud
            ),
            zoom = 16.0
        )
    )

    //El mapa ocupará todo el ancho disponible y tendrá una altura de 300.dp.
    MaplibreMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        state = estadoMapa
    )
}