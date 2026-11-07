package com.fernandez.catalogo

import android.content.Context
import androidx.room.Room
import com.fernandez.catalogo.data.local.AppDatabase
import com.fernandez.catalogo.data.remote.RetrofitClient
import com.fernandez.catalogo.data.repository.ItemRepository
import com.fernandez.catalogo.data.repository.ItemRepositoryImpl

object AppContainer {
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
