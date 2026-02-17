package com.example.myapplication1.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await // Necesario para usar await() con corrutinas

class UserDataSource(private val firestore: FirebaseFirestore) {

    // Función para guardar el usuario en la colección "usuarios"
    suspend fun saveUser(id: String, email: String, fechaNacimiento: String) {
        // Creamos un mapa con los datos (o usamos una data class)
        val userMap = hashMapOf(
            "email" to email,
            "fechaNacimiento" to fechaNacimiento,
            // "libros" no hace falta crearlo aquí, se puede crear luego como subcolección
        )

        // Guardamos en la colección "usuarios", documento con el ID del usuario
        firestore.collection("usuarios").document(id).set(userMap).await()
    }
}