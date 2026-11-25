
package com.fernandez.avisoscampus

import android.app.Application
import com.fernandez.avisoscampus.notificacion.Notificador

class AvisosApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Notificador.crearCanal(this)
    }
}
