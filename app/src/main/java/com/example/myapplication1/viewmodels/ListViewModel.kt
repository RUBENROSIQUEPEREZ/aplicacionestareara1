package com.example.myapplication1.viewmodels

import android.R
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication1.recycler.Book



class ListViewModel : ViewModel() {

    // 1. La "Caja" que contiene la lista de libros
    // Usamos MutableLiveData para poder cambiarla si queremos
    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books

    // 2. Al arrancar el ViewModel, cargamos los datos iniciales
    init {
        loadData()
    }

    fun loadData() {
        // Aquí movemos la lista que tenías antes en los fragments
        val initialList = listOf(
            Book(1,"Josema Beast","Posicion 3 Mr Oylmpia Classic Physique",imageResId = com.example.myapplication1.R.drawable.josemabeast),
            Book(2, "Joan Pradells", "2 posicion Praga Open ",imageResId = com.example.myapplication1.R.drawable.joanpradells),
            Book(3, "Angel Calderon", "2 Posicion Mr Oympia 212", imageResId = com.example.myapplication1.R.drawable.angelcalderon),
            Book(4, "Mauro Fialho", "Clasificado para el Mr Olympia", imageResId = com.example.myapplication1.R.drawable.maurofialho),
            Book(5, "Chris Bumstedd", "6 veces campeon del Mr Olympia.", imageResId = com.example.myapplication1.R.drawable.cbum)
        )
        _books.value = initialList
    }

    // 3. Función para marcar/desmarcar favorito
    // Esta función la llamará el Fragment cuando toques la estrella
    fun toggleFavorite(book: Book) {
        // Truco: Para que LiveData avise del cambio, necesitamos actualizar la lista
        // Creamos una copia de la lista actual para modificarla
        val currentList = _books.value?.toMutableList() ?: return

        // Buscamos el libro por su ID y le cambiamos el estado
        val index = currentList.indexOfFirst { it.id == book.id }
        if (index != -1) {
            // Actualizamos el objeto en la lista
            // (Nota: Como Book es data class, es mejor copiarlo cambiando el valor)
            val updatedBook = currentList[index].copy(isFavorite = book.isFavorite)
            currentList[index] = updatedBook

            // ¡IMPORTANTE! Al asignar el valor de nuevo, los Observers (Fragments) se enteran
            _books.value = currentList
        }
    }
}