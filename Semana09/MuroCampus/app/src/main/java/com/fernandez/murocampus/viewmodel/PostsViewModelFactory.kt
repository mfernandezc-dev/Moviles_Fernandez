package com.fernandez.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fernandez.murocampus.data.PostRepository

class PostsViewModelFactory(
    private val repo: PostRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostsViewModel(repo) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}
