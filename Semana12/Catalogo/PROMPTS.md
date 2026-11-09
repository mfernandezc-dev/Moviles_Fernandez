# Uso de Inteligencia Artificial — Catálogo de Laptops

Este documento registra el uso de **Warp + OpenCode con GPT-6.1 Sol** durante la **Fase 2 con IA del Laboratorio 11-12 de Programación en Móviles de Tecsup**. El trabajo fue individual y se realizó en la rama `con-ia`: primero mejoré la presentación de Catalogo y después incorporé las pruebas unitarias de ListaViewModel solicitadas en R12.

## Procedencia y alcance del registro

- Se recuperaron **tres mensajes originales** del historial local de OpenCode. Para facilitar la lectura, aquí se reproducen **extractos literales**, no las transcripciones completas. Las explicaciones fuera de esos bloques son resúmenes, no prompts originales.
- Las fechas de las consultas proceden de la marca `time.created` de cada mensaje y se expresan en **UTC**. No se dedujeron de las fechas de los commits.
- Los resultados se contrastaron con el historial Git, los archivos implementados, los reportes XML JUnit y los registros locales de compilación y pruebas.
- No queda pendiente recuperar el texto original de estas tres consultas. Esto no significa que se haya recuperado o documentado todo el historial de conversaciones del proyecto.
- No se incluyen credenciales, contenido de archivos de configuración privada ni exportaciones completas de las sesiones.

## Consulta 1

- **Fecha:** `2026-11-09T04:44:42.075Z` (UTC, registro del mensaje de OpenCode).
- **Herramienta:** Warp + OpenCode con GPT-6.1 Sol.
- **Prompt:** original recuperado; el siguiente bloque es un extracto literal de la solicitud inicial de mejora visual.

<details>
<summary>Ver extracto literal del prompt original</summary>

```text
## 3. OBJETIVO

Realizar una primera mejora visual de la aplicación Catalogo utilizando Inteligencia Artificial.

Quiero una interfaz:

- Moderna.
- Tecnológica.
- Profesional.
- Limpia.
- Ordenada.
- Fácil de usar.
- Visualmente atractiva.

Utiliza una línea visual coherente basada en Material 3, con colores azul e índigo, superficies claras y una jerarquía visual bien definida.

NO agregues funcionalidades innecesarias.

NO cambies la lógica del negocio.

NO conviertas el laboratorio en otro proyecto.
```

</details>

  Fuente local: sesión `ses_e41dc88acffergs4rnhwj1Y1mu`, mensaje `msg_1be9f6e59001wfLV8R7Odfq5zo`. El mensaje completo también delimitaba las cinco pantallas, los archivos permitidos y la protección de `main`.

- **Qué respondió (resumen):** la asistencia se orientó a aislar el trabajo en un worktree de `con-ia`, revisar las pantallas existentes y aplicar un diseño compartido mediante Material 3. Se prepararon cambios de tema, pantallas y componentes; la interrupción dejó pendiente continuar la verificación del trabajo.
- **Qué usamos / qué cambiamos y por qué:** adopté la paleta azul/índigo, superficies diferenciadas para los modos claro y oscuro, tarjetas redondeadas y una jerarquía tipográfica consistente. Las modificaciones visuales se limitaron al paquete `ui/` para mantener la arquitectura MVVM y las funcionalidades existentes. Firebase, Room, Retrofit, Coil y las notificaciones ya formaban parte del proyecto; no fueron creados durante este rediseño.
- **Qué aprendimos:** aprendí a pedir cambios con un alcance concreto. Separar presentación y lógica permitió mejorar las cinco pantallas sin convertir el rediseño en una reimplementación de la aplicación.

## Consulta 2

- **Fecha:** `2026-11-09T15:08:11.916Z` (UTC, registro del mensaje de OpenCode).
- **Herramienta:** Warp + OpenCode con GPT-6.1 Sol.
- **Prompt:** original recuperado; se muestran dos extractos literales de la solicitud de reanudación después del límite de tokens.

<details>
<summary>Ver extractos literales del prompt original</summary>

Inicio de la solicitud:

```text
Quiero continuar exactamente desde donde quedó interrumpida nuestra sesión anterior debido al límite de tokens.

Estamos trabajando en la Fase 2 con Inteligencia Artificial del Laboratorio 11-12 de Programación en Móviles de Tecsup.
```

Alcance de la continuación:

