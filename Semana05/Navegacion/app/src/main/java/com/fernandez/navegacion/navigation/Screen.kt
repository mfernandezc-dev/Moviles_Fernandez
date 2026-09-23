package com.fernandez.navegacion.navigation

sealed class Screen(val route: String) {

    // Pantalla de inicio de sesión
    object Login : Screen(route = "login")

    // Pantalla de inicio / bienvenida
    object Home : Screen(route = "home")

    // Pantalla de directorio de alumnos
    object List : Screen(route = "list")

    // Pantalla de expediente académico
    object Profile : Screen(route = "profile")

    // Pantalla de configuración de perfil
    object ProfileSettings : Screen(route = "profile_settings")

    // Ruta con argumento para detalle de alumno
    object Detail : Screen(route = "detail/{itemId}") {
        fun createRoute(itemId: Int): String {
            return "detail/$itemId"
        }
    }
}
