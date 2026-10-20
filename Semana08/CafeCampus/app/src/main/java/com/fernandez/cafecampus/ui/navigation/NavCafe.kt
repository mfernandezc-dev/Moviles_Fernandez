package com.fernandez.cafecampus.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.fernandez.cafecampus.ui.cliente.LoginScreen
import com.fernandez.cafecampus.viewmodel.SesionViewModel
import androidx.compose.ui.unit.dp
import com.fernandez.cafecampus.viewmodel.MenuViewModel
import com.fernandez.cafecampus.ui.cliente.MenuScreen
import com.fernandez.cafecampus.ui.components.ClienteBottomBar
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.fernandez.cafecampus.ui.cliente.DetalleProductoScreen
import com.fernandez.cafecampus.viewmodel.PedidosClienteViewModel
import com.fernandez.cafecampus.ui.cliente.ConfirmarPedidoScreen
import com.fernandez.cafecampus.ui.cliente.MisPedidosScreen
import com.fernandez.cafecampus.ui.cliente.SeguimientoScreen
import com.fernandez.cafecampus.viewmodel.PanelAdminViewModel
import com.fernandez.cafecampus.viewmodel.PedidosAdminViewModel
import com.fernandez.cafecampus.ui.admin.PanelScreen
import com.fernandez.cafecampus.ui.admin.PedidosAdminScreen
import com.fernandez.cafecampus.ui.components.AdminBottomBar
import com.fernandez.cafecampus.model.EstadoPedido
import com.fernandez.cafecampus.ui.admin.AjustesScreen
import com.fernandez.cafecampus.ui.admin.DetallePedidoAdminScreen
import com.fernandez.cafecampus.viewmodel.MenuAdminViewModel
import com.fernandez.cafecampus.ui.admin.MenuAdminScreen
import com.fernandez.cafecampus.ui.admin.FormularioProductoScreen
import com.fernandez.cafecampus.ui.cliente.PerfilClienteScreen


private const val PERFIL_CLIENTE = "perfil_cliente"

