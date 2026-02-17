package com.example.myapplication1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication1.recycler.Book
import com.example.myapplication1.repository.AuthRepository
import com.example.myapplication1.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.launch

// 1. SOLUCIÓN VIEWMODELSCOPE: Tienes que añadir ": ViewModel()" aquí
class ListViewModel(
    private val bookRepository: BookRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    init {
        loadBooks()
    }

    // Función para leer libros de Firestore (Sustituye a tu loadData estático)
    // 2. SOLUCIÓN LOADBOOKS: Tienes que tener esta función definida dentro de la clase
    fun loadBooks() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            viewModelScope.launch { // Esto lanza la corrutina en el hilo principal
                val resultList = bookRepository.getBooks(currentUser.uid)
                _books.value = resultList
            }
        }
    }

    // Función para añadir (Guardar en Firestore)
    fun addBook(book: Book) {
        viewModelScope.launch {
            try {
                // Llamada al repositorio
                bookRepository.saveBook(book)

                // Opcional: Recargar la lista tras guardar
                loadBooks()

            } catch (e: Exception) {
                // ESTO EVITA QUE LA APP SE CIERRE
                android.util.Log.e("ListViewModel", "Error al guardar: ${e.message}")
            }
        }
    }

    // (Opcional) Función auxiliar para subir tus culturistas la primera vez
    private fun uploadInitialData(userId: String) {
        val initialList = listOf(
            Book(1, "Josema Beast", "Posicion 3 Mr Oylmpia Classic Physique", com.example.myapplication1.R.drawable.josemabeast),
            Book(2, "Joan Pradells", "2 posicion Praga Open ", com.example.myapplication1.R.drawable.joanpradells),
            Book(3, "Angel Calderon", "2 Posicion Mr Oympia 212", com.example.myapplication1.R.drawable.angelcalderon),
            Book(4, "Mauro Fialho", "Clasificado para el Mr Olympia", com.example.myapplication1.R.drawable.maurofialho),
            Book(5, "Chris Bumstedd", "6 veces campeon del Mr Olympia.", com.example.myapplication1.R.drawable.cbum)
        )
        viewModelScope.launch {
            initialList.forEach { book ->
                bookRepository.addBook(userId, book)
            }
            // Después de subirlos, recargamos para mostrarlos
            loadBooks()
        }
    }
    fun toggleFavorite(book: Book) {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            // Ahora viewModelScope funcionará porque heredamos de ViewModel()
            viewModelScope.launch {
                val updatedBook = book.copy(isFavorite = !book.isFavorite)

                // Actualizamos en Firestore
                bookRepository.updateBook(currentUser.uid, updatedBook)

                // Ahora loadBooks() funcionará porque está definida arriba
                loadBooks()
            }
        }
    }


}