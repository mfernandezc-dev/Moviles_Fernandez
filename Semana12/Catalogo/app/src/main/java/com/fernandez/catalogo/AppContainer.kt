package com.fernandez.catalogo

import com.fernandez.catalogo.data.remote.RetrofitClient
import com.fernandez.catalogo.data.repository.ItemRepository
import com.fernandez.catalogo.data.repository.ItemRepositoryImpl

object AppContainer {
    val itemRepository: ItemRepository by lazy {
        ItemRepositoryImpl(api = RetrofitClient.apiService)
    }
}
