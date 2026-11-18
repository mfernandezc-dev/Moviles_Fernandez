package com.fernandez.campusgps.data

import android.annotation.SuppressLint
import android.content.Context
import com.fernandez.campusgps.model.Posicion
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import android.location.LocationManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class GpsApagadoException :
    Exception("La ubicación está desactivada. Activa el GPS e intenta nuevamente.")

class PlayServicesNoDisponibleException :
    Exception("Google Play Services no está disponible en este dispositivo.")
class UbicacionRepositoryGoogle(
    context: Context
) : UbicacionRepository {

    private val contexto = context.applicationContext

    private val cliente =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun obtenerPosicion(): Result<Posicion> {

        return runCatching {

            // Comprueba Google Play Services.
            val estadoPlayServices =
                GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(contexto)

            if (estadoPlayServices != ConnectionResult.SUCCESS) {
                throw PlayServicesNoDisponibleException()
            }

            // Comprueba los servicios de ubicación.
            val locationManager =
                contexto.getSystemService(Context.LOCATION_SERVICE)
                        as LocationManager

            val gpsActivo =
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            val redActiva =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!gpsActivo && !redActiva) {
                throw GpsApagadoException()
            }

            // Obtiene la ubicación.
            val token = CancellationTokenSource()

            val ubicacion = cliente.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                token.token
            ).await()

            if (ubicacion == null) {
                throw IllegalStateException(
                    "No se pudo obtener la ubicación. Activa el GPS e Intenta de nuevo."
                )
            }

            Posicion(
                latitud = ubicacion.latitude,
                longitud = ubicacion.longitude
            )
        }
    }
}