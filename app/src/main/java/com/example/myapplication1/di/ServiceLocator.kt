package com.example.myapplication1.di

import com.example.myapplication1.firebase.AuthDataSource
import com.example.myapplication1.firebase.FirebaseProvider
import com.example.myapplication1.repository.AuthRepository

object ServiceLocator {
    // Inicialización "perezosa" del DataSource
    val authDataSource by lazy {
        AuthDataSource(FirebaseProvider.provideAuth())
    }

    // Inicialización del Repositorio inyectándole el DataSource
    val authRepository by lazy {
        AuthRepository(authDataSource)
    }
}