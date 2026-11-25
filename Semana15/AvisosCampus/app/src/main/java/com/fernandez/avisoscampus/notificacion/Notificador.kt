
package com.fernandez.avisoscampus.notificacion

import android.content.Context
import com.fernandez.avisoscampus.model.Aviso

object Notificador {

    const val CANAL = "avisos"

    fun crearCanal(context: Context) {
        // TODO Paso 1: crear canal
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
