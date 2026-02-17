package com.example.myapplication1.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseProvider {

    // 1. Variables Volátiles (Visibles para todos los hilos inmediatamente)
    @Volatile
    private var auth: FirebaseAuth? = null

    @Volatile
    private var firestore: FirebaseFirestore? = null

    // 2. Función para proveer Auth (Singleton)
    fun provideAuth(): FirebaseAuth =
        auth ?: synchronized(this) {
            auth ?: FirebaseAuth.getInstance().also { auth = it }
        }

    // 3. Función para proveer Firestore (Base de datos)
    fun provideFirestore(): FirebaseFirestore =
        firestore ?: synchronized(this) {
            firestore ?: FirebaseFirestore.getInstance().also { firestore = it }
        }
}