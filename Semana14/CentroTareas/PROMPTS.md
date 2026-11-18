# Registro real de IA — CentroTareas, Laboratorio 14

## Herramienta, modelo y alcance

- Herramienta: **OpenCode**.
- Modelo utilizado en esta sesión: **GPT-6.1 Sol**.
- Fecha de trabajo: **18 de noviembre de 2026**.
- La asistencia de IA se utilizó también en main, no solo en con-ia.
- No se utilizó Gemini ni se atribuyen respuestas a otro asistente.
- Este archivo resume la interacción real de esta sesión; no simula una
  conversación nueva ni reproduce íntegramente el largo encargo inicial.
  Los fragmentos entre comillas son extractos del usuario; las respuestas son
  resúmenes identificados como tales, no citas inventadas del modelo.

## 1. Encargo inicial y Fase 1

**Prompt utilizado (extractos del único encargo inicial):**

> Actúa como un desarrollador Android senior especializado en
> Kotlin, Jetpack Compose, Material 3, corrutinas, StateFlow,
> arquitectura MVVM, Repository y control de versiones Git.

> Tu objetivo es desarrollar completamente el Laboratorio 14:
> "CentroTareas: Hilos en Android", respetando estrictamente
> la guía académica, la arquitectura, los requerimientos
> funcionales, las interfaces visuales y los commits indicados.

> No inventes pruebas ni ocultes errores.

**Respuesta resumida y trabajo real:** inspección de Git, archivos, SDK y Gradle;
adaptación del paquete generado a `com.tecsup.centrotareas`; implementación y
compilación paso a paso, con los nueve mensajes exactos. Publicación normal de
main en `29fc0f7`. Se ejecutaron pruebas JVM, pruebas de interfaz en emulador,
rotación, revisión de Logcat y Lint, y se recuperaron capturas reales.

**Cambios aceptados:** aprovechar el proyecto de Android Studio, inyección por
Factory, estados sellados, repositorio local, StateFlow de solo lectura,
cancelación cooperativa, una única pantalla con tres tarjetas y dependencias
compatibles con API 36.1.

**Cambios descartados/no incorporados:** crear otro proyecto o repositorio,
pantallas XML, navegación, servicios reales, dependencias de red, imágenes
artificiales, modificaciones de otros laboratorios y documentación que afirmara
que main se hizo sin IA.

**Justificación técnica:** respetar el alcance académico y separar trabajo de
CPU, estados visuales y representación. El fallo AAR de la plantilla exigió
ajustar Core y Lifecycle, no actualizar todo el conjunto de herramientas. Un
fallo instrumentado de rotación llevó a publicar el progreso en Main antes del
commit del paso 7; se repitieron realmente las pruebas después de corregirlo.

## 2. Decisión real sobre la rama con-ia existente

**Situación encontrada:** con-ia no estaba libre. Su punta local y remota era
`52fbaa7`, con mejoras anteriores de otros laboratorios. El asistente informó
el conflicto entre preservar esa rama y crear otra homónima desde main.

**Prompt posterior real del usuario (extractos de su aclaración):**

> La rama con-ia YA EXISTE y quiero utilizar exactamente
> esa rama para desarrollar la Fase 2 del Laboratorio 14.

> Integrar los cambios de main mediante un merge
> normal, conservando el historial de ambas ramas.

> Si algún conflicto afecta otros laboratorios
> o requiere sobrescribir archivos existentes,
> detenerse y solicitar mi autorización.

**Respuesta resumida y comprobación real:** `git merge-tree` previsualizó la
integración sin modificar el árbol de trabajo. Detectó 28 conflictos add/add
en Semana08, Semana12 y Semana13. El asistente se detuvo antes de ejecutar un
merge que pudiera afectar esos laboratorios y explicó la alternativa selectiva.

**Autorización real posterior:** el usuario eligió **«Autorizar merge selectivo»**
en la pregunta que detallaba conservar el árbol de con-ia fuera de CentroTareas,
usar estrategia `ours` sin confirmar e incorporar únicamente el proyecto de main.

**Cambios aceptados:** merge de dos padres `28228eb`, con padres `52fbaa7` y
`29fc0f7`. Antes de confirmarlo se verificó que solo se incorporara
`Semana14/CentroTareas`, que coincidiera con main y que compilara.

**Cambios descartados:** renombrar o eliminar con-ia, reiniciarla, rebase,
force push y resolver por cuenta propia conflictos de CafeCampus, Catalogo
o CampusGPS.

**Justificación técnica:** el merge selectivo autorizado conserva ambas
historias, hace ancestros a los nueve commits de main y deja intacto el árbol
histórico de con-ia en los demás laboratorios. Por conservar ese historial,
`main..con-ia` no contiene únicamente cuatro commits: incluye los 20 antiguos,
el merge y los cuatro nuevos. Para estos últimos se usa `28228eb..con-ia`.

## 3. Mejora 1 — progreso en Repository con Flow

**Prompt utilizado (extractos del encargo inicial, no un nuevo mensaje):**

> Mover la lógica de progreso desde el ViewModel
> hacia el Repository.
>
> Implementar Flow.

> El Repository debe emitir avances.
> El ViewModel debe consumirlos.
> La pantalla debe observar el StateFlow.

**Respuesta resumida y cambios aceptados:** se añadió
`TareasRepository.progresoTarea(): Flow<Float>`. El repositorio usa
`flow { emit(...) }`, emite 0 y 100 avances con delay(40), y aplica
`flowOn(Dispatchers.Default)`. El ViewModel recoge en Main y mantiene Job,
mensajes, porcentaje, guardia de doble inicio y cancelación. La pantalla no
necesitó cambiar para esta mejora.

