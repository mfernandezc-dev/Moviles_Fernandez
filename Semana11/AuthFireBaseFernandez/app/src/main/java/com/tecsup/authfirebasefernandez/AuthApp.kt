package com.tecsup.authfirebasefernandez

import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth

object Destinations {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
}

@Composable
fun AuthApp() {
    val auth = remember { FirebaseAuth.getInstance() }

    var currentScreen by remember {
        mutableStateOf(
            if (auth.currentUser != null) {
                Destinations.HOME
            } else {
                Destinations.LOGIN
            }
        )
    }

    when (currentScreen) {
        Destinations.LOGIN -> {
            LoginScreen(
                onNavigateToRegister = {
                    currentScreen = Destinations.REGISTER
                },
                onLoginSuccess = {
                    currentScreen = Destinations.HOME
                }
            )
        }

        Destinations.REGISTER -> {
            RegisterScreen(
                onRegisterSuccess = {
                    currentScreen = Destinations.HOME
                },
                onNavigateBack = {
                    currentScreen = Destinations.LOGIN
                }
            )
        }

        Destinations.HOME -> {
            HomeScreen(
                onLogout = {
                    currentScreen = Destinations.LOGIN
                }
            )
        }
    }
}