package com.example.myapplication1.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication1.R // importamos r para poder acceder a las imagenes facil
import com.example.myapplication1.recycler.Book

class ListViewModel : ViewModel() {

    // esta es la caja que guarda la lista de culturistas
    // es mutable porque nosotros la vamos a modificar desde aqui dentro
    private val _books = MutableLiveData<List<Book>>()

    // esta es la version que ven los fragments
    // es publica pero no se puede modificar desde fuera por seguridad
    val books: LiveData<List<Book>> = _books

    // este bloque se ejecuta solo una vez al crear el viewmodel
    init {
        loadData()
    }

    // funcion para cargar los datos iniciales
    fun loadData() {
        // creamos la lista con tus datos personalizados
        // asegurate de que las fotos esten en la carpeta drawable
        val initialList = listOf(
            Book(1, "Josema Beast", "Posicion 3 Mr Oylmpia Classic Physique", R.drawable.josemabeast),
            Book(2, "Joan Pradells", "2 posicion Praga Open ", R.drawable.joanpradells),
            Book(3, "Angel Calderon", "2 Posicion Mr Oympia 212", R.drawable.angelcalderon),
            Book(4, "Mauro Fialho", "Clasificado para el Mr Olympia", R.drawable.maurofialho),
            Book(5, "Chris Bumstedd", "6 veces campeon del Mr Olympia.", R.drawable.cbum)
        )
        // metemos la lista en la caja livedata para que la pantalla se actualice
        _books.value = initialList
    }

    // funcion para cambiar el corazon de favorito
    fun toggleFavorite(book: Book) {
        // hacemos una copia de la lista actual para poder tocarla
        val currentList = _books.value?.toMutableList() ?: return

        // buscamos en que posicion esta el culturista que hemos tocado
        val index = currentList.indexOfFirst { it.id == book.id }

        if (index != -1) {
            // creamos una copia del culturista con el valor de favorito cambiado
            // esto es necesario porque en kotlin las clases de datos son fijas
            val updatedBook = currentList[index].copy(isFavorite = book.isFavorite)

            // sustituimos el viejo por el nuevo en la lista
            currentList[index] = updatedBook

            // guardamos la lista nueva en la caja livedata
            // esto hace que las dos pantallas (lista y favoritos) se actualicen solas
            _books.value = currentList
        }
    }
}