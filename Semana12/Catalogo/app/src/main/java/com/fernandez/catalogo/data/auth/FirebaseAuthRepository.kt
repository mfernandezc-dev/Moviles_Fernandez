package com.fernandez.catalogo.data.auth

import com.fernandez.catalogo.domain.model.Sesion
import com.fernandez.catalogo.domain.model.Rol
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    override val sesiones: Flow<Sesion?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.toSesion())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> = try {
        val usuario = auth.signInWithEmailAndPassword(correo, clave).await().user
        if (usuario != null) {
            Result.success(usuario.toSesion())
        } else {
            Result.failure(IllegalStateException("No se pudo recuperar la sesión. Inténtalo de nuevo."))
        }
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        val mensaje = when (error) {
            is FirebaseAuthInvalidCredentialsException,
            is FirebaseAuthInvalidUserException ->
                "Correo o contraseña incorrectos, o la cuenta no está disponible."
            is FirebaseNetworkException ->
                "No se pudo conectar. Comprueba tu conexión e inténtalo de nuevo."
            is FirebaseTooManyRequestsException ->
                "Demasiados intentos. Espera unos minutos antes de volver a intentarlo."
            else -> "No se pudo iniciar sesión. Inténtalo de nuevo más tarde."
        }
        Result.failure(IllegalStateException(mensaje))
    }

    override suspend fun sesionActual(): Sesion? = auth.currentUser?.toSesion()

    override suspend fun obtenerRol(uid: String): Result<Rol> {
        if (uid.isBlank() || auth.currentUser?.uid != uid) {
            return Result.failure(IllegalStateException("La sesión cambió. Vuelve a iniciar sesión."))
        }
        return try {
            val perfil = withTimeout(15_000) {
                firestore.collection("catalogo_usuarios")
                    .document(uid)
                    .get(Source.SERVER)
                    .await()
            }
            when {
                auth.currentUser?.uid != uid ->
                    Result.failure(IllegalStateException("La sesión cambió. Vuelve a iniciar sesión."))
                perfil.metadata.isFromCache || perfil.metadata.hasPendingWrites() ->
                    Result.failure(IllegalStateException("No se pudo confirmar el perfil con el servidor."))
                else -> interpretarRol(perfil.exists(), perfil.get("rol"))
            }
        } catch (_: TimeoutCancellationException) {
            Result.failure(IllegalStateException("La consulta del perfil tardó demasiado. Comprueba tu conexión y reintenta."))
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            val mensaje = when ((error as? FirebaseFirestoreException)?.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                    "No tienes permiso para consultar tu perfil de acceso."
                FirebaseFirestoreException.Code.UNAUTHENTICATED ->
                    "No se pudo validar la sesión. Cierra sesión y vuelve a ingresar."
                FirebaseFirestoreException.Code.UNAVAILABLE,
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED ->
                    "No se pudo verificar el perfil. Comprueba tu conexión y reintenta."
                else -> "No se pudo consultar el perfil de acceso. Inténtalo de nuevo."
            }
            Result.failure(IllegalStateException(mensaje))
        }
    }

    override fun cerrarSesion() {
        auth.signOut()
    }

    private fun FirebaseUser.toSesion(): Sesion = Sesion(
        uid = uid,
        correo = email.orEmpty()
    )
}

internal fun interpretarRol(existe: Boolean, valor: Any?): Result<Rol> = when {
    !existe -> Result.failure(IllegalStateException("No existe un perfil de acceso para esta cuenta."))
    valor == "USER" -> Result.success(Rol.USER)
    valor == "ADMIN" -> Result.success(Rol.ADMIN)
    else -> Result.failure(IllegalStateException("El perfil contiene un rol inválido."))
}
