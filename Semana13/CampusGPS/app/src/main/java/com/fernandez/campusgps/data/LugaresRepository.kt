package com.fernandez.campusgps.data

import com.fernandez.campusgps.model.Lugar

interface LugaresRepository {
    fun obtenerLugares(): List<Lugar>
}