```text
Continúa únicamente con las tareas pendientes de la interfaz.

Utiliza:
- Kotlin.
- Jetpack Compose.
- Material 3.
- MVVM.
- Coil 3.3.0.

Mantén las mejoras dentro del paquete ui/.
```

</details>

  Fuente local: sesión `ses_e3f25bd9dffeWN477q5XYVM6cJ`, mensaje `msg_1c0da426a00164AmERtykF5WpZ`.

- **Qué respondió (resumen):** OpenCode inspeccionó el estado real de Git y encontró el worktree de `con-ia` con los cambios anteriores. Comprobó que la configuración local necesaria para compilar ya estaba presente e ignorada, por lo que no hizo otra copia. Conservó el trabajo existente y continuó con los ajustes y las verificaciones autorizadas.
- **Qué usamos / qué cambiamos y por qué:** reutilicé las mejoras ya realizadas. Se eliminó el límite de tres líneas de las descripciones de las tarjetas para evitar truncamientos y se centraron los mensajes de carga y de imagen no disponible, con márgenes adecuados. La solicitud inicial contemplaba sintetizar visualmente las descripciones; la reanudación priorizó mostrarlas completas sin alterar los datos.
- **Qué aprendimos:** aprendí que reanudar una tarea requiere comprobar el disco y Git, no confiar únicamente en el resumen de la conversación. Un worktree independiente permitió continuar sin cambiar de rama en el proyecto original ni descartar archivos ajenos.

### Rediseño consolidado y comprobaciones visuales

Las consultas 1 y 2 corresponden al mismo trabajo de interfaz, publicado en un único commit visual; no se cuentan como dos rediseños distintos.

| Pantalla | Mejora visual principal |
| --- | --- |
| Login | Bienvenida, fondo degradado, formulario organizado, botón destacado y mensajes de validación legibles. |
| Catálogo | Acciones distribuidas con `FlowRow`, tarjetas con fotografías proporcionadas, precio destacado y control de favoritos visible. |
| Detalle | Fotografía centrada, título destacado, bloque de precio, separación de la descripción y botón de retorno. |
| Favoritos | Reutilización de las tarjetas del catálogo y presentación clara del estado vacío. |
| Panel administrador | Tarjetas de indicadores locales, mejor jerarquía y distribución en columnas cuando hay espacio. |

Los componentes `EncabezadoPantalla`, `ImagenLaptop`, `AvisoInformativo` y `EstadoVacio` se agruparon en `ui/components/Presentacion.kt`. Se reutilizó `TarjetaLaptop` en Catálogo y Favoritos, y se actualizaron `Cargando`, `ErrorView` y el tema compartido. La carga de imágenes siguió usando Coil 3.3.0.

Comprobaciones registradas en el AVD autorizado **`Catalogo_H11_C`**:

- Apertura y revisión de Login, Catálogo, Detalle, Favoritos y Panel Admin, con una sesión administradora iniciada manualmente.
- Navegación al detalle desde Catálogo y Favoritos y retorno mediante los botones existentes.
- Visualización de las imágenes de los productos revisados. No se da por verificada individualmente la descarga de las cinco fotografías.
- Desmarcado del favorito existente, comprobación del estado vacío y restauración de ese favorito.
- Emisión de la notificación local, confirmada mediante el registro de Android.
- Rotación en las cinco pantallas y desplazamiento del contenido. Texto al 130 % revisado en Login, Catálogo, Detalle y Admin; modo oscuro revisado en Login, Catálogo y Admin.
- Observación de la carga inicial, del error de formulario vacío y del retorno a Login al cerrar sesión.
- Compilación de depuración con `BUILD SUCCESSFUL`.

**Limitaciones:** no se forzaron fallos de red ni descargas fallidas de imágenes. El teclado completo quedó pendiente porque el AVD mostró entrada flotante. El acceso administrador observado no equivale a una auditoría completa de todos los permisos de los roles USER y ADMIN. No se modificaron cuentas ni datos de Firebase, y estas comprobaciones no sustituyen pruebas de integración de autenticación, red o persistencia.

## Consulta 3

- **Fecha:** `2026-11-09T16:09:42.182Z` (UTC, registro del mensaje de OpenCode).
- **Herramienta:** Warp + OpenCode con GPT-6.1 Sol.
- **Prompt:** original recuperado; el siguiente bloque es un extracto literal del objetivo de R12.

<details>
<summary>Ver extracto literal del prompt original</summary>

