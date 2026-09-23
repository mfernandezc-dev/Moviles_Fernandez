# Navegación - Jetpack Compose

## Descripción

Aplicación desarrollada para el laboratorio de la Semana 05, donde se implementa navegación entre pantallas utilizando Jetpack Compose Navigation.

## Tecnologías

- Kotlin
- Android Studio
- Jetpack Compose
- Material 3
- Navigation Compose

## Pantallas implementadas

### HomeScreen
Pantalla principal que permite navegar hacia la lista de elementos y el perfil.

### ListScreen
Muestra una lista de elementos mediante LazyColumn y permite navegar al detalle.

### DetailScreen
Recibe el parámetro itemId enviado desde la lista y muestra la información del elemento seleccionado.

### ProfileScreen
Muestra información del usuario y permite regresar al inicio.

## Navegación implementada

- Home → List
- List → Detail
- Home → Profile

## Ejecución

Abrir el proyecto en Android Studio y ejecutar la aplicación desde: Run app

## Prompt para la mejora del laboratorio

- Actúa como un desarrollador Senior Android especializado en Jetpack Compose y Material 3. Necesito que refactorices y construyas el diseño visual completo del "Portal Académico" garantizando máxima fidelidad visual y coherencia UI/UX en todas las vistas.


1. CONFIGURACIÓN GLOBAL DE TEMA Y DATOS (Color.kt, Theme.kt, Model)

- Datos del Usuario Activo:
    * Nombre: "Michael Fernandez"
    * Correo Institucional: "michael.fernandez@tecsup.edu.pe"
    * Teléfono: "+51 987 654 321"
    * Carrera: "Ingeniería de Software"
    * ID Estudiante: "2024-0005"
    * Facultad: "Ingeniería y Tecnología"
    * Ciclo Actual: "VI Ciclo"
    * Biografía: "Estudiante dedicado con interés en desarrollo Android y arquitectura de software."

- Paleta de Colores Material 3:
    * LavenderLight = Color(0xFFF0F0FF) (Lavanda suave)
    * LavenderDeep = Color(0xFFDEDEFF) (Lavanda medio)
    * TonalPurpleDeep = Color(0xFF3B1E7B) (Púrpura oscuro para encabezados)
    * TonalPurplePrimary = Color(0xFF5E35B1) (Púrpura primario)
    * TonalPurpleMedium = Color(0xFF7E57C2) (Púrpura tonal secundario)
    * SurfaceVariantLight = Color(0xFFF2ECEF) o Color(0xFFF3F0F6) (Morado claro/lavanda tenue para tarjetas)
    * BackgroundWhite = Color(0xFFFFFFFF) (Blanco puro para fondos de pantalla)
    * TonalRed = Color(0xFFD32F2F) / ErrorContainer (Para acciones de cerrar sesión)

2. PANTALLA DE LOGIN (LoginScreen.kt)

- Fondo de Pantalla: Brush.verticalGradient completo de TonalPurpleDeep/TonalPurplePrimary (arriba) a LavenderLight (abajo).
- Contenedor Principal: Encapsula TODO el formulario (Título "Portal Académico", Subtítulo "Accede a tu cuenta", Inputs, Botón y Enlace) dentro de una única tarjeta elevada (ElevatedCard) con fondo SurfaceVariantLight, esquinas redondeadas (16.dp) y elevación M3 de 2.dp.
- Campos de Texto: OutlinedTextField con bordes redondeados (12.dp) en color TonalPurpleMedium, iconos principales (Mail, Lock) y trailingIcon en el campo de contraseña para conmutar visibilidad.
- Botón Principal: Filled Button con fondo TonalPurplePrimary, texto en blanco ("INICIAR SESIÓN") y esquinas de 12.dp.
- Enlace Inferior: Texto centrado en TonalPurpleMedium ("¿Olvidaste tu contraseña?").

3. PANTALLA DE BIENVENIDA / INICIO (HomeScreen.kt)

- Fondo de Pantalla: Brush.verticalGradient continuo que abarca toda la pantalla desde TonalPurpleDeep en la parte superior hasta LavenderLight en la inferior (sin contenedores superiores recortados).
- Alineación: Todo el contenido (saludo "Bienvenido, Michael Fernandez", subtítulo "¿Qué deseas gestionar hoy?" y las tarjetas) debe estar centrado verticalmente usando Arrangement.Center.
- Tarjetas de Acción ("Directorio de Alumnos" y "Mi Perfil Académico"):
    * Tarjetas blancas elevadas con bordes de 16.dp.
    * Iconos a la izquierda dentro de un contenedor cuadrado redondeado (shape = RoundedCornerShape(12.dp)) con fondo tonal púrpura claro.
    * Título en negrita y descripción en texto secundario legible.

4. PANTALLA DE EXPEDIENTE ACADÉMICO (AcademicRecordScreen.kt)

