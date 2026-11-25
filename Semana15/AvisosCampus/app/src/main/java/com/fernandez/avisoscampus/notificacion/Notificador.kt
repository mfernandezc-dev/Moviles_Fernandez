
package com.fernandez.avisoscampus.notificacion

import android.content.Context
import com.fernandez.avisoscampus.model.Aviso
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

object Notificador {

    const val CANAL = "avisos"

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val canal = NotificationChannel(
                CANAL,
                "Avisos",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = context.getSystemService(
                NotificationManager::class.java
            )

            manager.createNotificationChannel(canal)
        }
    }

    fun tienePermiso(context: Context): Boolean {
        // TODO Paso 2: comprobar permiso
        return false
    }

    fun mostrar(
        context: Context,
        aviso: Aviso
    ): Boolean {
        // TODO Paso 3: mostrar notificacion
        return false
    }
}
