package com.example.myapplication1.repository

import com.example.myapplication1.firebase.AuthDataSource
import com.google.firebase.auth.FirebaseUser

class AuthRepository(private val dataSource: AuthDataSource) {

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> =
        dataSource.signInWithEmail(email, password)

    suspend fun signUp(email: String, password: String): Result<FirebaseUser> =
        dataSource.signUpWithEmail(email, password)

    fun signOut() = dataSource.signOut()

    fun getCurrentUser(): FirebaseUser? = dataSource.currentUser()
}