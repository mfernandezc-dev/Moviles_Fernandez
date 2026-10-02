Prompt 01

"Actúa como un desarrollador experto en Jetpack Compose y Material 3 para Android. Necesito implementar la Fase de Mejora con IA para la aplicación TECSUP Store basada en nuestro código existente.

Por favor, genera el código con los siguientes requisitos:

Mejora de Modelo:

Modifica/extiende la data class Producto para incluir un campo booleano esFavorito: Boolean = false.

DropdownMenu Contextual en TarjetaProducto:

Mantiene el ícono de 3 puntos (⋮).

Al presionar, muestra mínimo 3 opciones con sus respectivos leadingIcon:

"Favorito" / "Quitar de favoritos": al presionar alterna el estado de favorito del producto.

"Compartir": icono Share.

"Reportar": icono Warning / Report.

(Mantener opcionalmente "Eliminar").

NavigationDrawer Completo con Badge:

Agrega los destinos requeridos: Inicio, Mis pedidos, Favoritos, Perfil y Cerrar sesión.

El encabezado del drawer debe mostrar un avatar circular con iniciales (ej. "MF" para Michael Fernandez) y los datos básicos del usuario ("Michael Fernandez", "michael.fernandez@tecsup.edu.pe").

Mejora Obligatoria: Agrega un Badge con contador (usando Badge { Text("") } dentro del parámetro badge de NavigationDrawerItem) en la opción "Favoritos" que muestre en tiempo real la cantidad de productos marcados como favoritos desde el DropdownMenu.

El ítem activo debe ser resaltado visualmente.*

Prompt 02

Actúa como un desarrollador experto en Jetpack Compose y Material 3. Necesito modificar la aplicación TECSUP Store para que su interfaz coincida exactamente con la maqueta de referencia del laboratorio.

Aplica los siguientes cambios específicos en el código:

1. **TopAppBar y Encabezado de Sección:**
   - La barra superior (TopAppBar) debe tener un fondo de color morado oscuro (#6750A4 o #4A148C) con texto en blanco.
   - Debe mostrar la marca "TECSUP Store" como título principal y un subtítulo debajo que indique la sección (ejemplo: "Mas vendidos" o "Mis pedidos").

2. **Diseño de Tarjetas de Producto (TarjetaProducto):**
   - Cada tarjeta debe tener bordes suavemente redondeados y fondo morado/gris claro (#F3EDF7).
   - A la izquierda del texto debe incluir una caja contenedora cuadrada/redondeada con un ícono de bolsa de compras (Icons.Default.ShoppingBag o Icons.Default.ShoppingBasket) en color morado.
   - A la derecha debe ubicarse el ícono de 3 puntos verticales (⋮).
   - El precio debe mostrarse debajo del nombre del producto (ej: "S/ 89.00").

3. **DropdownMenu Contextual:**
   - Debe desplegarse sobre o junto al ícono de 3 puntos con un fondo blanco limpio y bordes redondeados.
   - Contiene exactamente 3 opciones separadas por divisores horizontales delgados:
     1. "Favoritos" (ícono Favorite o FavoriteBorder).
     2. "Compartir" (ícono Share).
     3. "Reportar" (ícono Warning o OutlinedFlag).
   - Al tocar "Favoritos", altera el estado esFavorito del producto correspondiente.

4. **NavigationDrawer y Encabezado:**
   - **Header:** Muestra un círculo/avatar de fondo morado claro con las iniciales en mayúscula (ej: "MR" o "MF") en texto en negrita morado oscuro. Al lado, el nombre completo del usuario y su correo institucional (michael.fernandez@tecsup.edu.pe).
   - **Destinos:** 5 opciones principales: "Inicio", "Mis pedidos", "Favoritos", "Perfil", "Cerrar sesion".
   - Cada opción debe llevar un ícono circular/contorneado a la izquierda.
   - **Ítem Activo:** El destino activo ("Mis pedidos" u otro) debe mostrar un fondo morado claro pastel con esquinas completamente redondeadas.
   - **Badge de Favoritos (Mejora Obligatoria):** En la opción "Favoritos" del drawer, debe aparecer un Badge con el contador numérico que se actualiza automáticamente según la cantidad de productos marcados como favoritos desde el DropdownMenu.