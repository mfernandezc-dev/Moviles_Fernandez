package com.fernandez.murocampus.model

import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelosTest {
    private val jsonTolerante = Json { ignoreUnknownKeys = true }

    private val postJson = """
        {
            "userId": 1,
            "id": 2,
            "title": "Titulo de prueba",
            "body": "Contenido de prueba"
        }
    """.trimIndent()

    private val comentarioJson = """
        {
            "postId": 2,
            "id": 3,
            "name": "Comentario de prueba",
            "email": "persona@example.test",
            "body": "Contenido del comentario"
        }
    """.trimIndent()

    private val nuevoPostJson = """
        {
            "userId": 1,
            "title": "Titulo de prueba",
            "body": "Contenido de prueba"
        }
    """.trimIndent()

    private val usuarioJson = """
        {
            "id": 1,
            "name": "Usuario de prueba",
            "email": "usuario@example.test"
        }
    """.trimIndent()

    private val postEsperado = Post(1, 2, "Titulo de prueba", "Contenido de prueba")
    private val comentarioEsperado = Comentario(
        2, 3, "Comentario de prueba", "persona@example.test", "Contenido del comentario"
    )
    private val nuevoPostEsperado = NuevoPost(1, "Titulo de prueba", "Contenido de prueba")
    private val usuarioEsperado = Usuario(1, "Usuario de prueba", "usuario@example.test")

    @Test
    fun deserializaPost() {
        assertEquals(postEsperado, Json.decodeFromString<Post>(postJson))
    }

    @Test
    fun deserializaListaDePublicaciones() {
        val json = """
            [
                $postJson,
                {"userId": 4, "id": 5, "title": "Otro titulo", "body": "Otro contenido"}
            ]
        """.trimIndent()

        assertEquals(
            listOf(postEsperado, Post(4, 5, "Otro titulo", "Otro contenido")),
            Json.decodeFromString<List<Post>>(json)
        )
    }

    @Test
    fun deserializaComentario() {
        assertEquals(comentarioEsperado, Json.decodeFromString<Comentario>(comentarioJson))
    }

    @Test
    fun deserializaListaDeComentarios() {
        val json = """
            [
                $comentarioJson,
                {
                    "postId": 2,
                    "id": 6,
                    "name": "Otro comentario",
                    "email": "otra@example.test",
                    "body": "Otra respuesta"
                }
            ]
        """.trimIndent()

        assertEquals(
            listOf(
                comentarioEsperado,
                Comentario(2, 6, "Otro comentario", "otra@example.test", "Otra respuesta")
            ),
            Json.decodeFromString<List<Comentario>>(json)
        )
    }

    @Test
    fun serializaNuevoPost() {
        val resultado = Json.encodeToString(nuevoPostEsperado)

        assertEquals(Json.parseToJsonElement(nuevoPostJson), Json.parseToJsonElement(resultado))
    }

    @Test
    fun nuevoPostNoIncluyeId() {
        val objeto = Json.encodeToJsonElement(nuevoPostEsperado).jsonObject

        assertFalse(objeto.containsKey("id"))
        assertEquals(setOf("userId", "title", "body"), objeto.keys)
    }

    @Test
    fun deserializaUsuario() {
        assertEquals(usuarioEsperado, Json.decodeFromString<Usuario>(usuarioJson))
    }

    @Test
    fun ignoraCamposAdicionalesCuandoSeConfiguraExplicitamente() {
        assertEquals(postEsperado, jsonTolerante.decodeFromString<Post>(conCampoAdicional(postJson)))
        assertEquals(
            comentarioEsperado,
            jsonTolerante.decodeFromString<Comentario>(conCampoAdicional(comentarioJson))
        )
        assertEquals(
            nuevoPostEsperado,
            jsonTolerante.decodeFromString<NuevoPost>(conCampoAdicional(nuevoPostJson))
        )
        assertEquals(
            usuarioEsperado,
            jsonTolerante.decodeFromString<Usuario>(conCampoAdicional(usuarioJson))
        )
    }

    @Test
    fun rechazaCadaCampoObligatorioAusente() {
        assertCamposObligatorios<Post>(postJson)
        assertCamposObligatorios<Comentario>(comentarioJson)
        assertCamposObligatorios<NuevoPost>(nuevoPostJson)
        assertCamposObligatorios<Usuario>(usuarioJson)
    }

    @Test
    fun conservaNombresYTiposDelContrato() {
        assertContrato(
            Json.encodeToJsonElement(postEsperado).jsonObject,
            setOf("userId", "id"), setOf("title", "body")
        )
        assertContrato(
            Json.encodeToJsonElement(comentarioEsperado).jsonObject,
            setOf("postId", "id"), setOf("name", "email", "body")
        )
        assertContrato(
            Json.encodeToJsonElement(nuevoPostEsperado).jsonObject,
            setOf("userId"), setOf("title", "body")
        )
        assertContrato(
            Json.encodeToJsonElement(usuarioEsperado).jsonObject,
            setOf("id"), setOf("name", "email")
        )
    }

    @Test
    fun rechazaTiposIncorrectosEnCadaCampo() {
        assertTiposIncorrectos<Post>(postJson)
        assertTiposIncorrectos<Comentario>(comentarioJson)
        assertTiposIncorrectos<NuevoPost>(nuevoPostJson)
        assertTiposIncorrectos<Usuario>(usuarioJson)
    }

    @Test
    fun rechazaNullEnCadaCampoObligatorio() {
        assertCamposNoNulos<Post>(postJson)
        assertCamposNoNulos<Comentario>(comentarioJson)
        assertCamposNoNulos<NuevoPost>(nuevoPostJson)
        assertCamposNoNulos<Usuario>(usuarioJson)
    }

    @Test
    fun conservaUnicodeComillasYEscapesEnNuevoPost() {
        val original = NuevoPost(7, "Publicación \"especial\"", "Línea uno\nLínea dos\\fin")

        assertEquals(original, Json.decodeFromString<NuevoPost>(Json.encodeToString(original)))
    }

    @Test
    fun modoEstrictoRechazaCamposAdicionales() {
        assertThrows(SerializationException::class.java) {
            Json.decodeFromString<Usuario>(conCampoAdicional(usuarioJson))
        }
    }

    private fun conCampoAdicional(json: String): String {
        val objeto = Json.parseToJsonElement(json).jsonObject
        return JsonObject(objeto + ("extra" to JsonPrimitive("campo no modelado"))).toString()
    }

    private fun assertContrato(objeto: JsonObject, enteros: Set<String>, textos: Set<String>) {
        assertEquals(enteros + textos, objeto.keys)
        enteros.forEach { campo ->
            val valor = objeto.getValue(campo).jsonPrimitive
            assertFalse("$campo debe ser numerico", valor.isString)
            assertNotNull("$campo debe ser Int", valor.intOrNull)
        }
        textos.forEach { campo ->
            assertTrue("$campo debe ser String", objeto.getValue(campo).jsonPrimitive.isString)
        }
    }

    private inline fun <reified T> assertCamposObligatorios(json: String) {
        val objeto = Json.parseToJsonElement(json).jsonObject
        objeto.keys.forEach { campo ->
            assertThrows("Debe rechazar la ausencia de $campo", SerializationException::class.java) {
                Json.decodeFromJsonElement<T>(JsonObject(objeto - campo))
            }
        }
    }

    private inline fun <reified T> assertTiposIncorrectos(json: String) {
        val objeto = Json.parseToJsonElement(json).jsonObject
        objeto.forEach { (campo, valor) ->
            val incorrecto = if (valor.jsonPrimitive.isString) {
                JsonPrimitive(42)
            } else {
                JsonPrimitive("no-es-entero")
            }
            assertThrows("Debe rechazar el tipo de $campo", SerializationException::class.java) {
                Json.decodeFromJsonElement<T>(JsonObject(objeto + (campo to incorrecto)))
            }
        }
    }

    private inline fun <reified T> assertCamposNoNulos(json: String) {
        val objeto = Json.parseToJsonElement(json).jsonObject
        objeto.keys.forEach { campo ->
            assertThrows("Debe rechazar null en $campo", SerializationException::class.java) {
                Json.decodeFromJsonElement<T>(JsonObject(objeto + (campo to JsonNull)))
            }
        }
    }
}
