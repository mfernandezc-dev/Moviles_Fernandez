package com.fernandez.catalogo

import android.content.Context
import androidx.room.Room
import com.fernandez.catalogo.data.auth.AuthRepository
import com.fernandez.catalogo.data.auth.FirebaseAuthRepository
import com.fernandez.catalogo.data.local.AppDatabase
import com.fernandez.catalogo.data.remote.RetrofitClient
import com.fernandez.catalogo.data.repository.ItemRepository
import com.fernandez.catalogo.data.repository.ItemRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object AppContainer {
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository(FirebaseAuth.getInstance(), firestore)
    }

    @Volatile
    private var repository: ItemRepository? = null

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

        repository = ItemRepositoryImpl(
            api = RetrofitClient.apiService,
            dao = database.itemDao()
        )
    }
}
