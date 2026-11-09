package com.fernandez.catalogo.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fernandez.catalogo.AppContainer
import com.fernandez.catalogo.domain.model.Rol
import com.fernandez.catalogo.domain.model.Sesion
import com.fernandez.catalogo.ui.UiState
import com.fernandez.catalogo.ui.admin.AdminScreen
import com.fernandez.catalogo.ui.admin.AdminViewModel
import com.fernandez.catalogo.ui.components.Cargando
import com.fernandez.catalogo.ui.components.ErrorView
import com.fernandez.catalogo.ui.detalle.DetalleScreen
import com.fernandez.catalogo.ui.detalle.DetalleViewModel
import com.fernandez.catalogo.ui.favoritos.FavoritosScreen
import com.fernandez.catalogo.ui.favoritos.FavoritosViewModel
import com.fernandez.catalogo.ui.lista.ListaScreen
import com.fernandez.catalogo.ui.lista.ListaViewModel
import com.fernandez.catalogo.ui.login.LoginScreen
import com.fernandez.catalogo.ui.login.LoginViewModel
import kotlinx.coroutines.flow.StateFlow

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
    var primeraReanudacion by remember { mutableStateOf(true) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (primeraReanudacion) {
            primeraReanudacion = false
        } else {
            loginViewModel.revalidarPerfil()
        }
    }

    when (val estado = estadoSesion) {
        UiState.Cargando -> Cargando(modifier = modifier)
        is UiState.Error -> ErrorPerfil(
            mensaje = estado.mensaje,
            onReintentar = loginViewModel::revalidarPerfil,
            onCerrarSesion = loginViewModel::cerrarSesion,
            modifier = modifier
        )
        is UiState.Exito -> {
            // Cada cambio de sesión descarta el historial de la sesión anterior.
            key(estado.datos?.uid, estado.datos?.rol) {
                if (estado.datos == null) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login", modifier = modifier) {
                        composable("login") {
                            LoginScreen(viewModel = loginViewModel)
                        }
                    }
                } else {
                    val rol = estado.datos.rol
                    if (rol == null) {
                        ErrorPerfil(
                            mensaje = "El perfil de acceso todavía no está verificado.",
                            onReintentar = loginViewModel::revalidarPerfil,
                            onCerrarSesion = loginViewModel::cerrarSesion,
                            modifier = modifier
                        )
                    } else {
                        CatalogoNav(
                            listaViewModel = listaViewModel,
                            rol = rol,
                            sesionAutorizada = loginViewModel.sesion,
                            onCerrarSesion = loginViewModel::cerrarSesion,
                            modifier = modifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogoNav(
    listaViewModel: ListaViewModel,
    rol: Rol,
    sesionAutorizada: StateFlow<UiState<Sesion?>>,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (rol == Rol.ADMIN) "admin" else "lista",
        modifier = modifier
    ) {
        if (rol == Rol.ADMIN) {
            composable("admin") { backStackEntry ->
                val autorizacion by sesionAutorizada.collectAsStateWithLifecycle()
                val vigente = (autorizacion as? UiState.Exito)?.datos?.rol == Rol.ADMIN
                if (vigente) {
                    val factory = remember(sesionAutorizada) {
                        object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                require(modelClass.isAssignableFrom(AdminViewModel::class.java)) {
                                    "ViewModel no compatible: ${modelClass.name}"
                                }
                                @Suppress("UNCHECKED_CAST")
                                return AdminViewModel(AppContainer.itemRepository, sesionAutorizada) as T
                            }
                        }
                    }
                    val adminViewModel: AdminViewModel = viewModel(
                        viewModelStoreOwner = backStackEntry,
                        factory = factory
                    )
                    AdminScreen(
                        viewModel = adminViewModel,
                        onVerCatalogo = { navController.navigate("lista") { launchSingleTop = true } },
                        onCerrarSesion = onCerrarSesion
                    )
                } else {
                    Text("Acceso no autorizado.")
                }
            }
        }

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

@Composable
private fun ErrorPerfil(
    mensaje: String,
    onReintentar: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        ErrorView(mensaje = mensaje, onReintentar = onReintentar, modifier = Modifier.weight(1f))
        TextButton(
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Cerrar sesión")
        }
    }
}
