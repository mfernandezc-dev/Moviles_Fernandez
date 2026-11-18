package com.fernandez.campusgps.data

import android.annotation.SuppressLint
import android.content.Context
import com.fernandez.campusgps.model.Posicion
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

class UbicacionRepositoryGoogle(
    context: Context
) : UbicacionRepository {

    private val cliente =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun obtenerPosicion(): Result<Posicion> {

        return runCatching {

            val token = CancellationTokenSource()

            val ubicacion = cliente.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                token.token
            ).await()

            if (ubicacion == null) {
                throw IllegalStateException(
                    "No se pudo obtener la ubicación"
                )
            }

            Posicion(
                latitud = ubicacion.latitude,
                longitud = ubicacion.longitude
            )
        }
    }
}