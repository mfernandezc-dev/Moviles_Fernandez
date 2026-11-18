# Laboratorio 14 — CentroTareas: Hilos en Android

## 1. Descripción

Una única pantalla vertical desplazable demuestra cómo evitar bloqueos del hilo
principal, publicar progreso, cancelar trabajo y coordinar cargas con corrutinas.
Las tres tarjetas son **Tarea con progreso**, **Contar números primos** y
**Dos cargas**. No existe navegación adicional ni acceso real a Internet.

La Fase 1 (`main`) se implementó con **GPT-6.1 Sol mediante OpenCode**, siguiendo
la secuencia académica. No se presenta como trabajo sin IA. No se recibieron
imágenes de referencia adjuntas; se siguió la descripción visual proporcionada:
Material 3, cabecera y botones azules, tarjetas blancas, resultados verdes y
errores rojos. Las imágenes enlazadas abajo son capturas reales de la aplicación.

## 2. Tecnologías y compilación

- Kotlin, Jetpack Compose, Material 3, MVVM y Repository.
- Corrutinas, StateFlow, ViewModel y viewModelScope.
- Gradle Wrapper 9.4.1, Android Gradle Plugin 9.2.1.
- JDK 21 seleccionado por `gradle/gradle-daemon-jvm.properties`.
- compileSdk 36.1, targetSdk 36, minSdk 24.
- Compose BOM 2026.02.01 y Activity Compose 1.13.0 conservados de la plantilla.
- Dependencia solicitada: `lifecycle-viewmodel-compose:2.8.7`.
- Solo para pruebas JVM: `kotlinx-coroutines-test:1.9.0`, para ejecutar suspend
  con `runTest`, sin bloquear el hilo mediante `runBlocking`.

La plantilla inicial no compilaba: Core 1.19.1 y Lifecycle 2.11.0 requerían API
37. Se declararon Core 1.17.0 y Lifecycle Runtime 2.8.7 sin cambiar el SDK ni el
BOM. Gradle resuelve transitivamente Core 1.18.0 y Lifecycle 2.9.4, compatibles
con la configuración y verificados mediante compilación y dependencyInsight.

Desde esta carpeta, en Windows:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:connectedDebugAndroidTest
.\gradlew.bat :app:lintDebug
```

`connectedDebugAndroidTest` requiere un dispositivo o emulador conectado por
ADB. Configurar localmente el SDK mediante Android Studio o `local.properties`;
este archivo, `.idea/`, `.gradle/` y `build/` están excluidos de Git.
APK debug: `app/build/outputs/apk/debug/app-debug.apk` (no versionado).

## 3. Estructura de paquetes

```text
app/src/main/java/com/tecsup/centrotareas/
├── MainActivity.kt
├── model/
│   └── Estados.kt
├── data/
│   ├── TareasRepository.kt
│   └── TareasRepositoryLocal.kt
├── viewmodel/
│   ├── TareasViewModel.kt
│   └── TareasViewModelFactory.kt
└── ui/
    ├── PantallaTareas.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

Las pruebas están en `app/src/test/` y `app/src/androidTest/`, también bajo
`com.tecsup.centrotareas`. Se aprovechó la estructura generada por Android
Studio, incluidos manifiesto, recursos y Wrapper; no se creó otro proyecto.

## 4. Arquitectura MVVM

```text
PantallaTareas ──eventos──> TareasViewModel ──suspend──> TareasRepository
      ↑                         │                           ↑
      └──── collectAsState() ───┘                 TareasRepositoryLocal
                StateFlow                          implementación
```

La pantalla solo recibe el ViewModel. La Activity construye la Factory que
inyecta el Repository. El ViewModel no recibe Context ni importa Compose.
Gestiona estados y trabajos, mientras el repositorio desconoce la interfaz
visual. No se realizan cálculos pesados en un composable.

## 5. Funcionamiento del Repository

`TareasRepository` define contar primos, cargar perfil y cargar pedidos. Su
implementación local devuelve `Ana Torres (ficticia)` después de 2000 ms y
tres pedidos después de 3000 ms. Con error activado, pedidos lanza
`IOException("Sin conexión")`. Son simulaciones con `delay`, no llamadas de red;
no necesitan permisos, clientes HTTP ni servicios externos.

## 6. Corrutinas

