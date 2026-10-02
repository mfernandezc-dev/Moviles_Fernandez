package com.fernandez.lab04carritotecsup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCarrito() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var itemSeleccionado by remember { mutableStateOf("Inicio") }

    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }

    // Productos iniciales de prueba para coincidir con la maqueta
    val productos = remember {
        mutableStateListOf(
            Producto("Mochila Tecsup", 89.00, 1, esFavorito = true),
            Producto("Audífonos Bluetooth", 129.50, 1, esFavorito = false)
        )
    }

    val cantidadFavoritos = productos.count { it.esFavorito }
    val subtotal = productos.sumOf { it.precio * it.cantidad }
    val igv = subtotal * 0.18
    val total = subtotal + igv

    // Estilos de colores personalizados para NavigationDrawer
    val drawerItemColors = NavigationDrawerItemDefaults.colors(
        selectedContainerColor = Color(0xFFE8DEF8),
        selectedIconColor = Color(0xFF21005D),
        selectedTextColor = Color(0xFF21005D),
        unselectedIconColor = Color(0xFF49454F),
        unselectedTextColor = Color(0xFF1D1B20)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Header del Drawer: Avatar circular e información de usuario al lado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3EDF7))
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = Color(0xFFE8DEF8)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "MF",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color(0xFF21005D),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Michael Fernandez",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "michael.fernandez@tecsup.edu.pe",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                // Destinos del NavigationDrawer con ícono circular/contorneado
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Inicio") },
                    selected = itemSeleccionado == "Inicio",
                    onClick = {
                        itemSeleccionado = "Inicio"
                        scope.launch { drawerState.close() }
                    },
                    colors = drawerItemColors,
                    shape = CircleShape,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null) },
                    label = { Text("Mis pedidos") },
                    selected = itemSeleccionado == "Mis pedidos",
                    onClick = {
                        itemSeleccionado = "Mis pedidos"
                        scope.launch { drawerState.close() }
                    },
                    colors = drawerItemColors,
                    shape = CircleShape,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                // Opción Favoritos con Badge obligatorio en tiempo real
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            if (itemSeleccionado == "Favoritos") Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null
                        )
                    },
                    label = { Text("Favoritos") },
                    badge = {
                        Badge(
                            containerColor = Color(0xFF6750A4),
                            contentColor = Color.White
                        ) {
                            Text("$cantidadFavoritos")
                        }
                    },
                    selected = itemSeleccionado == "Favoritos",
                    onClick = {
                        itemSeleccionado = "Favoritos"
                        scope.launch { drawerState.close() }
                    },
                    colors = drawerItemColors,
                    shape = CircleShape,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Perfil") },
                    selected = itemSeleccionado == "Perfil",
                    onClick = {
                        itemSeleccionado = "Perfil"
                        scope.launch { drawerState.close() }
                    },
                    colors = drawerItemColors,
                    shape = CircleShape,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.LightGray.copy(alpha = 0.5f)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                    label = { Text("Cerrar sesion") },
                    selected = itemSeleccionado == "Cerrar sesion",
                    onClick = {
                        itemSeleccionado = "Cerrar sesion"
                        scope.launch { drawerState.close() }
                    },
                    colors = drawerItemColors,
                    shape = CircleShape,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                // TopAppBar con fondo morado oscuro y subtítulo de sección
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF6750A4),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    title = {
                        Column {
                            Text(
                                text = "TECSUP Store",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = when (itemSeleccionado) {
                                    "Inicio" -> "Más vendidos"
                                    "Mis pedidos" -> "Mis pedidos"
                                    "Favoritos" -> "Mis favoritos"
                                    "Perfil" -> "Mi perfil"
                                    "Cerrar sesion" -> "Cerrar sesión"
                                    else -> "Sección principal"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú Principal",
                                tint = Color.White
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (itemSeleccionado) {
                    "Inicio" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre del producto") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = precio,
                                    onValueChange = { precio = it },
                                    label = { Text("Precio") },
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = cantidad,
                                    onValueChange = { cantidad = it },
                                    label = { Text("Cantidad") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val precioNum = precio.toDoubleOrNull() ?: 0.0
                                    val cantidadNum = cantidad.toIntOrNull() ?: 0
                                    if (nombre.isNotBlank() && precioNum > 0 && cantidadNum > 0) {
                                        productos.add(
                                            Producto(
                                                nombre = nombre,
                                                precio = precioNum,
                                                cantidad = cantidadNum
                                            )
                                        )
                                        nombre = ""
                                        precio = ""
                                        cantidad = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("AGREGAR AL CARRITO", color = Color.White)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (productos.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Tu carrito está vacío",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Agrega tu primer producto arriba",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(productos) { producto ->
                                        TarjetaProducto(
                                            producto = producto,
                                            onToggleFavorito = {
                                                val index = productos.indexOf(producto)
                                                if (index != -1) {
                                                    productos[index] = producto.copy(
                                                        esFavorito = !producto.esFavorito
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF3EDF7)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Total de ítems:")
                                        Text("${productos.sumOf { it.cantidad }}")
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Subtotal:")
                                        Text("S/ %.2f".format(subtotal))
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("IGV (18%):")
                                        Text("S/ %.2f".format(igv))
                                    }

                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        color = Color.LightGray.copy(alpha = 0.5f)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "TOTAL",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "S/ %.2f".format(total),
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color(0xFF6750A4),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "Mis pedidos" -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color(0xFF6750A4)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Mis Pedidos",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Aquí podrás ver el historial de todas tus compras.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    "Favoritos" -> {
                        val listaFavoritos = productos.filter { it.esFavorito }
                        if (listaFavoritos.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = Color(0xFF6750A4)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No tienes favoritos aún",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Agrega productos a favoritos usando el menú de 3 puntos (⋮).",
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(listaFavoritos) { producto ->
                                    TarjetaProducto(
                                        producto = producto,
                                        onToggleFavorito = {
                                            val index = productos.indexOf(producto)
                                            if (index != -1) {
                                                productos[index] = producto.copy(
                                                    esFavorito = !producto.esFavorito
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    "Perfil" -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7))
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Surface(
                                        modifier = Modifier.size(80.dp),
                                        shape = CircleShape,
                                        color = Color(0xFFE8DEF8)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "MF",
                                                style = MaterialTheme.typography.headlineMedium,
                                                color = Color(0xFF21005D),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Michael Fernandez",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "michael.fernandez@tecsup.edu.pe",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Rol: Estudiante TECSUP",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Programa: Desarrollo de Software",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    "Cerrar sesion" -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Sesión cerrada",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { itemSeleccionado = "Inicio" },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                                ) {
                                    Text("Iniciar sesión", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaProducto(
    producto: Producto,
    onToggleFavorito: () -> Unit
) {
    var menuExpandido by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3EDF7)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Caja contenedora cuadrada/redondeada con ícono de bolsa de compras
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE8DEF8)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Bolsa de compras",
                        tint = Color(0xFF6750A4),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Información del producto: Nombre y Precio debajo
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = producto.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (producto.esFavorito) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorito",
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "S/ %.2f".format(producto.precio),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6750A4)
                )
            }

            // Ícono de 3 puntos (⋮) a la derecha con DropdownMenu desplegable
            Box {
                IconButton(onClick = { menuExpandido = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    // Opción 1: Favoritos
                    DropdownMenuItem(
                        text = { Text("Favoritos") },
                        leadingIcon = {
                            Icon(
                                imageVector = if (producto.esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (producto.esFavorito) Color(0xFF6750A4) else LocalContentColor.current
                            )
                        },
                        onClick = {
                            menuExpandido = false
                            onToggleFavorito()
                        }
                    )

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                    // Opción 2: Compartir
                    DropdownMenuItem(
                        text = { Text("Compartir") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            menuExpandido = false
                        }
                    )

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                    // Opción 3: Reportar
                    DropdownMenuItem(
                        text = { Text("Reportar") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            menuExpandido = false
                        }
                    )
                }
            }
        }
    }
}
