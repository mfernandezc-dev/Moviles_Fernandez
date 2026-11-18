# Laboratorio 14 — CentroTareas: Hilos en Android

Proyecto académico Kotlin y Jetpack Compose, implementado con asistencia de
GPT-6.1 Sol mediante OpenCode también durante la Fase 1 (`main`).

## Base (paso 0)

Se conserva el proyecto generado por Android Studio y se configura el paquete
`com.tecsup.centrotareas`. Una pantalla Material 3 reúne tres tarjetas que se
habilitan progresivamente durante los pasos del laboratorio.

Arquitectura: PantallaTareas → TareasViewModel → TareasRepository →
TareasRepositoryLocal. La Factory inyecta el repositorio; la pantalla observa
StateFlow de solo lectura con collectAsState().

Compilar en Windows: `./gradlew.bat :app:assembleDebug`.
SDK: API 36.1, mínimo 24, objetivo 36; Gradle 9.4.1 y JDK 21 mediante toolchain.

La plantilla inicial no compilaba: Core 1.19.1 y Lifecycle 2.11.0 exigían API 37.
Se ajustan sus versiones para conservar el SDK instalado, Compose y Material 3.
Las evidencias visuales y pruebas de interacción se registrarán al ejecutarse;
no se consideran aprobadas por la sola compilación.

## Observación técnica del paso 1

El evento del botón se ejecuta en el hilo principal. `Thread.sleep(4000)` lo
bloquea: no puede procesar toques ni dibujar cuadros durante cuatro segundos.
Aunque el estado cambie a «Trabajando...», Compose no puede dibujarlo antes del
bloqueo; cuando vuelve a renderizar, el estado ya dice «Terminó».
Si el bloqueo durara diez segundos podría producir un ANR (Application Not
Responding), según los límites del sistema y los eventos pendientes.
Esta es una explicación técnica del código, no una prueba manual ejecutada.
