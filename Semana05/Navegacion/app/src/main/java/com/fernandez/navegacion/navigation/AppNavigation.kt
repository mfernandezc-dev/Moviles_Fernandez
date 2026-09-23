package com.fernandez.navegacion.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fernandez.navegacion.screens.DetailScreen
import com.fernandez.navegacion.screens.HomeScreen
import com.fernandez.navegacion.screens.ListScreen
import com.fernandez.navegacion.screens.LoginScreen
import com.fernandez.navegacion.screens.ProfileScreen
import com.fernandez.navegacion.screens.ProfileSettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // Pantalla de Inicio de Sesión
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        // Pantalla de Inicio / Bienvenida
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        // Pantalla del Directorio de Alumnos
        composable(Screen.List.route) {
            ListScreen(navController)
        }

        // Pantalla de Expediente Académico
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

        // Pantalla de Configuración de Perfil
        composable(Screen.ProfileSettings.route) {
            ProfileSettingsScreen(navController)
        }

        // Ruta con argumento (Detalle de Alumno)
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 0
            DetailScreen(navController, itemId)
        }
    }
}