```text
## 2. OBJETIVO ÚNICO

Cumplir el requisito R12 de la guía:

Crear como mínimo tres pruebas unitarias reales para ListaViewModel, utilizando un repositorio falso.

Las pruebas deben funcionar sin internet, sin base de datos real, sin Firebase y sin emulador.

No modificar las funcionalidades de la aplicación.

No realizar commit ni push todavía.
```

</details>

  Fuente local: sesión `ses_e3f25bd9dffeWN477q5XYVM6cJ`, mensaje `msg_1c11291cc001s5h05klIXyyN3O`. La solicitud completa especificaba los escenarios de sincronización exitosa, error sin datos y error con datos locales.

- **Qué respondió (resumen):** OpenCode inspeccionó `ListaViewModel`, `ItemRepository`, `Item`, `UiState`, `Notificador` y las dependencias existentes. Identificó que el ViewModel llama a `cargarItems()` al crearse, usa `StateFlow`, no notifica automáticamente al sincronizar y conserva los productos locales cuando falla el refresco. También comprobó que el mensaje de error sin datos es fijo, no el texto de la excepción simulada.
- **Qué usamos / qué cambiamos y por qué:** se crearon los dos fakes, una regla de dispatcher y tres pruebas JUnit. JUnit 4.13.2 ya estaba disponible; solo se añadió `kotlinx-coroutines-test:1.10.2` mediante el Version Catalog y `testImplementation`, coincidiendo con la versión de Coroutines resuelta por Gradle. No se modificó el ViewModel ni otro código de producción para hacer pasar las pruebas.
- **Qué aprendimos:** aprendí que el fallo de una actualización y la ausencia de datos son situaciones diferentes. Con productos guardados, el estado puede seguir siendo `UiState.Exito` y comunicar el problema mediante `actualizacionFallida`.

### Implementación de prueba

- **`FakeItemRepository`:** implementa el contrato completo de `ItemRepository` en memoria. Usa `MutableStateFlow`, permite controlar los productos iniciales y emitidos, el resultado de `refrescar()`, los favoritos y las cantidades. Registra las llamadas al refresco y los identificadores cuyos favoritos se alternan. No usa HTTP, Room ni almacenamiento persistente.
- **`FakeNotificador`:** registra las solicitudes con su título y texto, sin `Context` ni `NotificationManager`. Las tres pruebas verifican que la sincronización no solicita notificaciones automáticas, que es el comportamiento real del ViewModel.
- **`MainDispatcherRule`:** sustituye temporalmente `Dispatchers.Main` por `StandardTestDispatcher` y lo restablece al finalizar. Las pruebas usan `runTest` y `advanceUntilIdle`; `ViewModelStore.clear()` cancela el `viewModelScope` al terminar cada caso.

### Tres pruebas R12

| Prueba | Comportamiento verificado |
| --- | --- |
| `sincronizacionExitosa_refrescaYExponeLosProductosEsperados` | Se ejecuta un refresco, los productos del repositorio llegan a `estado`, el resultado es `UiState.Exito` con la lista esperada y no se activa el aviso de actualización fallida. |
| `errorSinDatosGuardados_exponeErrorDeConexion` | El refresco falla sin productos locales; el estado es `UiState.Error` con el mensaje de conexión definido en producción y se activa `actualizacionFallida`. |
| `errorConDatosLocales_conservaProductosYActivaAvisoDeActualizacion` | El refresco falla con productos guardados; se mantiene `UiState.Exito` con la lista local, se conservan los favoritos y se activa `actualizacionFallida`. |

### Resultados comprobados de R12

| Verificación | Resultado registrado |
| --- | --- |
| Pruebas nuevas de `ListaViewModelTest` | **3 aprobadas**, 0 fallidas, 0 errores y 0 omitidas. |
| Total de pruebas locales | **4 aprobadas**, incluyendo `addition_isCorrect`, que ya existía y no cuenta como una nueva prueba R12. |
| `:app:testDebugUnitTest` | **BUILD SUCCESSFUL**. |
| Repetición con `--offline --rerun-tasks` | **BUILD SUCCESSFUL**; las pruebas se volvieron a ejecutar, no solo se reutilizó un resultado anterior. |
| `:app:assembleDebug` | **BUILD SUCCESSFUL**. |

Los nombres y los totales se verificaron en `app/build/test-results/testDebugUnitTest/TEST-com.fernandez.catalogo.ui.lista.ListaViewModelTest.xml` y en el reporte de la prueba existente. El reporte XML de R12 registra `2026-11-09T16:13:51.407Z`. El informe HTML local se genera en `app/build/reports/tests/testDebugUnitTest/index.html`; los reportes y los registros de ejecución son evidencia local, no archivos añadidos al commit de R12.

