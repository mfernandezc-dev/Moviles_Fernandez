package com.fernandez.navegacion.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fernandez.navegacion.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    itemId: Int
) {
    val studentList = listOf(
        "Juan León" to "Ingeniería de Sistemas",
        "Maria Garcia" to "Arquitectura",
        "Carlos Perez" to "Medicina",
        "Ana Lopez" to "Derecho",
        "Luis Ramirez" to "Administración",
        "Michael Fernandez" to "Ingeniería de Software",
        "Fernanda Ruiz" to "Desarrollo Web Fullstack",
        "Gabriel Torres" to "Inteligencia Artificial"
    )
    val (name, career) = studentList.getOrElse(itemId - 1) { "Estudiante #$itemId" to "Carrera Profesional" }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Detalle del Estudiante",
                            fontWeight = FontWeight.Bold,
                            color = TonalPurpleDeep
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = TonalPurpleDeep
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
                HorizontalDivider(
                    color = TonalPurpleMedium.copy(alpha = 0.2f),
                    thickness = 1.dp
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TonalPurpleDeep
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = career,
                    style = MaterialTheme.typography.titleMedium,
                    color = TonalPurpleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(20.dp))

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = SurfaceVariantLight
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = TonalPurpleDeep,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Código ID: #2024-000$itemId",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        HorizontalDivider(color = TonalPurpleMedium.copy(alpha = 0.2f))

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Expediente de estudiante consultado desde el Directorio de Alumnos mediante la ruta tipada $itemId.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TonalPurplePrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Regresar al Directorio", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