Los eventos lanzan corrutinas en `viewModelScope`. El trabajo de CPU utiliza
`withContext(Dispatchers.Default)`. `delay` suspende sin ocupar un hilo durante
la espera. Los logs de etiqueta `HILO` registran antes, dentro y después del
cambio de contexto. Los tres trabajos son independientes: cancelar progreso
no cancela primos ni cargas.

## 7. StateFlow

Cada `MutableStateFlow` es privado y se expone como `StateFlow` mediante
`asStateFlow()`. Compose usa `collectAsState()` y envía eventos, sin modificar
el estado directamente. `PrimosUi` representa Inicial, Calculando, Listo y
Cancelado; `CargaUi`, Inicial, Cargando, Exito y Error. El contador de toques
también vive en el ViewModel y el campo numérico usa `rememberSaveable`.

## 8. Progreso

En `main`, el ViewModel ejecuta 100 iteraciones con `delay(40)` en Default.
El avance es `i / 100f`; se representa con LinearProgressIndicator y porcentaje.
Cada publicación del estado se despacha a Main para evitar reentradas de la
interfaz durante la rotación en el entorno instrumentado de Compose.
El contador Tócame sigue disponible mientras se ejecuta la tarea.

## 9. Cancelación

`trabajo: Job?` conserva la tarea. El inicio comprueba si ya hay un trabajo activo
y el botón Iniciar se deshabilita mientras corre. Cancelar solo se habilita
durante la ejecución. `cancel()` provoca una `CancellationException`, conserva
el porcentaje alcanzado y cambia el mensaje a Cancelado. La excepción se
relanza y `finally` restablece `corriendo = false`. Un nuevo inicio parte de 0.
Primos tiene su propio Job y su botón de cancelación.

## 10. Cálculo de primos

El repositorio recorre `2..hasta` en `Dispatchers.Default` y ejecuta
`ensureActive()` en cada iteración. La función `esPrimo` comprueba divisores
hasta la raíz usando `d.toLong() * d <= n` para evitar desbordamiento.
El ViewModel mide con `System.currentTimeMillis()` y publica cantidad y tiempo.
La UI y el ViewModel ignoran límites menores que 2; la UI rechaza textos no
convertibles a Int, incluidos números fuera de rango.

Resultados comprobados por la prueba JVM que llama al repositorio real:

| Hasta | Cantidad |
| ---: | ---: |
| 100 | 25 |
| 1000 | 168 |
| 10000 | 1229 |
| 100000 | 9592 |
| 500000 | 41538 |

## 11. Cargas secuenciales

Una por una espera primero el perfil y luego los pedidos. La duración nominal
es 2000 + 3000 = 5000 ms. La última ejecución instrumentada para las capturas
midió **5003 ms**. La duración varía con el dispositivo y la carga del sistema.

## 12. Cargas paralelas

A la vez abre un `coroutineScope`, crea dos tareas `async` y obtiene sus
resultados con `await()`. La duración nominal es max(2000, 3000) = 3000 ms.
La misma ejecución midió **3002 ms**. Ambos botones se deshabilitan durante
la carga; se muestra un CircularProgressIndicator y, al terminar, un recuadro
verde con modo, perfil, cantidad de pedidos y duración medida.

## 13. Manejo de errores y reintentos

El Switch Simular error controla un StateFlow. Al comenzar cada carga se toma
una instantánea del valor y se envía a `repo.cargarPedidos`. El Switch se
deshabilita durante la carga. Las excepciones se convierten en
`CargaUi.Error("No se pudo cargar: " + mensaje)` y aparecen en un recuadro rojo.
`CancellationException` siempre se relanza y no se presenta como fallo de red.
Reintentar ejecuta `cargarALaVez()` con el valor actual del Switch: si sigue
activado vuelve a fallar; al desactivarlo termina correctamente.

## 14. Observación técnica del paso 1

El commit del paso 1 conserva `tareaLargaMala()`: asignaba Trabajando..., llamaba
`Thread.sleep(4000)` y asignaba Terminó. El evento del botón corre en el hilo
principal; al bloquearlo no puede procesar toques ni dibujar cuadros. Compose
no puede presentar Trabajando... antes del bloqueo, y al recuperar el hilo
el estado ya dice Terminó. Diez segundos de bloqueo podrían provocar un ANR,
dependiendo de los límites del sistema y los eventos pendientes.

