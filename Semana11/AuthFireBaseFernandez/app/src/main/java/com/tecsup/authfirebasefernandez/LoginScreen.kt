package com.tecsup.authfirebasefernandez

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.graphics.Color

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val auth = remember { FirebaseAuth.getInstance() }
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Programación Móvil - Tecsup",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Image(
                painter = painterResource(id = R.drawable.tecsup_logo),
                contentDescription = "Logo Tecsup",
                modifier = Modifier
                    .width(170.dp)
                    .height(70.dp)
            )

            Spacer(modifier = Modifier.height(70.dp))

            Text(
                text = "Iniciar Sesión 🔐",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))


            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(
                            context,
                            "Complete todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        isLoading = true

                        auth.signInWithEmailAndPassword(
                            email.trim(),
                            password
                        ).addOnCompleteListener { task ->
                            isLoading = false

                            if (task.isSuccessful) {
                                onLoginSuccess()
                            } else {
                                Toast.makeText(
                                    context,
                                    task.exception?.localizedMessage
                                        ?: "Error al iniciar sesión",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6750A4),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = if (isLoading) "Ingresando..." else "Ingresar",
                    fontSize = 16.sp
                )
            }


            Spacer(modifier = Modifier.height(18.dp))

            TextButton(
                onClick = onNavigateToRegister
            ) {
                Text(
                    text = "¿No tienes cuenta? Regístrate aquí",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Michael Fernandez - Tecsup",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}