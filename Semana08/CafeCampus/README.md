# CafeCampus 

Aplicación Android para la gestión de pedidos de una cafetería de campus. Permite a los estudiantes consultar el menú y realizar pedidos, mientras que el administrador administra los productos, los pedidos y las preferencias del servicio.

## Tecnologías utilizadas

- **Kotlin**: lenguaje principal.
- **Jetpack Compose y Material 3**: interfaces de usuario.
- **MVVM**: separación entre interfaces, lógica de presentación y datos.
- **Repository**: capa de acceso a datos mediante una interfaz.
- **Room**: persistencia local de usuarios, productos, pedidos y detalles.
- **DataStore Preferences**: preferencias y datos de sesión.
- **Navigation Compose**: navegación entre pantallas.
- **Coroutines, Flow y StateFlow**: operaciones asíncronas y actualización de la interfaz.

## Funcionalidades

### Estudiante

- Inicio de sesión.
- Consulta del menú de productos por categorías.
- Selección de productos, cantidades y carrito.
- Confirmación de pedidos con hora de recojo y forma de pago.
- Consulta del estado y del historial de pedidos.
- Acceso al perfil y cierre de sesión.
- Bloqueo de nuevas confirmaciones cuando el administrador desactiva **Recibir pedidos**.

### Administrador

- Panel con resumen de pedidos y ventas.
- Consulta y gestión del estado de los pedidos.
- Gestión del menú: registro, edición, eliminación y disponibilidad de productos.
- Filtros por categoría.
- Ajustes: modo oscuro, preferencia de notificaciones y recepción de pedidos.
- Cambio de perfil y cierre de sesión.

> La opción de notificaciones guarda una preferencia; no implica por sí sola notificaciones del sistema Android.

## Arquitectura

La aplicación sigue la estructura **MVVM + Repository + Room**:

```text
Pantallas Jetpack Compose
          ↓
       ViewModels
          ↓
  CafeRepository (interfaz)
          ↓
 Implementación del repositorio
          ↓
       Room / DAO
```

**DataStore** almacena las preferencias y los datos de sesión. Los ViewModels exponen estados mediante `StateFlow` para actualizar las pantallas.

## Credenciales de prueba

| Perfil | Correo | Contraseña |
| --- | --- | --- |
| Estudiante | `michael@campus.test` | `1234` |
| Administrador | `admin@cafe.test` | `admin` |

> Cuentas de demostración para uso local del laboratorio; no son credenciales para producción.

## Requisitos y ejecución

1. Abrir la carpeta `Semana08/CafeCampus` con **Android Studio**.
2. Esperar la sincronización de Gradle.
3. Seleccionar un emulador o dispositivo Android compatible con la configuración del proyecto.
4. Ejecutar el módulo `app`.

Para comprobar la compilación desde PowerShell, en la carpeta del proyecto:

```powershell
.\gradlew.bat :app:assembleDebug
```

## Pruebas de funcionamiento

| Caso | Resultado esperado |
| --- | --- |
| Login de estudiante | Abre el menú del estudiante. |
| Login de administrador | Abre el panel de control. |
| Crear y editar un producto | El menú muestra los cambios guardados. |
| Cambiar disponibilidad | Se refleja el estado disponible o agotado. |
| Crear un pedido | Se registra y puede consultarse en seguimiento e historial. |
| Cambiar estado del pedido | El estudiante visualiza el estado actualizado. |
| Desactivar **Recibir pedidos** | No permite confirmar pedidos nuevos. |
| Cerrar sesión | Vuelve al login sin eliminar los datos de Room. |
| Reiniciar la aplicación | Se conservan las preferencias guardadas en DataStore. |

**Nota:** esta tabla contiene los casos que se deben verificar para la entrega; no sustituye un registro de resultados de ejecución o capturas.

## Alcance

Proyecto académico desarrollado para practicar **Jetpack Compose, MVVM, Room, Repository y DataStore**. La persistencia es local al dispositivo; no se ha implementado un backend remoto ni un servicio real de pagos.
