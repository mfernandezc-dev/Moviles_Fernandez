package com.fernandez.catalogo.data.auth

import com.fernandez.catalogo.domain.model.Sesion
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(private val auth: FirebaseAuth) : AuthRepository {
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

    override fun cerrarSesion() {
        auth.signOut()
    }

    private fun FirebaseUser.toSesion(): Sesion = Sesion(
        uid = uid,
        correo = email.orEmpty()
    )
}
