package com.fernandez.navegacion.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fernandez.navegacion.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    // Datos del Usuario Activo
    val nombre = "Michael Fernandez"
    val correo = "michael.fernandez@tecsup.edu.pe"
    val carrera = "Ingeniería de Software"
    val idEstudiante = "2024-0005"
    val facultad = "Ingeniería y Tecnología"
    val biografia = "Estudiante dedicado con interés en desarrollo Android y arquitectura de software."

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color.White, // Fondo general blanco puro
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Expediente Académico",
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White) // Fondo general blanco puro (#FFFFFF)
                .verticalScroll(scrollState)
        ) {
            // BANNER SUPERIOR CON DEGRADADO Y ESQUINAS INFERIORES REDONDEADAS
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Banner con altura de 180dp e inferior redondeado (28dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF5D497E), TonalPurpleDeep)
                            )
                        )
                )

                // Avatar circular superpuesto en el borde inferior del banner con borde blanco prominente
                Surface(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = 50.dp),
                    shape = CircleShape,
                    color = TonalPurplePrimary,
                    border = BorderStroke(3.dp, Color.White),
                    shadowElevation = 6.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar de Usuario",
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }
            }

            // Espaciado para compensar la superposición del avatar
            Spacer(modifier = Modifier.height(58.dp))

            // Nombre y Carrera DEBAJO del avatar sobre el fondo blanco
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = TonalPurpleDeep,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = carrera,
                    style = MaterialTheme.typography.titleMedium,
                    color = TonalPurpleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // TARJETA ÚNICA DE INFORMACIÓN (Fondo morado claro / lavanda tenue con íconos limpios en púrpura oscuro)
            PaddingBox {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = SurfaceVariantLight
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // ID Estudiante
                        CleanRecordDetailRow(
                            icon = Icons.Default.Badge,
                            label = "ID Estudiante",
                            value = idEstudiante
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = TonalPurpleMedium.copy(alpha = 0.2f)
                        )

                        // Correo Electrónico
                        CleanRecordDetailRow(
                            icon = Icons.Default.Email,
                            label = "Correo Electrónico",
                            value = correo
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = TonalPurpleMedium.copy(alpha = 0.2f)
                        )

                        // Facultad
                        CleanRecordDetailRow(
                            icon = Icons.Default.Book,
                            label = "Facultad",
                            value = facultad
                        )

                        // Línea divisoria fina separando los datos personales del bloque "Biografía"
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = TonalPurpleMedium.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )

                        // Sección Biografía
                        Text(
                            text = "Biografía",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TonalPurpleDeep
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = biografia,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun PaddingBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        content()
    }
}

/**
 * Fila de detalle con ícono limpio en púrpura oscuro sobre el fondo de la tarjeta (sin círculos morados)
 */
@Composable
private fun CleanRecordDetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TonalPurpleDeep, // Ícono limpio en púrpura oscuro
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TonalPurpleMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    NavegacionTheme {
        ProfileScreen(rememberNavController())
    }
}