Las pruebas se ejecutaron en la JVM, sin emulador, sin inicializar Firebase, sin una base de datos Room real y sin consultar DummyJSON. El modo offline de Gradle no es por sí solo un bloqueo de red: el aislamiento de estos casos se consigue inyectando exclusivamente los fakes en memoria. No son pruebas de integración de los servicios reales.

No hubo pruebas fallidas que corregir durante esta implementación. Se configuró el dispatcher de pruebas y se adaptaron las expectativas al contrato existente. Permaneció la advertencia previa de compatibilidad entre AGP y el SDK de compilación; no se cambiaron esas versiones para silenciarla.

## Resultados y evidencias versionadas

Los dos trabajos se publicaron después de recibir autorización separada. Las fechas siguientes son **fechas de commit**, conservando su zona horaria; no representan las fechas de las consultas.

| Trabajo | Commit y mensaje | Fecha de commit |
| --- | --- | --- |
| Mejora visual | [`083fe97b58bed00635517f9f2ec95fe63428ce9a`](https://github.com/mfernandezc-dev/Moviles_Fernandez/commit/083fe97b58bed00635517f9f2ec95fe63428ce9a) — `feat: mejorar interfaz de Catalogo con IA` | `2026-11-09T10:34:31-05:00` |
| R12 | [`76df3a29e1492f897f7e8534291ac3e4fa6220f1`](https://github.com/mfernandezc-dev/Moviles_Fernandez/commit/76df3a29e1492f897f7e8534291ac3e4fa6220f1) — `test: agregar pruebas unitarias de ListaViewModel` | `2026-11-09T11:20:02-05:00` |

### Alcance de los archivos publicados

El commit visual contiene **11 archivos**, todos bajo `app/src/main/java/com/fernandez/catalogo/ui/`:

```text
admin/AdminScreen.kt
components/Cargando.kt
components/ErrorView.kt
components/Presentacion.kt
detalle/DetalleScreen.kt
favoritos/FavoritosScreen.kt
lista/ListaScreen.kt
login/LoginScreen.kt
theme/Color.kt
theme/Theme.kt
theme/Type.kt
```

El commit de R12 contiene **seis archivos**, relativos a `Semana12/Catalogo/`:

```text
app/src/test/java/com/fernandez/catalogo/testing/FakeItemRepository.kt
app/src/test/java/com/fernandez/catalogo/testing/FakeNotificador.kt
app/src/test/java/com/fernandez/catalogo/testing/MainDispatcherRule.kt
app/src/test/java/com/fernandez/catalogo/ui/lista/ListaViewModelTest.kt
app/build.gradle.kts
gradle/libs.versions.toml
```

En R12, los dos archivos Gradle solo incorporaron la dependencia de pruebas. La mejora visual previa y el código de producción no se alteraron. En ambas publicaciones se comprobó el conjunto exacto de archivos antes del commit y se utilizó un push normal a `origin/con-ia`.

Las verificaciones de integridad conservaron `main`, los demás laboratorios y los cambios locales ajenos, incluido `.idea/caches/deviceStreaming.xml`, que quedó fuera de ambos commits. Se comparó el estado antes y después de las operaciones; esto no significa que las historias completas de `main` y `con-ia` sean idénticas. No se añadieron credenciales ni se modificaron las capturas existentes.

## Reflexión personal

La IA me ayudó a ordenar las mejoras visuales y a mantener una misma línea de diseño entre pantallas. Aun así, tuve que comprobar cada propuesta con el código real, la compilación y la aplicación en ejecución. Una descripción aparentemente correcta no garantiza que los textos se vean bien o que la navegación siga funcionando.

Probar el ViewModel con repositorios falsos me permitió repetir los errores de conexión sin depender de internet, de Firebase o de una base de datos real. Entendí que conservar datos locales permite seguir mostrando productos aunque falle la actualización, y que ese fallo debe comunicarse por separado. La IA fue un apoyo para preparar los casos, pero no sustituyó su ejecución ni la revisión de los reportes. También aprendí a dejar documentado lo que todavía no se ha probado, en lugar de darlo por resuelto.

Este archivo corresponde a R13 y queda pendiente de revisión antes de su publicación. Su creación no incluye nuevos cambios de código ni una nueva ejecución de las pruebas descritas.
