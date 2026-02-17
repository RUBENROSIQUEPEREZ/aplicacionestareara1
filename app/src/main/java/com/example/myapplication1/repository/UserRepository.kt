package com.example.myapplication1.repository

import com.example.myapplication1.firebase.UserDataSource

class UserRepository(private val userDataSource: UserDataSource) {

    // Simplemente llama al DataSource
    fun saveUser(id: String, email: String, birthDate: String) {
        userDataSource.saveUser(id, email, birthDate)
    }

    
}