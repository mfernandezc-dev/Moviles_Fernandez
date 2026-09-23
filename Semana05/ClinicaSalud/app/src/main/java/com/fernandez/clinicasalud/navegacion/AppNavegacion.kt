package com.fernandez.clinicasalud.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fernandez.clinicasalud.screens.InicioScreen
import com.fernandez.clinicasalud.screens.PerfilMedicosScreen

@Composable
fun AppNavegacion() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {

        composable("inicio") {

            InicioScreen(
                navController = navController
            )

        }


        composable(
            route = "perfil/{nombre}"
        ) { backStackEntry ->

            val nombre =
                backStackEntry.arguments?.getString("nombre") ?: ""

            PerfilMedicosScreen(
                nombre = nombre
            )

        }

    }

}