- Fondo General de la Pantalla: Blanco puro (Color.White / #FFFFFF).
- TopAppBar: Fondo transparente/claro con flecha de retroceso y título "Expediente Académico" en TonalPurpleDeep.
- Banner Superior:
    * Box con altura de 180.dp a 200.dp, degradado vertical suave (Brush.verticalGradient) entre púrpura oscuro (#5D497E) y morado violáceo tenue.
    * Esquinas inferiores redondeadas: shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp).
- Avatar y Encabezado:
    * Foto de perfil circular (Avatar) centrada y superpuesta en el borde inferior del banner con borde blanco prominente.
    * Nombre "Michael Fernandez" (HeadlineLarge) y carrera "Ingeniería de Software" (TitleMedium en TonalPurpleMedium) ubicados justo DEBAJO del avatar sobre el fondo blanco.
- Tarjeta Única de Información (Card/Surface):
    * Un solo contenedor elevado con fondo morado medio claro / lavanda tenue (SurfaceVariantLight / Color(0xFFF3F0F6)) y bordes de 20.dp.
    * Datos incluidos: ID Estudiante (2024-0005), Correo (michael.fernandez@tecsup.edu.pe), Facultad (Ingeniería y Tecnología).
    * Iconos limpios en color púrpura oscuro sobre el fondo de la tarjeta (sin círculos morados detrás).
    * Un Divider fino que separa los datos personales del bloque "Biografía" al final de la tarjeta.

5. PANTALLA DIRECTORIO DE ALUMNOS (StudentListScreen.kt) - REFACTORIZACIÓN COMPLETA

- SUSTITUIR VISTA ANTERIOR: Reemplaza por completo la lista plana/genérica ("Lista", "Elemento número X") por el diseño de Directorio de Alumnos.
- Cabecera y Separador:
    * TopAppBar clara con fondo lavanda/blanco y título "Directorio de Alumnos" en negrita (TonalPurpleDeep).
    * Incluye un divisor horizontal (HorizontalDivider) de color lavanda/gris tenue ubicado INMEDIATAMENTE DEBAJO de la TopAppBar para delimitar la cabecera del cuerpo de la lista.
- Fondo General: Blanco puro (Color.White / #FFFFFF) en toda la LazyColumn.
- Datos de Alumnos (Model & Lista):
    * Define la siguiente lista de pruebas:
        1. Juan León — Ingeniería de Sistemas
        2. Maria Garcia — Arquitectura
        3. Carlos Perez — Medicina
        4. Ana Lopez — Derecho
        5. Luis Ramirez — Administración
- Diseño de Tarjeta de Alumno (StudentCard / Surface):
    * Cada elemento se renderiza dentro de una Card/Surface con fondo morado claro / lavanda tenue (SurfaceVariantLight / Color(0xFFF2ECEF)) y bordes redondeados (16.dp).
    * Extremo izquierdo: Avatar circular de 50.dp (`Image` con `clip(CircleShape)` o contenedor circular con imagen/inicial).
    * Centro (Column):
        - Nombre del alumno en negrita (TitleMedium, color TonalPurpleDeep).
        - Carrera del alumno justo debajo (BodyMedium, color TonalPurpleMedium).
        - Elimina completamente cualquier texto como "Toca para ver el detalle" o "Elemento número X".
    * Extremo derecho: Icono de flecha de navegación (Icons.AutoMirrored.Filled.KeyboardArrowRight / chevron_right) en color TonalPurpleMedium.

6. PANTALLA CONFIGURACIÓN DE PERFIL (ProfileSettingsScreen.kt) Y NAVEGACIÓN

- Ruta NavHost: Conectada a la ruta `profile_settings`.
- Fondo General: Blanco puro / neutro (Color.White) para asegurar contraste.
- TopAppBar: Fondo claro con botón de retroceso y título "Configuración de Perfil".
- Banner Superior:
    * Contenedor totalmente RECTANGULAR sin bordes redondeados (shape = RectangleShape).
    * Fondo con degradado horizontal/linear (Brush.horizontalGradient) en tonos morados apagados.
    * Avatar circular centrado y nombre completo en texto blanco ubicado DENTRO del banner.
- Secciones de Información:
    * Tarjetas agrupadas bajo las etiquetas "INFORMACIÓN PERSONAL" (Nombre, Correo, Teléfono) y "ACADÉMICO" (Carrera, Ciclo Actual) con iconos limpios.
- Botón de Cerrar Sesión:
    * Ubicado al final de la pantalla usando un Spacer(modifier = Modifier.weight(1f)) previo.
    * Estilizado como una píldora (shape = CircleShape o RoundedCornerShape(50)) de altura delgada/estilizada (height = 44.dp).
    * Fondo en tono rojo/rosa claro (ErrorContainer) con texto en rojo "Cerrar Sesión" e icono de salida.

Por favor, genera la refactorización estructurada de los archivos Jetpack Compose (Color.kt, Theme.kt, NavGraph.kt y las pantallas correspondientes) implementando estos lineamientos.