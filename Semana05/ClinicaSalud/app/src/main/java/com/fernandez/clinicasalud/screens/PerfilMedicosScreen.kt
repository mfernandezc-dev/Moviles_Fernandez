package com.fernandez.clinicasalud.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PerfilMedicosScreen(
    nombre: String
) {

    Text(
        text = "Perfil del médico: $nombre"
    )

}