Este comportamiento histórico se explica a partir del código; **no se ejecutó
manualmente aquella versión**. Desde el paso 2 se eliminó del código Kotlin.

## 15. Pruebas realizadas

Ejecutadas el **18 de noviembre de 2026** en Windows y en el emulador
**Medium_Phone, Android 17 / API 37**, conectado como `emulator-5554`.

| Verificación real | Resultado y alcance |
| --- | --- |
| Compilación debug | Correcta en cada paso antes de su commit |
| Pruebas JVM | 4 aprobadas: prueba aritmética de la plantilla y 3 de Repository |
| Valores de primos | Los cinco valores de la tabla, más límites 1, 2 y 3 |
| Cancelación CPU | Int.MAX_VALUE cancelado con cancelAndJoin y límite de 2 s |
| Pruebas instrumentadas | 6 aprobadas: paquete de la app y 5 pruebas de interfaz |
| Barra hasta 100 % | Se verificaron 1f, 100 % y Terminó |
| Cancelar y reiniciar | Cancelado, porcentaje conservado y reinicio desde cero |
| Doble inicio | Botón deshabilitado y segunda llamada al ViewModel durante la tarea |
| Tócame | Contador cambia durante progreso y durante primos grandes |
| Entrada y primos UI | Texto inválido, 1 y desbordamiento rechazados; 100000 → 9592 |
| Cancelar primos UI | Calculando → Cancelado usando su botón |
| Cargas | 5003 ms frente a 3002 ms; perfil y 3 pedidos comprobados |
| Error y reintento | Fallo secuencial, fallo paralelo y éxito al desactivar el Switch |
| Rotación | Paisaje y regreso a retrato; mismo ViewModel, entrada y contador conservados; tarea completa |
| Logcat HILO | Antes y Después en main; Dentro en DefaultDispatcher-worker |
| Lint debug | 0 errores y 19 advertencias, no ocultadas |
| Patrones prohibidos | Sin GlobalScope, runBlocking ni Thread.sleep en los archivos Kotlin finales |

Los tests de interfaz están en `PantallaTareasTest.kt`. Las capturas proceden
de `UiAutomation.takeScreenshot()` durante esos tests, no de imágenes generadas.
Se ejecutó la suite con Gradle y se repitió directamente con AndroidJUnitRunner
para recuperar las imágenes antes de desinstalar la aplicación. Las comprobaciones
de interfaz son **automatizadas**, no pruebas manuales realizadas por una persona.

Informes locales (ignorados por Git):
- `app/build/reports/tests/testDebugUnitTest/index.html`
- `app/build/reports/androidTests/connected/debug/index.html`
- `app/build/reports/lint-results-debug.html`

Advertencias Lint: API objetivo no más reciente, etiqueta redundante de la
plantilla, actualizaciones disponibles, colores XML heredados sin uso y
recomendación de mover las dos dependencias explícitas al catálogo. Se
mantuvieron las versiones compatibles y la estructura inicial, sin suprimir
los avisos ni actualizar dependencias por motivos ajenos al laboratorio.

**Incidencias y soluciones:**
1. Fallo AAR inicial por requerir API 37: ajuste de Core y Lifecycle.
2. Una ejecución de rotación falló con CalledFromWrongThreadException al
   publicar progreso desde Default en el entorno de pruebas Compose. Se
   trasladó exclusivamente la publicación a Main; después pasaron la suite
   completa y dos repeticiones directas. No se reescribieron commits previos.
3. Gradle desinstaló la app al finalizar las pruebas y eliminó las capturas
   externas: reinstalación de los APK ya compilados y ejecución directa del
   runner, seguida de `adb pull`.

**Pendiente de validación manual:** dispositivo físico, contraste/accesibilidad
con TalkBack y fuentes grandes, otros tamaños/API, y demostración histórica del
congelamiento. No se comprobó persistencia tras muerte del proceso (fuera del
alcance); ViewModel no equivale a almacenamiento persistente. Las imágenes de
referencia originales siguen sin estar disponibles para una comparación exacta.

## 16. Capturas de evidencias

Capturas completas reales, sin edición, almacenadas en `capturas/main/`.
La comparación se registra en dos imágenes para mantener ambos tiempos legibles.

### 1. Tarea con progreso
![Progreso y contador activo](capturas/main/01-progreso.png)

