# Laboratorio 02 - Carrito de Compras en Kotlin

## Datos del estudiante

**Nombre:** Michael Fernandez


## Prompt
Actúa como profesor de Kotlin y ayúdame a transformar mi carrito de compras básico en una versión con POO. Estoy trabajando en la rama `con-ia` a partir de la rama `sin-ia`. Quiero mantener todo en un solo archivo `Carrito.kt` y con nivel de estudiante. Implementa clases, encapsulamiento, herencia, abstracción y polimorfismo: crea una clase abstracta `Producto` con dos categorías (`ProductoElectronico` y `ProductoRopa`), una clase `Carrito` para gestionar productos, una clase `Cliente`, una clase `Factura` para calcular subtotal, IGV, descuentos y total final. Además, modifica el `main` para agregar un menú interactivo por consola donde el usuario pueda mostrar catálogo, agregar productos, eliminar productos, ver carrito y ver resumen de compra. Explícame los cambios paso a paso y evita agregar funcionalidades innecesarias.


## Descripción del proyecto

Este proyecto consiste en desarrollar un carrito de compras utilizando Kotlin mediante programación por consola.

En esta versión se realizó una mejora aplicando Programación Orientada a Objetos (POO), implementando clases, herencia, abstracción y polimorfismo para organizar mejor la lógica del sistema.

El programa permite gestionar un carrito de compras mediante un menú interactivo donde el usuario puede mostrar productos disponibles, agregar productos, eliminar productos y visualizar el resumen final de compra con IGV y descuentos.


## Programación Orientada a Objetos implementada

### Clase abstracta Producto

Se creó una clase base abstracta `Producto` que contiene los atributos principales del producto y define métodos que son implementados por sus clases hijas.

```kotlin
abstract class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
)