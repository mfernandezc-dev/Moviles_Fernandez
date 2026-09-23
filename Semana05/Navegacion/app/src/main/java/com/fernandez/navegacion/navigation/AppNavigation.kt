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
import com.fernandez.navegacion.screens.ProfileScreen

@Composable
fun AppNavigation() {

    // Crea y mantiene el controlador de navegación
    val navController = rememberNavController()

    // Contenedor del grafo de navegación
    // startDestination define la pantalla inicial
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        // Pantalla inicial
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }


        // Pantalla de lista
        composable(Screen.List.route) {
            ListScreen(navController)
        }


        // Pantalla de perfil
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }


        // Ruta con argumento
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