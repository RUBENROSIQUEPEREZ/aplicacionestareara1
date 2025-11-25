package com.example.myapplication1.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    // LiveData que controla si el botón de login está habilitado
    private val _isLoginButtonEnabled = MutableLiveData<Boolean>()
    val isLoginButtonEnabled: LiveData<Boolean> = _isLoginButtonEnabled

    // LiveData que devuelve si el login es correcto/incorrecto
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult


    //variables para almacenar datos
    private var usernameData: String = ""
    private var passwordData: String = ""


    //SETTERS, que debe de llamer el fragment

    fun setUsername(newUsername: String) {
        usernameData = newUsername // 1. Actualiza el dato privado
        checkCredentialsValidity() // 2. Llama a la validación manualmente
    }

    // Lo mismo para la contraseña.
    fun setPassword(newPassword: String) {
        passwordData = newPassword // 1. Actualiza el dato privado
        checkCredentialsValidity() // 2. Llama a la validación manualmente
    }

    //Validacion de credenciales
    fun checkCredentialsValidity() {
        // username debe tener al menos 1 caracter
        // password debe tener al menos 4
        val isValid = usernameData.length >= 1 && passwordData.length >= 4
        _isLoginButtonEnabled.value = isValid
    }

    fun performLogin() {
        // login success si coincide con los valores establecidos
        val success = usernameData == "admin" && passwordData == "1234"
        _loginResult.value = success
    }
}