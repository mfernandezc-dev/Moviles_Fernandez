package com.tecsup.centrotareas

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import com.tecsup.centrotareas.model.CargaUi
import com.tecsup.centrotareas.model.PrimosUi
import com.tecsup.centrotareas.viewmodel.TareasViewModel
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PantallaTareasTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    private val vm: TareasViewModel
        get() = ViewModelProvider(compose.activity)[TareasViewModel::class.java]

    @Test
    fun progresoCancelacionDobleInicioYContador() {
        compose.onNodeWithText("Iniciar").performClick()
        compose.onNodeWithText("Iniciar").assertIsNotEnabled()
        compose.runOnIdle { vm.tareaLarga() }
        compose.waitUntil(6000) { vm.progreso.value >= 0.2f }
        compose.onNodeWithText("Tócame").performClick()
        compose.onNodeWithText("Toques: 1").assertTextEquals("Toques: 1")
        captura("01-progreso")
        compose.onAllNodesWithText("Cancelar")[0].performClick()
        compose.waitUntil(3000) { !vm.corriendo.value }
        compose.onNodeWithText("Cancelado").assertTextEquals("Cancelado")
        val conservado = vm.progreso.value
        assertTrue(conservado > 0f && conservado < 1f)
        captura("02-cancelado")
        compose.onNodeWithText("Iniciar").assertIsEnabled().performClick()
        compose.runOnIdle { assertTrue(vm.progreso.value < conservado) }
        compose.waitUntil(10000) { !vm.corriendo.value }
        assertEquals(1f, vm.progreso.value)
        compose.onNodeWithText("100 %").assertTextEquals("100 %")
        compose.onNodeWithText("Terminó").assertTextEquals("Terminó")
    }

    @Test
    fun primosValidacionResultadoYCancelacion() {
        val campo = compose.onNodeWithText("Hasta qué número")
        for (invalido in listOf("abc", "1", "2147483648")) {
            campo.performScrollTo().performTextReplacement(invalido)
            compose.onNodeWithText("Contar").assertIsNotEnabled()
        }
        campo.performTextReplacement("100000")
        Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Contar").performScrollTo().performClick()
        compose.waitUntil(10000) { vm.primos.value is PrimosUi.Listo }
        compose.onNodeWithText("Primos encontrados: 9592").performScrollTo()
            .assertTextEquals("Primos encontrados: 9592")
        captura("03-primos")
        campo.performScrollTo().performTextReplacement(Int.MAX_VALUE.toString())
        Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Contar").performScrollTo().performClick()
        compose.waitUntil(3000) { vm.primos.value is PrimosUi.Calculando }
        compose.onNodeWithText("Tócame").performScrollTo().performClick()
        compose.onNodeWithText("Toques: 1").assertTextEquals("Toques: 1")
        compose.onAllNodesWithText("Cancelar")[1].performScrollTo().performClick()
        compose.waitUntil(3000) { vm.primos.value is PrimosUi.Cancelado }
    }

    @Test
    fun cargasConTiemposReales() {
        compose.onNodeWithText("Una por una").performScrollTo().performClick()
        compose.onNodeWithText("Una por una").assertIsNotEnabled()
        compose.onNodeWithText("A la vez").assertIsNotEnabled()
        compose.waitUntil(15000) { vm.carga.value is CargaUi.Exito }
        val secuencial = vm.carga.value as CargaUi.Exito
        assertEquals("Una por una", secuencial.modo)
        assertEquals("Ana Torres (ficticia)", secuencial.perfil)
        assertEquals(3, secuencial.pedidos)
        assertTrue("Tiempo secuencial: ${secuencial.milisegundos}", secuencial.milisegundos in 4800..12000)
        compose.onNodeWithText("Pedidos: 3").performScrollTo()
        captura("04a-carga-secuencial")
        compose.onNodeWithText("A la vez").performScrollTo().performClick()
        compose.waitUntil(12000) { vm.carga.value is CargaUi.Exito }
        val paralela = vm.carga.value as CargaUi.Exito
        assertEquals("A la vez", paralela.modo)
        assertEquals(3, paralela.pedidos)
        assertTrue("Tiempo paralelo: ${paralela.milisegundos}", paralela.milisegundos in 2800..10000)
        assertTrue(paralela.milisegundos < secuencial.milisegundos)
        println("TIEMPOS_REALES secuencial=${secuencial.milisegundos} paralelo=${paralela.milisegundos}")
        compose.onNodeWithText("Pedidos: 3").performScrollTo()
        captura("04b-carga-paralela")
    }

    @Test
    fun erroresEnAmbosModosYReintento() {
        compose.onNodeWithContentDescription("Simular error").performScrollTo().performClick()
        compose.onNodeWithText("Una por una").performScrollTo().performClick()
        compose.waitUntil(15000) { vm.carga.value is CargaUi.Error }
        compose.onNodeWithText("No se pudo cargar: Sin conexión").performScrollTo()
            .assertTextEquals("No se pudo cargar: Sin conexión")
        compose.onNodeWithText("Reintentar").performScrollTo().performClick()
        compose.waitUntil(12000) { vm.carga.value is CargaUi.Error }
        compose.onNodeWithText("No se pudo cargar: Sin conexión").performScrollTo()
        captura("05-error-simulado")
        compose.onNodeWithContentDescription("Simular error").performScrollTo().performClick()
        compose.onNodeWithText("Reintentar").performScrollTo().performClick()
        compose.waitUntil(12000) { vm.carga.value is CargaUi.Exito }
        assertEquals("A la vez", (vm.carga.value as CargaUi.Exito).modo)
        compose.onNodeWithText("Pedidos: 3").performScrollTo().assertTextEquals("Pedidos: 3")
    }

    @Test
    fun rotacionConservaViewModelYEntrada() {
        compose.onNodeWithText("Tócame").performClick()
        compose.onNodeWithText("Hasta qué número").performScrollTo().performTextReplacement("500000")
        Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Iniciar").performScrollTo().performClick()
        val original = vm
        compose.activityRule.scenario.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        compose.waitUntil(10000) {
            compose.activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        }
        compose.waitForIdle()
        assertTrue(original === vm)
        assertEquals(1, vm.toques.value)
        compose.onNodeWithText("Hasta qué número").performScrollTo()
            .assertTextEquals("Hasta qué número", "500000")
        compose.waitUntil(10000) { !vm.corriendo.value }
        assertEquals(1f, vm.progreso.value)
        compose.activityRule.scenario.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        compose.waitUntil(10000) {
            compose.activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        }
    }

    private fun captura(nombre: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val carpeta = File(instrumentation.targetContext.getExternalFilesDir(null), "evidencias/main")
        check(carpeta.mkdirs() || carpeta.isDirectory)
        val imagen = checkNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(carpeta, "$nombre.png").outputStream().use {
            check(imagen.compress(Bitmap.CompressFormat.PNG, 100, it))
        }
        imagen.recycle()
    }
}
