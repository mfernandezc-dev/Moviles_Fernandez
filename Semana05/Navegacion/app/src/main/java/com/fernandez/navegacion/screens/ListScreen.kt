package com.fernandez.navegacion.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fernandez.navegacion.navigation.Screen
import com.fernandez.navegacion.ui.theme.*

data class StudentInfo(
    val id: Int,
    val name: String,
    val career: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(navController: NavController) {
    // Lista de alumnos con datos reales / ficticios requeridos
    val estudiantes = listOf(
        StudentInfo(1, "Juan León", "Ingeniería de Sistemas"),
        StudentInfo(2, "Maria Garcia", "Arquitectura"),
        StudentInfo(3, "Carlos Perez", "Medicina"),
        StudentInfo(4, "Ana Lopez", "Derecho"),
        StudentInfo(5, "Luis Ramirez", "Administración"),
        StudentInfo(6, "Michael Fernandez", "Ingeniería de Software"),
        StudentInfo(7, "Fernanda Ruiz", "Desarrollo Web Fullstack"),
        StudentInfo(8, "Gabriel Torres", "Inteligencia Artificial")
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Directorio de Alumnos",
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
                // Divisor horizontal OBLIGATORIO inmediatamente debajo de la TopAppBar
                HorizontalDivider(
                    color = TonalPurpleMedium.copy(alpha = 0.2f),
                    thickness = 1.dp
                )
            }
        }
    ) { padding ->
        // Fondo general blanco puro (#FFFFFF) en toda la LazyColumn / Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(estudiantes.size) { index ->
                    val alumno = estudiantes[index]

                    // Tarjeta / Surface de alumno en color morado claro / lavanda tenue (SurfaceVariantLight)
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    Screen.Detail.createRoute(alumno.id)
                                )
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = SurfaceVariantLight
                        ),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Extremo izquierdo: Foto de perfil circular de 50.dp
                            Surface(
                                shape = CircleShape,
                                color = TonalPurplePrimary,
                                modifier = Modifier.size(50.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Avatar Alumno",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Columna Central: Nombre completo en negrita (TitleMedium) y Carrera (BodyMedium)
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = alumno.name,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TonalPurpleDeep
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = alumno.career,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TonalPurpleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Extremo derecho: Ícono de flecha de navegación (chevron_right / KeyboardArrowRight)
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Ver detalle",
                                tint = TonalPurpleMedium,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListScreenPreview() {
    NavegacionTheme {
        ListScreen(rememberNavController())
    }
}
