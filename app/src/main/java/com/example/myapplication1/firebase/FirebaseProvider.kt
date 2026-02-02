package com.example.myapplication1.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseProvider {
    @Volatile private var auth: FirebaseAuth? = null
    @Volatile private var firestore: FirebaseFirestore? = null

    // Función para proveer Auth
    fun provideAuth(): FirebaseAuth =
        auth ?: synchronized(this) {
            auth ?: FirebaseAuth.getInstance().also { auth = it }
        }

    // Función para proveer Firestore (se usará más adelante para la BBDD)
    fun provideFirestore(): FirebaseFirestore =
        firestore ?: synchronized(this) {
            firestore ?: FirebaseFirestore.getInstance().also { firestore = it }
        }
}