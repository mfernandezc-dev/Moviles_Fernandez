
package com.fernandez.cafecampus.ui.cliente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernandez.cafecampus.model.Rol
import com.fernandez.cafecampus.ui.theme.ClientePrimario
import com.fernandez.cafecampus.ui.theme.AdminPrimario
import com.fernandez.cafecampus.viewmodel.SesionViewModel

@Composable
fun LoginScreen(vm: SesionViewModel) {

    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var rol by rememberSaveable { mutableStateOf(Rol.CLIENTE) }

    val error by vm.error.collectAsState()
    val cargando by vm.cargando.collectAsState()

    val colorRol = if (rol == Rol.CLIENTE) {
        ClientePrimario
    } else {
        AdminPrimario
    }

    val fondoSelector = Color(0xFFF3F4F6)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 20.dp)
            .padding(top = 90.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {

        // ENCABEZADO

        Text(
            text = "Bienvenido",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Ingresa para continuar",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(30.dp))

        // SELECTOR DE PERFIL

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    fondoSelector,
                    RoundedCornerShape(12.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            listOf(
                Rol.CLIENTE to "Estudiante",
                Rol.ADMIN to "Administrador"
            ).forEach { (opcion, texto) ->

                val seleccionado = rol == opcion

                Button(
                    onClick = {
                        rol = opcion
                        vm.limpiarError()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(9.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (seleccionado) {
                            colorRol
                        } else {
                            Color.Transparent
                        },
                        contentColor = if (seleccionado) {
                            Color.White
                        } else {
                            Color.DarkGray
                        }
                    )
                ) {
                    Text(
                        text = texto,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // CAMPO CORREO

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            placeholder = { Text("correo@campus.test") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    tint = colorRol
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorRol,
                focusedLabelColor = colorRol
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CAMPO CONTRASEÑA

        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = colorRol
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorRol,
                focusedLabelColor = colorRol
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorRol
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // BOTÓN INGRESAR

        Button(
            onClick = {
                vm.login(correo, clave, rol)
            },
            enabled = !cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorRol,
                contentColor = Color.White
            )
        ) {
            Text(
                text = if (cargando) "Ingresando..." else "Ingresar",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CONTENIDO INFERIOR SEGÚN PERFIL

        if (rol == Rol.CLIENTE) {

            Text(
                text = "¿No tienes cuenta? Regístrate",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = colorRol,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )

        } else {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFEEF2FF),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Modo práctica",
                    color = AdminPrimario,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Puedes cambiar de perfil desde Ajustes para probar ambos roles.",
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
