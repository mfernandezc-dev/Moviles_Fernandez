package com.tecsup.centrotareas.data

import kotlinx.coroutines.flow.Flow
import com.tecsup.centrotareas.model.AvancePrimos

interface TareasRepository {
    fun progresoTarea(): Flow<Float>
    suspend fun contarPrimos(hasta: Int): Int
    fun contarPrimosConProgreso(hasta: Int): Flow<AvancePrimos>
    suspend fun cargarPerfil(): String
    suspend fun cargarPedidos(simularError: Boolean): List<String>
}
