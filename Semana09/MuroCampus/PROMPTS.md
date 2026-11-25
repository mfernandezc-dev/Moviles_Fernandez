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

## MEJORA B — Eliminación

### Fragmentos reales del prompt maestro (secciones 19–29)

> La nueva operación deberá utilizar Response<Unit> para comprobar explícitamente el resultado HTTP, tal como solicita la guía.
> El ViewModel no debe conocer Response<Unit> ni HttpException.
> No eliminar localmente antes de confirmar el éxito HTTP.
> Aplicar esa exclusión a los resultados posteriores de listar y porUsuario.

El usuario añadió posteriormente una autorización real para continuar tras el cambio
externo: «Autorizo continuar sin integrar ese cambio externo en main local ni en
con-ia, siempre que se compruebe que únicamente corresponde a documentación de MuroCampus».
OpenCode verificó padre y diff; solo cambió el README remoto y no se integró.

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué |
| --- | --- | --- | --- |
| Secciones 20–22: DELETE, Result y estados independientes. | OpenCode implementó el contrato, comprobación HTTP en Repository y Job/versión propios en ViewModel. | Decisión del agente: registrar IDs solo después del éxito y aplicarlos también a respuestas de lista pendientes. | Evita desaparición optimista y reaparición por recarga de JSONPlaceholder. |
| Secciones 23–28: diálogo, errores, pruebas y evidencia. | OpenCode incorporó confirmación, bloqueo de duplicados, retorno y mensaje de éxito; agregó 21 unitarias y 4 Compose. | El agente corrigió dos problemas de sus pruebas: sobrecarga ambigua de `Response.success` y un Proxy que envolvía IOException. Usó tipo explícito y fake de PostApi explícito. | Probar cancelación/excepción original sin alterar producción para satisfacer un test defectuoso. |
| Autorización adicional: continuar sin integrar README externo. | OpenCode comprobó `544e1ca…` como hijo directo de `8a67a45…`, con un solo archivo documental. | No hubo pull, merge, rebase, reset ni push a main. | Respeta el trabajo externo y la base local exigida. |

Archivos principales: `PostApi.kt`, `PostRepository.kt`, `PostsViewModel.kt`,
`PantallaDetalle.kt`, `PantallaLista.kt`, `MainActivity.kt`; contrato y todos los
fakes existentes adaptados con fallos explícitos para operaciones inesperadas.
Nuevas suites: `EliminacionRepositoryTest`, `EliminacionViewModelTest`, `EliminacionTest`.

Resultados: **200 unitarias, 50 instrumentadas**, cero fallos/errores/omitidas;
assemble, lint (24 advertencias/0 errores) y repetición offline correctos. HTTP real
desde MuroCampus: `DELETE /posts/1` → **200**, `{}`; cancelar no generó DELETE.
Se verificó exclusión con búsqueda/filtros/recarga; id 2 falló offline y el reintento
voluntario conectado fue exitoso. Registros completos externos; ajustes restaurados.

Evidencias: [confirmación](evidencias/con-ia/eliminar-confirmacion.png),
[éxito](evidencias/con-ia/eliminar-exito.png).

Para sustentación: éxito HTTP y persistencia son garantías diferentes. JSONPlaceholder
simula DELETE; el conjunto de IDs en el ViewModel evita reapariciones solo durante
esa sesión. Cancelar un diálogo no inicia una corrutina ni llama al Repository.
Todas estas decisiones corresponden al agente y requieren revisión del estudiante.

## MEJORA C — Edición

### Fragmentos reales del prompt maestro (secciones 30–45)

