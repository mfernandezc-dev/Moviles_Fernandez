package com.tecsup.lab04

import androidx.compose.runtime.Composable

data class Tarea(
    val id: Int,
    val nombre: String,
    val completada: Boolean = false
)

@Composable
fun ItemTarea(
    tarea: Tarea,
    onCambiarEstado: (Boolean) -> Unit,
    onEliminarTarea: () -> Unit
){}