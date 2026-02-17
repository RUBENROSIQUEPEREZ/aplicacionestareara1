package com.example.myapplication1.firebase

import com.google.firebase.firestore.FirebaseFirestore

class UserDataSource(private val db: FirebaseFirestore) {

    fun saveUser(id: String, email: String, birthDate: String) {
        // 1. Preparamos los datos a guardar (mapa clave-valor)
        val userData = hashMapOf(
            "correo" to email,
            "fechaNacimiento" to birthDate
            // La colección de libros se creará después, al añadir el primer libro
        )

        // 2. Guardamos en la colección "usuarios"
        // Usamos el 'id' (UID de autenticación) como nombre del documento
        db.collection("usuarios").document(id).set(userData)
    }
}