package com.example.myapplication1.recycler

data class Book(
    val id: Int,                // identificador unico para saber cual es cual en la base de datos
    val title: String,          // el nombre del culturista o titulo del elemento
    val description: String,    // la descripcion o info extra que sale debajo
    val imageResId: Int,        // aqui guardamos el numero de la foto en drawable no el archivo entero
    var isFavorite: Boolean = false // variable para saber si le hemos dado al corazon se pone var porque cambia
)