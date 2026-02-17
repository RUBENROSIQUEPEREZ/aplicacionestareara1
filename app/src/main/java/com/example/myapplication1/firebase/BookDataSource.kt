package com.example.myapplication1.firebase


import com.google.firebase.firestore.FirebaseFirestore
import com.example.myapplication1.recycler.Book // Tu modelo de datos
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class BookDataSource(private val db: FirebaseFirestore) {

    private val auth = FirebaseAuth.getInstance()
    // Guardar un libro en Firestore

    suspend fun saveBook(book: Book) {
        // 1. Obtener el usuario actual (Si es null, lanzamos error controlado)
        val user = auth.currentUser ?: throw Exception("Usuario no logueado")

        // 2. Crear el mapa de datos (Tal como sugiere la Tarea 4)
        val bookMap = hashMapOf(
            "title" to book.title,
            "description" to book.description,
            "isFavorite" to book.isFavorite,
            "imageResId" to book.imageResId
            // No guardamos el ID dentro del mapa porque será el ID del documento
        )

        // 3. Guardar en la subcolección: usuarios/{uid}/libros
        db.collection("usuarios")
            .document(user.uid)
            .collection("libros")
            .add(bookMap) // .add genera un ID automático
            .await()
    }

    // Obtener todos los libros de un usuario en tiempo real o puntual
    suspend fun getBooks(userId: String): List<Book> {
        val snapshot = db.collection("usuarios")
            .document(userId)
            .collection("libros")
            .get()
            .await()

        // Convertir documentos a objetos Book
        return snapshot.toObjects(Book::class.java)
    }

    // Añadir un nuevo libro
    fun addBook(userId: String, book: Book) {
        db.collection("usuarios")
            .document(userId)
            .collection("libros")
            .add(book)
    }
    fun updateBook(userId: String, book: Book) {
        // 1. Accedemos a la colección de libros del usuario
        val booksRef = db.collection("usuarios").document(userId).collection("libros")

        // 2. Buscamos el documento que coincida con el 'id' numérico de tu libro
        //    (Como usaste IDs aleatorios numéricos, no sabemos el ID del documento de Firestore,
        //    así que lo buscamos por el campo "id" que guardaste dentro).
        booksRef.whereEqualTo("id", book.id)
            .get()
            .addOnSuccessListener { documents ->
                // 3. Si lo encontramos, actualizamos el campo 'isFavorite'
                for (document in documents) {
                    // Usamos document.reference.update para modificar solo ese campo
                    document.reference.update("isFavorite", book.isFavorite)
                        .addOnFailureListener { e ->
                            // Aquí podrías loguear el error si falla
                            println("Error al actualizar: ${e.message}")
                        }
                }
            }
    }
}