package com.example.myapplication1.viewmodels

sealed interface NewUserUiState {
    object Idle : NewUserUiState             // Esperando a que el usuario pulse el botón
    object Loading : NewUserUiState          // Registrando... (mostrar ProgressBar)
    object Created : NewUserUiState          // ¡Éxito! Cuenta y datos guardados
    data class Error(val message: String) : NewUserUiState // Fallo (mostrar Toast/Snackbar)
}