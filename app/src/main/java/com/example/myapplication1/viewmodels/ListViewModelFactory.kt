package com.example.myapplication1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication1.repository.AuthRepository
import com.example.myapplication1.repository.BookRepository

class ListViewModelFactory(
    private val bookRepository: BookRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            return ListViewModel(bookRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}