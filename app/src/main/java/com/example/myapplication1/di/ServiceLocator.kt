package com.example.myapplication1.di

import com.example.myapplication1.firebase.AuthDataSource
import com.example.myapplication1.firebase.UserDataSource // Asegúrate de tener creada esta clase
import com.example.myapplication1.firebase.FirebaseProvider
import com.example.myapplication1.repository.AuthRepository
import com.example.myapplication1.repository.UserRepository // Asegúrate de tener creada esta clase
import com.example.myapplication1.firebase.BookDataSource
import com.example.myapplication1.repository.BookRepository


object ServiceLocator {

    // --- AUTENTICACIÓN (Firebase Auth) ---
    // Inicialización "perezosa" del DataSource de Autenticación
    val authDataSource by lazy {
        AuthDataSource(FirebaseProvider.provideAuth())
    }

    // Inicialización del Repositorio inyectándole el DataSource de Auth
    val authRepository by lazy {
        AuthRepository(authDataSource)
    }

    // --- PERSISTENCIA (Cloud Firestore) ---
    // Tarea 4: Inicialización del DataSource de Usuarios inyectando Firestore
    val userDataSource by lazy {
        UserDataSource(FirebaseProvider.provideFirestore())
    }

    // Tarea 4: Inicialización del Repositorio de Usuarios
    val userRepository by lazy {
        UserRepository(userDataSource)
    }

    val bookDataSource by lazy {
        BookDataSource(FirebaseProvider.provideFirestore())
    }

    val bookRepository by lazy {
        BookRepository(bookDataSource)
    }
}