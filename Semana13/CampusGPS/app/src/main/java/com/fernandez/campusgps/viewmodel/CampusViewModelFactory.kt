package com.fernandez.campusgps.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fernandez.campusgps.data.LugaresRepositoryLocal
import com.fernandez.campusgps.data.UbicacionRepositoryGoogle

class CampusViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {

    private val appContext = context.applicationContext

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(MapaViewModel::class.java)) {

            val ubicacionRepo =
                UbicacionRepositoryGoogle(appContext)

            val lugaresRepo =
                LugaresRepositoryLocal()

            @Suppress("UNCHECKED_CAST")
            return MapaViewModel(
                ubicacionRepo,
                lugaresRepo
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}