package com.fernandez.cafecampus

import android.app.Application
import com.fernandez.cafecampus.data.local.CafeDatabase
import com.fernandez.cafecampus.data.local.DatosIniciales
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CafeApp : Application() {

    lateinit var database: CafeDatabase
        private set

    private val applicationScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        database = CafeDatabase.obtener(this)

        applicationScope.launch {
            DatosIniciales.sembrar(database)
        }
    }
}