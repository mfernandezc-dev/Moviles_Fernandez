package com.fernandez.clinicasalud.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fernandez.clinicasalud.model.Medico

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen() {

    val especialidades = listOf(
        "Cardiología",
        "Pediatría",
        "Dermatología"
    )

    val medicos = listOf(
        Medico(
            "Dra. Ana Torres",
            "Cardiología",
            4.9
        ),
        Medico(
            "Dr. Luis Vega",
            "Pediatría",
            4.7
        ),
        Medico(
            "Dra. Rosa Díaz",
            "Dermatología",
            4.8
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Clínica Salud+"
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LazyRow(
                modifier = Modifier.padding(16.dp)
            ) {
                items(especialidades) { especialidad ->
                    Card(
                        modifier = Modifier
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = especialidad,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Text(
                text = "Médicos disponibles",
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn {
                items(medicos) { medico ->
                    Card(
                        modifier = Modifier
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = medico.nombre
                            )
                            Text(
                                text = medico.especialidad
                            )
                            Text(
                                text = "Calificación: ${medico.calificacion}"
                            )
                        }
                    }
                }
            }
        }
    }
}
