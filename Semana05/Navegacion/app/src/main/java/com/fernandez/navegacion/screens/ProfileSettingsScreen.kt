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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fernandez.navegacion.navigation.Screen
import com.fernandez.navegacion.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(navController: NavController) {
    val nombreCompleto = "Michael Fernandez"
    val correo = "michael.fernandez@tecsup.edu.pe"
    val telefono = "+51 987 654 321"
    val carrera = "Ingeniería de Software"
    val cicloActual = "VI Ciclo"

    val scrollState = rememberScrollState()

    // Color de fondo base neutro casi blanco para máximo contraste
    val baseBackgroundColor = BackgroundWhite

    Scaffold(
        containerColor = baseBackgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configuración de Perfil",
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
                    containerColor = baseBackgroundColor
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(baseBackgroundColor)
        ) {
            // Contenido superior desplazable
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                // 1. BANNER SUPERIOR RECTANGULAR Y CON DEGRADADO
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF432371),
                                    Color(0xFF6B48B7),
                                    Color(0xFF7E57C2)
                                )
                            ),
                            shape = RectangleShape
                        )
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar centrado
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = TonalPurplePrimary,
                            border = BorderStroke(2.dp, Color.White)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Avatar de Usuario",
                                    tint = Color.White,
                                    modifier = Modifier.size(46.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Nombre Completo en blanco
                        Text(
                            text = nombreCompleto,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. SECCIONES DE INFORMACIÓN Y CONTRASTE
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    // SECCIÓN: INFORMACIÓN PERSONAL
                    Text(
                        text = "INFORMACIÓN PERSONAL",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TonalPurpleMedium,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

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
                        Column(modifier = Modifier.padding(16.dp)) {
                            SettingItemRow(
                                icon = Icons.Default.Person,
                                label = "Nombre Completo",
                                value = nombreCompleto
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = TonalPurpleMedium.copy(alpha = 0.15f)
                            )

                            SettingItemRow(
                                icon = Icons.Default.Email,
                                label = "Correo Electrónico",
                                value = correo
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = TonalPurpleMedium.copy(alpha = 0.15f)
                            )

                            SettingItemRow(
                                icon = Icons.Default.Phone,
                                label = "Teléfono",
                                value = telefono
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECCIÓN: ACADÉMICO
                    Text(
                        text = "ACADÉMICO",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TonalPurpleMedium,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

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
                        Column(modifier = Modifier.padding(16.dp)) {
                            SettingItemRow(
                                icon = Icons.Default.School,
                                label = "Carrera",
                                value = carrera
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = TonalPurpleMedium.copy(alpha = 0.15f)
                            )

                            SettingItemRow(
                                icon = Icons.Default.Badge,
                                label = "Ciclo Actual",
                                value = cicloActual
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 3. BOTÓN DE CERRAR SESIÓN (MÁS DELGADO Y EN LA PARTE INFERIOR)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp), // Altura reducida estilizada
                    shape = CircleShape, // Estilo píldora totalmente redondeada
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = TonalRed
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        tint = TonalRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cerrar Sesión",
                        fontWeight = FontWeight.Bold,
                        color = TonalRed,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingItemRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = CircleShape,
            color = TonalPurplePrimary.copy(alpha = 0.1f),
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TonalPurplePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

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
fun ProfileSettingsScreenPreview() {
    NavegacionTheme {
        ProfileSettingsScreen(rememberNavController())
    }
}
