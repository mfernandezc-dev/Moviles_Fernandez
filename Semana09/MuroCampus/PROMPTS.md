# PROMPTS — MuroCampus, Fase 2

- Proyecto: Semana09/MuroCampus. Rama: `con-ia` existente.
- Herramienta utilizada: **OpenCode**. No se utilizó Gemini.
- Objetivo: búsqueda local, eliminación DELETE y edición PATCH, en exactamente
  tres avances probados y publicados de forma secuencial.
- Fuente: un único prompt maestro real del usuario; las secciones siguientes
  citan fragmentos de ese prompt, no intercambios independientes inventados.
- Las adaptaciones descritas son decisiones del agente OpenCode. No se atribuye
  al estudiante una revisión o modificación que no haya realizado personalmente.
  **El estudiante debe revisar, comprender y validar el código y este análisis antes de entregarlos.**

## MEJORA A — Búsqueda

### Fragmentos reales del prompt maestro (secciones 11–18)

> Implementar una búsqueda local de las publicaciones descargadas, compatible con los filtros existentes.
> No realizar consultas HTTP por cada carácter ingresado.
> Preferentemente utilizar combine y stateIn, manteniendo una fuente de verdad para las publicaciones descargadas.
> Preservar el estado lista utilizado por las funcionalidades y pruebas anteriores, o adaptar sus observadores sin perder sus contratos ni comportamiento.

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué |
| --- | --- | --- | --- |
| Secciones 11–15: búsqueda local por `title`, StateFlow, filtros y conservación de `lista`. | OpenCode implementó `busqueda`, `buscar` y `resultados` mediante `combine`/`stateIn`; la pantalla observa el resultado derivado. | El agente conservó `lista` sin filtrarla ni reemplazarla; no hubo una intervención posterior del estudiante. | Evita destruir los datos descargados o realizar GET por carácter; preserva los contratos previos. |
| Sección 14: OutlinedTextField, icono, limpieza y accesibilidad. | OpenCode incorporó campo Material 3, etiqueta y botón accesible. | El agente sustituyó su intento inicial de usar Material Icons por dos vectores locales: la compilación informó que ese módulo no estaba disponible. | Cumplir el diseño sin agregar dependencias; la compilación posterior fue correcta. |
| Secciones 15–18: pruebas, emulador y evidencia real. | OpenCode agregó 16 pruebas unitarias y 6 Compose y verificó la aplicación instalada mediante ADB/Logcat. | El agente corrigió una expectativa del script externo: limpiar recupera la lista conservando su posición, no garantiza mostrar el primer elemento. No se modificó la app para fabricar evidencia. | La lista conserva LazyListState para navegación; la comprobación debía respetar ese comportamiento. |

### Archivos y decisiones efectivas

- `viewmodel/PostsViewModel.kt`: búsqueda local derivada; `SharingStarted.Eagerly`
  mantiene el resultado actualizado durante la sesión del ViewModel.
- `ui/PantallaLista.kt`: buscador sobre chips y mensaje sin coincidencias;
  colección con `collectAsStateWithLifecycle`.
- `res/drawable/ic_search.xml`, `ic_close.xml`: iconos locales sin dependencias.
- `BusquedaViewModelTest.kt`, `BusquedaTest.kt`: casos controlados y deterministas.
- README, este archivo y la captura seleccionada; importación exacta de la base
  dentro del primer commit, sin merge de otras semanas.

### Resultados y alcance

179 unitarias, 46 instrumentadas; cero fallos, errores u omitidas; las 203 base
conservadas. Assemble, lint y repetición offline correctos. Lint mantiene 24
advertencias y cero errores. La prueba Compose de restauración recrea la composición
con ViewModel retenido; adicionalmente se comprobó una rotación real del emulador.

Aplicación real: 100 posts en Todos y 10 en Usuario 2; `qui`/`QUI`, limpieza,
sin coincidencias, detalle/regreso, rotación, fallo de red y reintento. Logcat
confirma que escribir no genera GET adicionales. Se restauraron conectividad y
rotación. Evidencia: [busqueda.png](evidencias/con-ia/busqueda.png).

### Para sustentación

El filtro de usuario descarga su conjunto mediante Query; la búsqueda solo deriva
una vista local por título. Por eso cambiar de filtro puede hacer un GET, pero
escribir no. StateFlow del ViewModel sobrevive a la rotación, no a la muerte del
proceso; no se promete persistencia permanente.
