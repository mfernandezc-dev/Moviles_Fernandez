package com.tecsup.authfirebasefernandez

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.graphics.Color

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val auth = remember { FirebaseAuth.getInstance() }
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "Crear cuenta ✨",
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
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

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))


        Button(
            onClick = {
                when {
                    email.isBlank() ||
                            password.isBlank() ||
                            confirmPassword.isBlank() -> {
                        Toast.makeText(
                            context,
                            "Complete todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    password != confirmPassword -> {
                        Toast.makeText(
                            context,
                            "Las contraseñas no coinciden",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    password.length < 6 -> {
                        Toast.makeText(
                            context,
                            "La contraseña debe tener mínimo 6 caracteres",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        isLoading = true

                        auth.createUserWithEmailAndPassword(
                            email.trim(),
                            password
                        ).addOnCompleteListener { task ->
                            isLoading = false

                            if (task.isSuccessful) {
                                onRegisterSuccess()
                            } else {
                                Toast.makeText(
                                    context,
                                    task.exception?.localizedMessage
                                        ?: "Error al registrar usuario",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
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
                text = if (isLoading) "Registrando..." else "Registrarse",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateBack
        ) {
            Text("← Volver a login")
        }
    }
}