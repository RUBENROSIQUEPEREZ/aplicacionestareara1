package com.example.myapplication1.recycler

/*
Data class:
Usamos esto cuando una clase solo sirve para guardar datos. Nos regala funciones útiles automáticamente (como toString() para ver los datos en los logs).
*/
data class Book(
    val id: Int,                // Identificador único (muy útil para bases de datos)
    val title: String,          // Título del libro
    val description: String,    // Sinopsis
    val imageResId: Int,        // ID de la imagen en drawable (ej: R.drawable.harry_potter)
    var isFavorite: Boolean = false // Estado: ¿Está marcado como favorito?
)