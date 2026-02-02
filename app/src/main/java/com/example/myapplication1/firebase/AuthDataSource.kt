package com.example.myapplication1.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthDataSource(private val auth: FirebaseAuth) {

    // Login
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            if (authResult.user != null) {
                Result.success(authResult.user!!)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Registro
    suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            if (authResult.user != null) {
                Result.success(authResult.user!!)
            } else {
                Result.failure(Exception("Error en el registro"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cerrar sesión
    fun signOut() {
        auth.signOut()
    }

    // Obtener usuario actual (si ya está logueado)
    fun currentUser(): FirebaseUser? {
        return auth.currentUser
    }
}