### 2. Tarea cancelada
![Porcentaje conservado al cancelar](capturas/main/02-cancelado.png)

### 3. Conteo de primos
![Resultado y duración de primos](capturas/main/03-primos.png)

### 4. Comparación de cargas
![Carga una por una](capturas/main/04a-carga-secuencial.png)
![Carga a la vez](capturas/main/04b-carga-paralela.png)

### 5. Error simulado
![Error de conexión y opción Reintentar](capturas/main/05-error-simulado.png)

## 17. Preguntas «Para pensar»

**1. ¿Por qué no se veía Trabajando...?**
Porque el hilo que debía dibujarlo estaba bloqueado. Cambiar el estado no
dibuja inmediatamente la pantalla: el hilo principal necesita ejecutar el
siguiente cuadro. Al liberarse, el estado ya había cambiado a Terminó.

**2. ¿Qué diferencia hay entre Thread.sleep y delay?**
Thread.sleep bloquea el hilo y no coopera con la cancelación de corrutinas.
delay suspende la corrutina, deja el hilo disponible y comprueba cancelación.
Una espera no equivale a un cálculo de CPU: para este se cambia de dispatcher.

**3. ¿Por qué Default para primos e IO para la red?**
Default tiene un conjunto de trabajadores adecuado a tareas intensivas de CPU.
IO está pensado para operaciones bloqueantes de entrada/salida. Un cliente de
red suspend y no bloqueante puede administrar su propio dispatcher. Aquí no
hay red real: las simulaciones usan delay, por lo que no necesitan IO.

**4. ¿Qué ocurre al cerrar una pantalla y qué hace viewModelScope?**
Las tareas se cancelan cuando se limpia el ViewModel, al finalizar su propietario.
Una rotación recrea la Activity pero conserva el ViewModel, por eso la tarea y
sus estados continúan. No toda salida momentánea de pantalla limpia su
ViewModel. viewModelScope evita mantener tareas huérfanas tras su destrucción;
delay y ensureActive permiten que las tareas cooperen con la cancelación.

**5. ¿Por qué async tarda 3 segundos y no 5? ¿Qué pasa si una carga falla?**
Ambas tareas empiezan sin esperar a la otra y se espera a la más lenta: unos
3 segundos. En coroutineScope el fallo de una hija cancela a las hermanas que
sigan activas y se propaga al padre; el catch publica Error. En esta simulación
perfil suele haber terminado cuando pedidos falla. No se entregan resultados
parciales ni se convierte una cancelación en éxito o error de conexión.

## 18. Historial y ramas

La Fase 1 sigue nueve commits reales y consecutivos, en este orden:

1. `chore: importa proyecto base CentroTareas`
2. `feat(hilos): provoca el congelamiento con Thread.sleep en el hilo principal`
3. `fix(hilos): mueve la tarea larga a una corrutina con withContext`
4. `feat(progreso): agrega barra de progreso con StateFlow`
5. `feat(cancelar): permite cancelar la tarea con Job`
6. `feat(primos): cuenta primos en Dispatchers.Default con cancelacion`
7. `feat(carga): compara carga secuencial y en paralelo con async`
8. `feat(errores): maneja errores de carga y agrega reintento`
9. `docs: agrega README con observaciones y capturas`

Ubicación: `Moviles_Fernandez/Semana14/CentroTareas`. Los commits del laboratorio
no incluyen archivos de otras semanas ni cambios en `Semana14/Lab14`.

La Fase 2 requiere una decisión previa: ya existía una rama `con-ia`, local y
remota, con trabajo de otros laboratorios (punta inspeccionada: `52fbaa7`).
No se sobrescribe ni se reinicia. Crear desde main otra rama con ese mismo
nombre, conservar la existente intacta y obtener solo cuatro commits adicionales
son condiciones incompatibles sin resolver primero esa ocupación del nombre.
No se han iniciado mejoras ni creado PROMPTS.md en main.

## 19. Conclusiones

La capacidad de respuesta depende de no bloquear el hilo principal, no solo de
usar la palabra suspend. Repository aísla la simulación y el cálculo de CPU;
ViewModel administra el ciclo de vida, cancelación y estados observables. La
concurrencia estructurada reduce las cargas independientes de unos cinco a
tres segundos y propaga fallos de forma controlada. Compilar, medir y probar
rotación detectó problemas que una revisión estática no habría demostrado.