@Composable
fun NavCafe(
    vm: SesionViewModel,
    menuVm: MenuViewModel,
    pedidosVm: PedidosClienteViewModel,
    panelAdminVm: PanelAdminViewModel,
    pedidosAdminVm: PedidosAdminViewModel,
    menuAdminVm: MenuAdminViewModel
) {

    val navController = rememberNavController()
    val perfil by vm.perfilActivo.collectAsState()
    val usuarioId by vm.usuarioId.collectAsState()

    LaunchedEffect(usuarioId) {
        pedidosVm.establecerCliente(usuarioId)
    }


    LaunchedEffect(perfil) {
        if (perfil == "") {
            val rutaActual = navController.currentDestination?.route

            if (rutaActual != Rutas.LOGIN &&
                rutaActual != Rutas.SPLASH) {

                navController.navigate(Rutas.LOGIN) {
                    popUpTo(navController.graph.id) {
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

        composable(Rutas.CONFIRMAR_PEDIDO) {

            val carrito by menuVm.carrito.collectAsState()
            val confirmando by pedidosVm.confirmando.collectAsState()
            val recibirPedidos by vm.recibirPedidos.collectAsState()
            val error by pedidosVm.error.collectAsState()
            val pedidoCreado by pedidosVm.pedidoCreado.collectAsState()


            LaunchedEffect(pedidoCreado) {
                pedidoCreado?.let { id ->
                    menuVm.vaciar()
                    pedidosVm.pedidoCreadoConsumido()

                    navController.navigate(
                        Rutas.seguimiento(id.toInt())
                    ) {
                        popUpTo(Rutas.CONFIRMAR_PEDIDO) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }

            ConfirmarPedidoScreen(
                items = carrito,
                confirmando = confirmando,
                recibirPedidos = recibirPedidos,
                error = error,
                onConfirmar = { hora, pago, nota ->
                    pedidosVm.confirmar(
                        items = carrito,
                        horaRecojo = hora,
                        pago = pago,
                        nota = nota,
                        recibirPedidos = recibirPedidos
                    )
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

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
                            navController.navigate(Rutas.MIS_PEDIDOS) {
                                launchSingleTop = true
                            }
                        },
                        onPerfil = {
                            navController.navigate(PERFIL_CLIENTE) { launchSingleTop = true }
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
                        },
                        onCarrito = {
                            navController.navigate(
                                Rutas.CONFIRMAR_PEDIDO
                            )
                        },
                        onCerrarSesion = {
                            menuVm.vaciar()
                            vm.cerrarSesion()
                        }
                    )

                }
            }
        }



        composable(PERFIL_CLIENTE) {
            Scaffold(
                bottomBar = {
                    ClienteBottomBar(
                        seleccionado = "perfil",
                        onMenu = {
                            navController.navigate(Rutas.MENU_CLIENTE) {
                                popUpTo(Rutas.MENU_CLIENTE) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        onPedidos = {
                            navController.navigate(Rutas.MIS_PEDIDOS) {
                                launchSingleTop = true
                            }
                        },
                        onPerfil = {}
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    PerfilClienteScreen(
                        onCerrarSesion = {
                            menuVm.vaciar()
                            vm.cerrarSesion()
                        },
                        onVolver = { navController.popBackStack() }
                    )
                }
            }
        }

        composable(Rutas.PANEL_ADMIN) {

            val nuevos by panelAdminVm.nuevos.collectAsState()
            val preparando by panelAdminVm.preparando.collectAsState()
            val listos by panelAdminVm.listos.collectAsState()
            val ventasHoy by panelAdminVm.ventasHoy.collectAsState()
            val pedidosNuevos by panelAdminVm.pedidosNuevos.collectAsState()

            Scaffold(
                bottomBar = {
                    AdminBottomBar(
                        seleccionado = "panel",
                        onPanel = {},
                        onPedidos = {
                            navController.navigate(Rutas.PEDIDOS_ADMIN) {
                                launchSingleTop = true
                            }
                        },
                        onMenu = {
                            navController.navigate(Rutas.MENU_ADMIN) {
                                launchSingleTop = true
                            }
                        },
                        onAjustes = {
                            navController.navigate(Rutas.AJUSTES_ADMIN) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            ) { innerPadding ->

                Box(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    PanelScreen(
                        nuevos = nuevos,
                        preparando = preparando,
                        listos = listos,
                        ventasHoy = ventasHoy,
                        pedidosNuevos = pedidosNuevos,
                        // Contador temporal de pedidos activos
                        cantidadPedidosHoy = nuevos + preparando + listos,
                        onPreparar = { registro ->
                            pedidosAdminVm.avanzar(registro)
                        },

                        onVerPedidos = {
                            pedidosAdminVm.seleccionarEstado(
                                EstadoPedido.RECIBIDO
                            )

                            navController.navigate(Rutas.PEDIDOS_ADMIN) {
                                launchSingleTop = true
                            }
                        },
                        // Cerrar sesión ahora está en Ajustes
                    )
                }
            }
        }


        composable(Rutas.MENU_ADMIN) {

            val productos by menuAdminVm.productos.collectAsState()

            Scaffold(
                bottomBar = {
                    AdminBottomBar(
                        seleccionado = "menu",
                        onPanel = {
                            navController.navigate(Rutas.PANEL_ADMIN) {
                                popUpTo(Rutas.PANEL_ADMIN) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        },
                        onPedidos = {
                            navController.navigate(Rutas.PEDIDOS_ADMIN) {
                                launchSingleTop = true
                            }
                        },
                        onMenu = {},
                        onAjustes = {
                            navController.navigate(Rutas.AJUSTES_ADMIN) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            ) { innerPadding ->

                Box(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    MenuAdminScreen(
                        productos = productos,
                        onAgregar = {
                            navController.navigate(Rutas.NUEVO_PRODUCTO)
                        },
                        onEditar = { productoId ->
                            navController.navigate(
                                Rutas.editarProducto(productoId)
                            )
                        },
                        onEliminar = { producto ->
                            menuAdminVm.eliminar(producto)
                        },
                        onDisponibilidad = { producto ->
                            menuAdminVm.alternarDisponibilidad(producto)
                        }
                    )
                }
            }
        }


        composable(Rutas.NUEVO_PRODUCTO) {

            FormularioProductoScreen(
                producto = null,
                onGuardar = { producto ->
                    menuAdminVm.guardar(producto)
                    navController.popBackStack()
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }


        composable(
            route = Rutas.EDITAR_PRODUCTO,
            arguments = listOf(
                navArgument("productoId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val productoId =
                backStackEntry.arguments?.getInt("productoId") ?: 0

            val productos by menuAdminVm.productos.collectAsState()

            val producto = productos.find {
                it.id == productoId
            }

            if (producto != null) {
                FormularioProductoScreen(
                    producto = producto,
                    onGuardar = { actualizado ->
                        menuAdminVm.guardar(actualizado)
                        navController.popBackStack()
                    },
                    onVolver = {
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



        composable(Rutas.PEDIDOS_ADMIN) {

            val estado by pedidosAdminVm.estadoSeleccionado.collectAsState()
            val pedidos by pedidosAdminVm.pedidos.collectAsState()

            Scaffold(
                bottomBar = {
                    AdminBottomBar(
                        seleccionado = "pedidos",
                        onPanel = {
                            navController.navigate(Rutas.PANEL_ADMIN) {
                                popUpTo(Rutas.PANEL_ADMIN) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        },
                        onPedidos = {},
                        onMenu = {
                            navController.navigate(Rutas.MENU_ADMIN) {
                                launchSingleTop = true
                            }
                        },
                        onAjustes = {
                            navController.navigate(Rutas.AJUSTES_ADMIN) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            ) { innerPadding ->

                Box(
                    modifier = Modifier.padding(innerPadding)
                ) {

                    PedidosAdminScreen(
                        estadoSeleccionado = estado,
                        pedidos = pedidos,

                        onEstado = { seleccionado ->
                            pedidosAdminVm.seleccionarEstado(seleccionado)
                        },

                        onAvanzar = { registro ->
                            pedidosAdminVm.avanzar(registro)
                        },

                        onCancelar = { registro ->
                            pedidosAdminVm.cancelar(registro)
                        },

                        onDetalle = { pedidoId ->
                            navController.navigate(
                                Rutas.detallePedidoAdmin(pedidoId)
                            )
                        },

                        onVolver = {
                            navController.navigate(Rutas.PANEL_ADMIN) {
                                popUpTo(Rutas.PANEL_ADMIN) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    )

                }
            }
        }


        composable(Rutas.AJUSTES_ADMIN) {

            val modoOscuro by vm.modoOscuro.collectAsState()
            val notificaciones by vm.notificaciones.collectAsState()
            val recibirPedidos by vm.recibirPedidos.collectAsState()

            Scaffold(
                bottomBar = {
                    AdminBottomBar(
                        seleccionado = "ajustes",
                        onPanel = {
                            navController.navigate(Rutas.PANEL_ADMIN) {
                                popUpTo(Rutas.PANEL_ADMIN) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        },
                        onPedidos = {
                            navController.navigate(Rutas.PEDIDOS_ADMIN) {
                                launchSingleTop = true
                            }
                        },
                        onMenu = {
                            navController.navigate(Rutas.MENU_ADMIN) {
                                launchSingleTop = true
                            }
                        },
                        onAjustes = {}
                    )
                }
            ) { innerPadding ->

                Box(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    AjustesScreen(
                        modoOscuro = modoOscuro,
                        notificaciones = notificaciones,
                        recibirPedidos = recibirPedidos,
                        onModoOscuro = vm::cambiarModoOscuro,
                        onNotificaciones = vm::cambiarNotificaciones,
                        onRecibirPedidos = vm::cambiarRecibirPedidos,
                        onCambiarPerfil = {
                            menuVm.vaciar()
                            vm.cambiarPerfil()
                        },
                        onCerrarSesion = {
                            menuVm.vaciar()
                            vm.cerrarSesion()
                        },
                        onVolver = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }



        composable(
            route = Rutas.DETALLE_PEDIDO_ADMIN,
            arguments = listOf(
                navArgument("pedidoId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val pedidoId =
                backStackEntry.arguments?.getInt("pedidoId") ?: 0

            // Reutilizamos la consulta del pedido por ID.
            val flujoPedido = remember(pedidoId) {
                pedidosVm.pedido(pedidoId)
            }

            val registro by flujoPedido.collectAsState()

            if (perfil == "ADMIN" && registro != null) {
                val pedidoActual = registro!!

                DetallePedidoAdminScreen(
                    registro = pedidoActual,
                    onVolver = {
                        navController.popBackStack()
                    },
                    onAvanzar = {
                        pedidosAdminVm.avanzar(pedidoActual)
                    },
                    onCancelar = {
                        pedidosAdminVm.cancelar(pedidoActual)
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Pedido no disponible")
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


        composable(
            route = Rutas.SEGUIMIENTO,
            arguments = listOf(
                navArgument("pedidoId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val pedidoId =
                backStackEntry.arguments?.getInt("pedidoId") ?: 0

            val flujoPedido = remember(pedidoId) {
                pedidosVm.pedido(pedidoId)
            }

            val registro by flujoPedido.collectAsState()
            val productos by menuVm.productos.collectAsState()

            // Solo mostrar pedidos del usuario autenticado
            val pedidoVisible = registro?.takeIf {
                it.pedido.clienteId == usuarioId
            }


            if (pedidoVisible != null) {
                val pedido = pedidoVisible.pedido

                Scaffold(
                    bottomBar = {
                        ClienteBottomBar(
                            seleccionado = "pedidos",
                            onMenu = {
                                navController.navigate(Rutas.MENU_CLIENTE) {
                                    popUpTo(Rutas.MENU_CLIENTE) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            },
                            onPedidos = {
                                navController.navigate(Rutas.MIS_PEDIDOS) {
                                    launchSingleTop = true
                                }
                            },
                            onPerfil = {
                                navController.navigate(PERFIL_CLIENTE) { launchSingleTop = true }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        val resumenReal = pedidoVisible.detalles.joinToString(" · ") { detalle ->
                            val nombreProducto = productos
                                .find { it.id == detalle.productoId }
                                ?.nombre ?: "Producto"

                            "${detalle.cantidad} x $nombreProducto"
                        }
                        SeguimientoScreen(
                            pedidoId = pedido.id,
                            estado = pedido.estado,
                            horaRecojo = pedido.horaRecojo,
                            total = pedido.total,
                            resumen = resumenReal,
                            onVolver = {
                                navController.popBackStack()
                            }
                        )
                    }
                }

            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cargando pedido o pedido no disponible")
                }
            }

        }


        composable(Rutas.MIS_PEDIDOS) {

            val pedidos by pedidosVm.misPedidos.collectAsState()
            val productos by menuVm.productos.collectAsState()

            Scaffold(
                bottomBar = {
                    ClienteBottomBar(
                        seleccionado = "pedidos",
                        onMenu = {
                            navController.navigate(Rutas.MENU_CLIENTE) {
                                popUpTo(Rutas.MENU_CLIENTE) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        },
                        onPedidos = {
                            // Ya estamos en Mis pedidos
                        },
                        onPerfil = {
                            navController.navigate(PERFIL_CLIENTE) { launchSingleTop = true }
                        }
                    )
                }
            ) { innerPadding ->

                Box(
                    modifier = Modifier.padding(innerPadding)
                ) {

                    MisPedidosScreen(
                        pedidos = pedidos,
                        productos = productos,
                        onPedido = { pedidoId ->
                            navController.navigate(
                                Rutas.seguimiento(pedidoId)
                            )
                        },
                        onVolver = {
                            navController.popBackStack()
                        }
                    )

                }
            }
        }



    }
}
