package com.fernandez.cafecampus.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.fernandez.cafecampus.ui.cliente.LoginScreen
import com.fernandez.cafecampus.viewmodel.SesionViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.fernandez.cafecampus.viewmodel.MenuViewModel
import com.fernandez.cafecampus.ui.cliente.MenuScreen
import com.fernandez.cafecampus.ui.components.ClienteBottomBar
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.fernandez.cafecampus.ui.cliente.DetalleProductoScreen

@Composable
fun NavCafe(
    vm: SesionViewModel,
    menuVm: MenuViewModel
) {

    val navController = rememberNavController()
    val perfil by vm.perfilActivo.collectAsState()

    LaunchedEffect(perfil) {
        if (perfil == "") {
            val rutaActual = navController.currentDestination?.route

            if (rutaActual == Rutas.MENU_CLIENTE ||
                rutaActual == Rutas.PANEL_ADMIN) {

                navController.navigate(Rutas.LOGIN) {
                    popUpTo(Rutas.MENU_CLIENTE) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    fun destino(rol: String?): String = when (rol) {
        "CLIENTE" -> Rutas.MENU_CLIENTE
        "ADMIN" -> Rutas.PANEL_ADMIN
        else -> Rutas.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH
    ) {

        composable(Rutas.SPLASH) {
            LaunchedEffect(perfil) {
                if (perfil != null) {
                    navController.navigate(destino(perfil)) {
                        popUpTo(Rutas.SPLASH) {
                            inclusive = true
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        composable(Rutas.LOGIN) {
            LaunchedEffect(perfil) {
                if (perfil == "CLIENTE" || perfil == "ADMIN") {
                    navController.navigate(destino(perfil)) {
                        popUpTo(Rutas.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            }

            LoginScreen(vm)
        }


        composable(Rutas.MENU_CLIENTE) {

            Scaffold(
                bottomBar = {
                    ClienteBottomBar(
                        seleccionado = "menu",
                        onMenu = {
                            // Ya estamos en el menú
                        },
                        onPedidos = {
                            // Se implementará en el Paso 8
                        },
                        onPerfil = {
                            // Pantalla de perfil pendiente
                        }
                    )
                }
            ) { innerPadding ->

                Box(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    MenuScreen(
                        vm = menuVm,
                        onProducto = { productoId ->
                            navController.navigate(
                                Rutas.detalleProducto(productoId)
                            )
                        }
                    )
                }
            }
        }


        composable(Rutas.PANEL_ADMIN) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text("Panel de administración de CafeCampus")

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        vm.cerrarSesion()
                    }
                ) {
                    Text("Cerrar sesión")
                }
            }
        }


        composable(
            route = Rutas.DETALLE_PRODUCTO,
            arguments = listOf(
                navArgument("productoId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val productoId =
                backStackEntry.arguments?.getInt("productoId") ?: 0

            val productos by menuVm.productos.collectAsState()

            val producto = productos.find {
                it.id == productoId
            }

            if (producto != null) {
                DetalleProductoScreen(
                    producto = producto,
                    onVolver = {
                        navController.popBackStack()
                    },
                    onAgregar = { seleccionado, cantidad, nota ->
                        menuVm.agregar(
                            producto = seleccionado,
                            cantidad = cantidad,
                            nota = nota
                        )

                        navController.popBackStack()
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

    }
}