> Este modelo representa exclusivamente el campo que se desea modificar.
> Utilizar el Post realmente devuelto por el servidor.
> Actualizar el detalle actual.
> Conservar los comentarios.
> Las eliminaciones locales tendrán prioridad sobre las actualizaciones: una publicación eliminada no debe reaparecer por una actualización almacenada.
> No ejecutar PATCH automáticamente al escribir.
> No ejecutar PATCH automáticamente al rotar.

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué |
| --- | --- | --- | --- |
| Secciones 31–34: EditarPost, PATCH, Result y actualización de sesión. | OpenCode agregó modelo, contrato de siete operaciones, delegación y estado/Job/versión de edición. | El agente guarda el Post recibido por ID; no reconstruye una respuesta a partir del formulario. La eliminación retira el override y cancela edición pendiente. | Respeta la respuesta real, conserva comentarios y evita reapariciones por carreras de solicitudes. |
| Secciones 35–38: formulario, navegación, validación y pruebas. | OpenCode creó PantallaEditar, borrador saveable y acciones diferenciadas; agregó 28 unitarias y 7 Compose. | El agente corrigió dos expectativas Compose nuevas: texto de referencia y cuerpo compartían el mismo literal; un Back con teclado visible puede cerrar primero el teclado. Se verificó retorno explícito al detalle. | No debilitar las pruebas anteriores ni cambiar producción para ocultar errores de selección/teclado en el test. |
| Secciones 39–40: título exacto acentuado, Logcat y capturas reales. | OpenCode ejecutó PATCH /posts/2 desde MuroCampus, obtuvo HTTP 200 y comparó respuesta, título, id, autor y cuerpo con GET original. | El agente utilizó Gboard real para `í`, tras ajustar el teclado flotante y corregir intentos de selección del acento. Ninguno de esos borradores incorrectos se guardó por PATCH. | ADB input text no permite esa entrada Unicode en este AVD; no se sustituyó el título solicitado ni se manipuló estado de producción. |
| Secciones 43–49 y autorización adicional: cierre, checkout e integridad. | OpenCode preparó comprobaciones de pruebas, arquitectura, archivos y comparación de APK recompiladas por rama después del tercer push. | Sin cuarto commit; registros de cierre y capturas comparativas fuera de Git. Mantener main local/remota diferentes está expresamente autorizado. | Evita reescritura de trabajo externo y respeta exactamente tres avances. El resultado posterior se entrega en el informe final, no se anticipa aquí. |

### Archivos y decisiones

`Modelos.kt`, `PostApi.kt`, `PostRepository.kt`, `PostsViewModel.kt`,
`PantallaEditar.kt`, `PantallaDetalle.kt`, `MainActivity.kt`, pruebas de contrato y
todos los fakes ampliados explícitamente. Nuevas suites `EdicionRepositoryTest`,
`EdicionViewModelTest`, `EdicionTest`; README y dos capturas reales actualizados.

El formulario no llama a la red al escribir. Guardar valida en UI y ViewModel;
el fallo mantiene el borrador, el éxito aplica el resultado, y volver no recarga la
lista. `rememberSaveable` conserva el formulario en recreación y el ViewModel
mantiene la sesión. No se agregó almacenamiento permanente ni nuevas dependencias.

### Resultados reales y controlados

- **228 unitarias + 57 instrumentadas = 285 únicas**, 65/17 nuevas frente a base;
  cero fallos, errores u omitidas. Assemble/lint/offline correctos, 24 advertencias
  y cero errores de lint. Se mantienen las comprobaciones de las 225 pruebas de
  búsqueda y las de DELETE, además de las 203 base.
- Pruebas JVM de HTTP local: Retrofit real envía PATCH con solo `title`, interpreta
  Post y HTTP 500; DELETE verifica método/Response. Son pruebas locales controladas,
  no solicitudes al proveedor. Los fakes verifican estados, delegación, cancelación,
  duplicados, respuestas obsoletas, filtros, búsqueda y prioridad de eliminación.
- Emulador real: PATCH `/posts/2` con `{"title":"Título actualizado desde MuroCampus"}`
  → **200**, Post con `id=2`, `userId=1` y body original. Cinco comentarios
  conservados; búsqueda del título nuevo, filtros, recarga/detalle y DELETE de id 1
  funcionando en la misma sesión. Error offline conserva formulario y reintento
  conectado exitoso. Rotación real sin PATCH automático. Ajustes restaurados.
- [Formulario](evidencias/con-ia/editar-formulario.png) y
  [éxito](evidencias/con-ia/editar-exito.png); logs/JSON/capturas complementarias externos.

### Para sustentación y revisión personal

PATCH modifica parcialmente, por eso EditarPost solo contiene title; PUT sería una
sustitución completa. Repository abstrae Retrofit y las excepciones HTTP. El mapa
de sesión permite seguir viendo el resultado simulado de JSONPlaceholder, pero no
demuestra persistencia remota. La exclusión por eliminación se aplica antes de las
actualizaciones. Las guardas de versión/cancelación evitan resultados de pantallas
obsoletas y los estados de cada escritura son independientes.

No se inventaron prompts adicionales ni decisiones del estudiante. El estudiante
debe revisar personalmente las tres mejoras, pruebas, limitaciones y explicación
técnica antes de la entrega; este registro no certifica que ya lo haya hecho.
