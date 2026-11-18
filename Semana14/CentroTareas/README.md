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