**Cambios descartados:** exponer estados visuales desde Repository, mover
Compose al ViewModel, mantener el bucle duplicado en ambas capas o crear otra
pantalla. Se retiró el log Dentro del antiguo bucle del ViewModel; permanecen
Antes y Después, sin afirmar que el collector se ejecuta en Default.

**Justificación técnica:** Flow frío inicia una operación por colección y
propaga cancelación al productor; flowOn cambia solo el trabajo upstream.
StateFlow sigue siendo la fuente de estado de la pantalla y se actualiza en Main.

**Verificación real:** compilación, regresión instrumentada y pruebas JVM de
101 emisiones crecientes, inicio en 0, fin en 1 y nueva colección desde cero.

Commit: `95a1d23 feat(ia): mueve el progreso al Repository usando Flow`.

## 4. Mejora 2 — progreso de primos

**Prompt utilizado (extractos del encargo inicial):**

> Durante el cálculo, mostrar avance porcentual.

> El cálculo debe continuar ejecutándose
> en Dispatchers.Default.

> Mantener:
>
> ensureActive()

**Respuesta resumida y cambios aceptados:** se incorporó
`contarPrimosConProgreso(): Flow<AvancePrimos>`, donde el dato contiene avance
y cantidad acumulada. `flowOn(Default)` mantiene el trabajo de CPU fuera de
Main y `currentCoroutineContext().ensureActive()` coopera en cada iteración.
La API suspend original toma el resultado final del mismo Flow, sin duplicar
el algoritmo. El ViewModel expone progresoPrimos y la UI muestra barra y porcentaje.

**Cambios descartados:** emitir un evento por cada número, retrasar el algoritmo
para hacer más vistosa la barra, calcular en Compose, cambiar los cinco
resultados de la guía o reemplazar la cancelación por espera bloqueante.

**Justificación técnica:** como máximo una emisión por porcentaje reduce el
tráfico de actualizaciones; usar Long en las operaciones evita overflow. Se
conserva el resultado acumulado final y se separan datos de cálculo de PrimosUi.

**Verificación real:** cinco resultados exactos, límites pequeños, progreso
creciente, cantidad final para 500000, cancelación y reinicio. El primer test
UI usó 2000000 y agotó la espera de Cancelado: el cálculo podía terminar
durante la captura. Se cambió la entrada de prueba a 20000000 y se exigió
Cancelar habilitado. Las repeticiones posteriores pasaron; no se marcó como
aprobada la ejecución fallida ni se agregó demora al código de producción.

Commit: `2bc33bb feat(ia): agrega progreso al calculo de numeros primos`.

## 5. Mejora 3 — comparación de tiempos

**Prompt utilizado (extractos del encargo inicial):**

> Registrar las mediciones reales de:
>
> - Una por una.
> - A la vez.

> No utilizar resultados inventados.

**Respuesta resumida y cambios aceptados:** se añadió `TiemposCarga` con dos
valores inicialmente nulos. El ViewModel reutiliza la misma duración medida
con System.currentTimeMillis para Exito y para la fila correspondiente.
La tarjeta Dos cargas muestra una tabla Material 3 sencilla. Las filas sin
ejecución exitosa muestran Sin medir; los errores no borran medidas anteriores.

**Cambios descartados:** valores constantes de 5000/3000 como supuestas medidas,
promedios no solicitados, gráficos, base de datos y modificaciones de la lógica
async o de las demoras simuladas.

**Justificación técnica:** dos últimas medidas exitosas permiten comparar los
modos sin guardar un historial innecesario; StateFlow las conserva durante
rotación. No se promete persistencia tras cierre o muerte del proceso.

**Verificación real:** valores inicialmente nulos, registro independiente de
ambos modos, igualdad con los resultados, conservación después de un fallo y
tabla con medidas reales. Última repetición directa: **5004 ms / 3001 ms**.
La captura `capturas/con-ia/07-comparacion-tiempos.png` muestra esos valores.

Commit: `a80de32 feat(ia): agrega comparacion de tiempos de carga`.

## 6. Comparación funcional y arquitectónica

| Área | MAIN | CON-IA |
| --- | --- | --- |
| Origen del progreso | ViewModel | Repository, Flow frío y flowOn(Default) |
| Primos | Cantidad al finalizar | Avance y cantidad, barra observable y misma API suspend |
| Tiempos | Resultado del último modo | Tabla con última ejecución exitosa de cada modo |
| UI | Tres tarjetas verticales Material 3 | Mismas tarjetas con barra adicional y tabla |
| MVVM | ViewModel administra estados y Jobs | Misma responsabilidad; Repository produce avances reutilizables |
| Pruebas reales | 4 JVM + 6 instrumentadas | 8 JVM + 7 instrumentadas |

Ambas ramas compilaron. La versión mejorada pasó las tres tarjetas, progreso,
cancelación, doble inicio, primos, validación, errores, reintentos y rotación.
Lint: 0 errores y 19 advertencias, detalladas en README; no se ocultaron.
Las capturas son reales del emulador, no imágenes generadas. Siguen pendientes
la revisión humana en dispositivo físico, accesibilidad y otros tamaños/API.

## 7. Documentación y límites del registro

El commit `docs(ia): documenta prompts y mejoras realizadas` agrega este archivo,
actualiza README y conserva las dos capturas de mejoras. Las evidencias main
no se reemplazan por capturas de con-ia. No se modificó main durante esta fase
ni se fusionó con-ia hacia main. No se agregaron dependencias en la Fase 2.
Las decisiones descritas provienen del encargo y de las autorizaciones reales
en esta sesión, no de respuestas atribuidas a Gemini ni de prompts ficticios.
