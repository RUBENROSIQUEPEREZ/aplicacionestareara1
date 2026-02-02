package com.example.myapplication1.ui

import com.google.firebase.auth.FirebaseUser

sealed interface UserUiState {
    object Idle : UserUiState             // Estado inactivo / esperando acción
    object Loading : UserUiState          // Cargando (mostrando spinner)
    data class Authenticated(val user: FirebaseUser) : UserUiState // Éxito
    data class Error(val message: String) : UserUiState            // Fallo
}