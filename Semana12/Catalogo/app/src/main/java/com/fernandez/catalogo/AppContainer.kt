package com.fernandez.catalogo

import android.content.Context
import androidx.room.Room
import com.fernandez.catalogo.data.auth.AuthRepository
import com.fernandez.catalogo.data.auth.FirebaseAuthRepository
import com.fernandez.catalogo.data.local.AppDatabase
import com.fernandez.catalogo.data.remote.RetrofitClient
import com.fernandez.catalogo.data.repository.ItemRepository
import com.fernandez.catalogo.data.repository.ItemRepositoryImpl
import com.fernandez.catalogo.notification.Notificador
import com.fernandez.catalogo.notification.NotificadorAndroid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object AppContainer {
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository(FirebaseAuth.getInstance(), firestore)
    }

    @Volatile
    private var repository: ItemRepository? = null

    @Volatile
    private var notificadorLocal: Notificador? = null

    val notificador: Notificador
        get() = checkNotNull(notificadorLocal) { "AppContainer debe inicializarse antes de usarse." }

    val itemRepository: ItemRepository
        get() = checkNotNull(repository) { "AppContainer debe inicializarse antes de usarse." }

    @Synchronized
    fun inicializar(context: Context) {
        if (repository != null) return

        val database = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "catalogo.db"
        ).build()

        notificadorLocal = NotificadorAndroid(context.applicationContext)

        repository = ItemRepositoryImpl(
            api = RetrofitClient.apiService,
            dao = database.itemDao()
        )
    }
}
