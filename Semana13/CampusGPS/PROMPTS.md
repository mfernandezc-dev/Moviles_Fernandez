# CampusGPS — Registro de la fase con IA

## Datos académicos

- **Estudiante:** Michael Fernandez.
- **Institución:** Tecsup.
- **Curso:** Desarrollo de Aplicaciones Móviles.
- **Laboratorio:** Semana 13.
- **Proyecto:** CampusGPS.
- **Repositorio:** [Moviles_Fernandez](https://github.com/mfernandezc-dev/Moviles_Fernandez).
- **Rama de trabajo:** `con-ia`. Las ramas `main` y `sin-ia` no se modificaron durante estos trabajos.

## Objetivo

Mejorar la presentación y las posibilidades de consulta de CampusGPS con apoyo de IA: modernizar la interfaz, filtrar lugares por categoría y radio, y alternar el estilo claro/oscuro del mapa. Se conservan la ubicación del usuario, las distancias, la selección, el lugar más cercano y el manejo de permisos y errores.

El trabajo mantiene Kotlin, Jetpack Compose, Material 3, MVVM, StateFlow, Repository, MapLibre Compose y OpenFreeMap. No incorpora seguimiento continuo ni modifica las coordenadas originales de los lugares.

## Herramientas utilizadas y procedencia

| Herramienta | Uso y alcance de la información disponible |
| --- | --- |
| Warp | Terminal indicada por el estudiante como parte de su entorno de trabajo. No se registró su versión. |
| OpenCode | Agente utilizado en esta conversación para inspeccionar archivos, proponer cambios, implementarlos y ejecutar verificaciones con autorización. |
| Modelo de IA | Los metadatos del agente de esta sesión identifican **GPT-6.1 Sol**, de OpenAI, con identificador `openai/gpt-6.1-sol`. No se atribuye este trabajo a Gemini. |
| Android Studio | Entorno de desarrollo indicado por el estudiante. No se registró su versión en esta documentación. |
| Emulador Android | Validación de las pantallas y de las mejoras. En la comprobación asistida del modo oscuro se utilizó el dispositivo `emulator-5554`. |
| Gradle y Git | Compilación de depuración, revisión de diferencias y publicación de las mejoras en `origin/con-ia`, en etapas autorizadas anteriores. |

## Registro de prompts

**Criterio de honestidad:** los cuatro prompts proceden de los mensajes reales del estudiante en esta conversación. Los bloques de la siguiente sección son **extractos literales**, no los prompts íntegros. Las columnas de respuestas, cambios y justificación son síntesis técnicas elaboradas para este documento; no son transcripciones de respuestas de IA ni conversaciones reconstruidas.

| Número | Objetivo | Prompt utilizado | Respuesta o propuesta de la IA | Cambios aplicados o descartados | Justificación |
| --- | --- | --- | --- | --- | --- |
| 1 | Modernizar la interfaz sin alterar sus funciones. | [Extracto literal del prompt de implementación visual](#prompt-1). | Síntesis: definir una paleta propia, ordenar los componentes y respetar los márgenes del sistema sin cambiar MVVM. | Aplicados: encabezado y botón azules, tarjetas grises, distancias azules, borde de selección, errores rosados, botones secundarios delineados y marcador rojo del usuario. Se desactivó la sustitución de la paleta por colores dinámicos. No se añadieron filtros en esta etapa. | Acercar la presentación a los criterios de la guía, conservando la lógica y las dependencias existentes. |
| 2 | Mostrar lugares por categoría. | [Extracto literal del prompt de categorías](#prompt-2). | Síntesis: exponer la categoría seleccionada y una colección filtrada mediante StateFlow; usar la misma colección en mapa y lista. | Aplicados: chips Todos, Estudio, Comida, Eventos y Servicios; inicio en Todos; filtrado mediante `combine`; validación de selección y más cercano dentro del filtro. No se modificaron los repositorios. | Evitar que mapa y lista presenten resultados diferentes y mantener las decisiones de filtrado en el ViewModel. |
| 3 | Limitar resultados por distancia, además de categoría. | [Extracto literal del prompt de radio](#prompt-3). | Síntesis: combinar radio y categoría sobre las distancias calculadas e inspeccionar las APIs instaladas antes de representar el área. | Aplicados: radios 100/200/500 m, inicio en 500 m, comparación inclusiva de distancia, mensaje sin resultados y círculo aproximado mediante un polígono geográfico de 96 segmentos. Se descartó utilizar un círculo de tamaño fijo en píxeles. | El filtro reutiliza `distanciaMetros` y conserva el orden. El polígono cambia su tamaño en pantalla con el zoom, sin confundir píxeles con metros. |
| 4 | Alternar únicamente el mapa entre Claro y Oscuro. | [Extracto literal del prompt de modo oscuro](#prompt-4). | Síntesis: comprobar un estilo oscuro real de OpenFreeMap y actualizar `BaseStyle` sin recrear innecesariamente el estado del mapa. | Aplicados: selector Claro/Oscuro, inicio en Claro, StateFlow del modo, estilos Liberty/Dark y aviso con intento de retorno a Claro ante un fallo del estilo oscuro. No se cambió el tema completo de CampusGPS. | Conservar cámara, selección, marcadores y filtros, evitando depender de URLs o APIs inventadas. |

### Prompt 1

Trabajo: mejora visual. Extracto literal del mensaje de autorización para implementar; se omiten otras instrucciones del prompt original.

```text
Actualizar la interfaz de CampusGPS para que se parezca
lo máximo posible a las pantallas de referencia de la guía.

La aplicación debe seguir funcionando exactamente igual,
pero con una presentación visual más moderna y ordenada.
```

Resultado verificable: cambios en `PantallaMapa.kt`, `ListaLugares.kt`, `MapaUbicacion.kt`, `MensajeError.kt`, `ui/theme/Color.kt` y `ui/theme/Theme.kt`.

### Prompt 2

Trabajo: filtros por categoría. Extracto literal del objetivo y de las categorías del mensaje original.

```text
Implementar filtros por categoría para mostrar
lugares específicos del campus.

Esta es la primera mejora funcional de la fase con IA.

CATEGORÍAS:

- Todos
- Estudio
- Comida
- Eventos
- Servicios
```

Resultado verificable: `categoriaSeleccionada` y `lugaresFiltrados` en `MapaViewModel`, junto con FilterChip en `PantallaMapa`. Se limpia la selección si el lugar deja de pertenecer al filtro.

### Prompt 3

Trabajo: selector de radio. Extracto literal del objetivo del mensaje original.

```text
Implementar la segunda mejora funcional con IA:

SELECTOR DE RADIO DE BÚSQUEDA.
```

El prompt completo también exigía radios de 100, 200 y 500 metros, combinación con categorías, conservación de distancias y verificación de las APIs de MapLibre. Esta frase es una síntesis de esos requisitos, no una cita textual.

Resultado verificable: `radioSeleccionado`, la función `seleccionarRadio` y el filtrado conjunto en el ViewModel. Solo se incluyen lugares con distancia calculada menor o igual al radio. Una nueva búsqueda recalcula distancias y valida la selección.

Para el área se verificó MapLibre Compose **0.20.0**. `CircleLayer` expresa su radio en dp; se emplearon `GeoJsonData`, `rememberGeoJsonSource`, `FillLayer` y `LineLayer`. El anillo tiene 96 segmentos y ajusta sus vértices usando `Location.distanceBetween`, la misma API WGS84 utilizada por `distanciaMetros`. Sigue siendo una aproximación poligonal, no una circunferencia matemática perfecta.

Durante la compilación se corrigieron el cierre tipado del anillo GeoJSON y la declaración de las capas: pertenecen al contenido de `rememberMapState`, no al overlay de componentes Compose.

### Prompt 4

Trabajo: modo oscuro. Extracto literal del objetivo del mensaje original.

```text
Implementar la tercera mejora funcional con IA:

MODO OSCURO DEL MAPA.
```

El prompt completo también pedía inicio en Claro, StateFlow, conservación de cámara y filtros, un estilo oscuro verificable y no cambiar el tema general. Esta enumeración es una síntesis, no una transcripción.

Resultado verificable: `modoOscuroMapa` y `mensajeEstiloMapa` en el ViewModel; `PantallaMapa` observa ambos estados y transmite los eventos. `MapaUbicacion` recibe el modo y actualiza `BaseStyle.Uri` sobre el mismo estado recordado del mapa.

- Claro: [OpenFreeMap Liberty](https://tiles.openfreemap.org/styles/liberty).
- Oscuro: [OpenFreeMap Dark](https://tiles.openfreemap.org/styles/dark).
- Fuente de identificación del estilo: [guía oficial de OpenFreeMap](https://openfreemap.org/quick_start/).

Antes de implementar se comprobó que Dark devuelve HTTP 200, declara un estilo v8 y dispone de fuentes, sprites y glifos accesibles. Después se verificó su renderizado en el emulador. La notificación de `StyleLoadState.Failed` se comunica al ViewModel, que solicita Claro si falla Oscuro.

## Trazabilidad de las mejoras publicadas

| Trabajo | Commit en `con-ia` |
| --- | --- |
| Mejora visual | [`e3690a2` — feat(ui): mejora diseño visual de CampusGPS con IA](https://github.com/mfernandezc-dev/Moviles_Fernandez/commit/e3690a20116082798c4268749a74cbc1ae114da3) |
| Categorías | [`caa6452` — feat(ia): agrega filtros de lugares por categoria](https://github.com/mfernandezc-dev/Moviles_Fernandez/commit/caa64523c18e554bb2bbe5135b0c4dc419b7b3b1) |
| Radio | [`ba6f9c1` — feat(ia): agrega filtro por radio de distancia](https://github.com/mfernandezc-dev/Moviles_Fernandez/commit/ba6f9c11022c8bff1f725ad5959c4679d5dda5a9) |
| Modo oscuro | [`f7ec6aa` — feat(ia): agrega modo oscuro al mapa](https://github.com/mfernandezc-dev/Moviles_Fernandez/commit/f7ec6aa00648595288400bd6f464ebe52a3f5f63) |

Estos commits corresponden a las implementaciones previas. La preparación de este documento y de sus referencias no crea un commit nuevo.

## Conservación de MVVM

- **ViewModel:** administra ubicación, categoría, radio, modo del mapa, selección y resultados mediante StateFlow. `combine` deriva `lugaresFiltrados`; el más cercano se obtiene de esa colección ordenada.
- **Vista:** `PantallaMapa` observa los estados y envía las acciones del usuario al ViewModel. No contiene la lógica de filtrado por categoría o distancia.
- **Componentes:** `ListaLugares` y `MapaUbicacion` reciben la misma colección visible. El mapa presenta marcadores, cámara, estilo y área de búsqueda, y comunica fallos de estilo sin decidir localmente el modo seleccionado.
- **Datos:** se mantienen Repository, modelos, coordenadas y función original `distanciaMetros`. No se añadieron dependencias para estas mejoras.
- **Estados iniciales:** Todos, 500 metros y mapa Claro. El modo del mapa es independiente del tema general de Material 3.

## Pruebas realizadas y alcance

Las siguientes verificaciones proceden de la conversación de implementación, de las comprobaciones asistidas y de las capturas aportadas. Una imagen no demuestra por sí sola todos los casos de una función.

| Comprobación | Resultado registrado y evidencia |
| --- | --- |
| Compilación de las cuatro mejoras | En las etapas de implementación se ejecutó `.\gradlew.bat :app:assembleDebug` y se obtuvo **BUILD SUCCESSFUL**. En esta tarea documental no se recompiló ni se cambió código. |
| Inicio y presentación | Se revisaron encabezado, botón y mensaje inicial. [Inicio](capturas/con-ia/con-ia-Inicio.png). |
| Ubicación, marcadores y lista | Las capturas muestran la posición roja, el lugar más cercano verde, otros lugares azules y distancias ascendentes visibles. [Mapa](capturas/con-ia/con-ia-Mapa.png) y [lista](capturas/con-ia/con-ia-Lista.png). |
| Categorías | El estudiante informó haber probado la mejora en el emulador. Durante la sesión asistida posterior se verificó Eventos con radio de 100 m. La [captura de filtros](capturas/con-ia/con-ia-Filtros.png) muestra la fila y Todos seleccionado; no documenta por sí sola el resultado individual de las cinco categorías. |
| Radio | El estudiante informó haber probado los radios. La [captura de búsqueda](capturas/con-ia/con-ia-BusquedaRadio.png) muestra 200 m y el área azul. En la sesión asistida también se verificaron Eventos y el círculo de 100 m al cambiar el estilo. |
| Claro → Oscuro → Claro | Comprobado en el emulador durante la sesión asistida, conservando cámara, Auditorio seleccionado, marcador verde, posición roja y círculo. La [captura oscura](capturas/con-ia/con-ia-ModoOscuro.png) muestra el mapa oscuro con radio de 100 m y la interfaz general clara. |
| Permiso denegado | La [captura sin permiso](capturas/con-ia/con-ia-SinPermiso.png) muestra el error rosado y la acción Abrir ajustes. |
| GPS desactivado | La [captura de GPS](capturas/con-ia/con-ia-ActivarGPS.png) muestra el mensaje para activar GPS y el botón Reintentar; no representa una activación automática. |
| Revisión de diferencias | En las etapas anteriores se ejecutó `git diff --check` sin errores de espacios. Se observaron avisos de conversión LF/CRLF. |

### Límites y verificaciones pendientes

- La recuperación automática ante una caída del servidor del estilo oscuro está implementada, pero **no se reprodujo ese fallo** en la sesión asistida. No se presenta como una prueba superada.
- Se conserva el manejo de Google Play Services no disponible, pero no se aporta una captura nueva de ese caso en `capturas/con-ia/`.
- Las capturas no documentan exhaustivamente todos los cruces entre categorías y radios, todos los límites de distancia ni todos los tamaños de pantalla. No se afirma disponer de una batería automatizada de pruebas de estas mejoras.
- La compilación advirtió que AGP 9.2.1 fue probado hasta SDK 37.0 y el proyecto usa 37.1. No impidió generar el APK; no se cambió la configuración para ocultar el aviso.

## Evidencias gráficas

Las ocho imágenes ya existían en `capturas/con-ia/` al iniciar esta tarea. Se revisaron sin copiarlas, moverlas, sobrescribirlas ni generar imágenes nuevas. Las capturas originales de la carpeta superior `capturas/` también se conservaron.

**Son evidencias progresivas de la fase con IA, no ocho pantallas de una única compilación final.** Inicio, mapa, lista y errores corresponden a la mejora visual; filtros incorpora categorías; búsqueda añade el radio; modo oscuro incorpora el último selector. Algunas imágenes son recortes de componentes.

| Archivo | Contenido observado |
| --- | --- |
| [con-ia-Inicio.png](capturas/con-ia/con-ia-Inicio.png) | Encabezado azul, botón de ubicación y mensaje inicial; todavía no aparecen los selectores posteriores. |
| [con-ia-Mapa.png](capturas/con-ia/con-ia-Mapa.png) | Mapa claro, coordenadas, marcadores y atribuciones. |
| [con-ia-Lista.png](capturas/con-ia/con-ia-Lista.png) | Recorte de la lista con tarjetas, categorías, distancias azules e indicador verde del más cercano. |
| [con-ia-Filtros.png](capturas/con-ia/con-ia-Filtros.png) | Fila horizontal de categorías, Todos seleccionado, mapa y lista. |
| [con-ia-BusquedaRadio.png](capturas/con-ia/con-ia-BusquedaRadio.png) | Radio de 200 m seleccionado, círculo geográfico y resultados visibles. |
| [con-ia-ModoOscuro.png](capturas/con-ia/con-ia-ModoOscuro.png) | Modo Oscuro, radio de 100 m, círculo y marcadores sobre el estilo oscuro. |
| [con-ia-SinPermiso.png](capturas/con-ia/con-ia-SinPermiso.png) | Permiso denegado, contenedor rosado y botón Abrir ajustes. |
| [con-ia-ActivarGPS.png](capturas/con-ia/con-ia-ActivarGPS.png) | GPS apagado, mensaje informativo y botón Reintentar. |

No faltan archivos de la lista de evidencias solicitada. Los enlaces de esta sección son relativos a `PROMPTS.md`, para que funcionen dentro del repositorio.

## Conclusión del estudiante

En esta fase aprendí a utilizar la IA como apoyo para mejorar una aplicación sin reemplazar su arquitectura. Pude separar los estados y filtros del ViewModel de la presentación en Compose, y comprobé la importancia de revisar las APIs reales antes de incorporar funciones al mapa. Las pruebas en el emulador y el control de cambios me ayudaron a conservar lo que ya funcionaba. También identifiqué que una función implementada, una captura y una prueba completa no significan lo mismo; por eso dejo explícitas las verificaciones pendientes.

Esta conclusión es una propuesta de redacción en primera persona para que el estudiante la revise y confirme antes de la entrega.
