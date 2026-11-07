package com.fernandez.catalogo.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fernandez.catalogo.AppContainer
import com.fernandez.catalogo.ui.UiState
import com.fernandez.catalogo.ui.components.Cargando
import com.fernandez.catalogo.ui.detalle.DetalleScreen
import com.fernandez.catalogo.ui.detalle.DetalleViewModel
import com.fernandez.catalogo.ui.favoritos.FavoritosScreen
import com.fernandez.catalogo.ui.favoritos.FavoritosViewModel
import com.fernandez.catalogo.ui.lista.ListaScreen
import com.fernandez.catalogo.ui.lista.ListaViewModel
import com.fernandez.catalogo.ui.login.LoginScreen
import com.fernandez.catalogo.ui.login.LoginViewModel

@Composable
fun AppNav(
    listaViewModel: ListaViewModel,
    modifier: Modifier = Modifier
) {
    val factory = remember {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                    "ViewModel no compatible: ${modelClass.name}"
                }
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(AppContainer.authRepository) as T
            }
        }
    }
    val loginViewModel: LoginViewModel = viewModel(factory = factory)
    val estadoSesion by loginViewModel.sesion.collectAsStateWithLifecycle()

    when (val estado = estadoSesion) {
        UiState.Cargando -> Cargando(modifier = modifier)
        is UiState.Error -> Text(estado.mensaje, modifier = modifier.padding(16.dp))
        is UiState.Exito -> {
            // Cada cambio de sesión descarta el historial de la sesión anterior.
            key(estado.datos?.uid) {
                if (estado.datos == null) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login", modifier = modifier) {
                        composable("login") {
                            LoginScreen(viewModel = loginViewModel)
                        }
                    }
                } else {
                    CatalogoNav(
                        listaViewModel = listaViewModel,
                        onCerrarSesion = loginViewModel::cerrarSesion,
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@Composable
private fun CatalogoNav(
    listaViewModel: ListaViewModel,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "lista",
        modifier = modifier
    ) {
        composable("lista") {
            ListaScreen(
                viewModel = listaViewModel,
                onSeleccionarItem = { id ->
                    navController.navigate("detalle/$id") {
                        launchSingleTop = true
                    }
                },
                onVerFavoritos = {
                    navController.navigate("favoritos") {
                        launchSingleTop = true
                    }
                },
                onCerrarSesion = onCerrarSesion
            )
        }

        composable("favoritos") { backStackEntry ->
            val factory = remember {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        require(modelClass.isAssignableFrom(FavoritosViewModel::class.java)) {
                            "ViewModel no compatible: ${modelClass.name}"
                        }
                        @Suppress("UNCHECKED_CAST")
                        return FavoritosViewModel(AppContainer.itemRepository) as T
                    }
                }
            }
            val favoritosViewModel: FavoritosViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = factory
            )

            FavoritosScreen(
                viewModel = favoritosViewModel,
                onSeleccionarItem = { id ->
                    navController.navigate("detalle/$id") {
                        launchSingleTop = true
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = "detalle/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            val factory = remember(id) {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        require(modelClass.isAssignableFrom(DetalleViewModel::class.java)) {
                            "ViewModel no compatible: ${modelClass.name}"
                        }
                        @Suppress("UNCHECKED_CAST")
                        return DetalleViewModel(AppContainer.itemRepository, id) as T
                    }
                }
            }
            val detalleViewModel: DetalleViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = factory
            )

            DetalleScreen(
                viewModel = detalleViewModel,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
