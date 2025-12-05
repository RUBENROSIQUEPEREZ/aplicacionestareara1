package com.example.myapplication1.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    // 1. Ahora los datos son LiveData (Cajas observables)
    // Esto coincide con la imagen: val username = MutableLiveData<String>()
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    // Estado del botón (igual que antes)
    private val _isLoginButtonEnabled = MutableLiveData<Boolean>()
    val isLoginButtonEnabled: LiveData<Boolean> = _isLoginButtonEnabled

    // Resultado del login (igual que antes)
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    init {
        // Estado inicial
        _isLoginButtonEnabled.value = false
    }

    // 2. Funciones que llama la Vista al escribir
    // En la imagen se llamaban 'onUsernameChanged'
    fun onUsernameChanged(newUsername: String) {
        username.value = newUsername // Guardamos dentro de la caja
        validateFields()
    }

    fun onPasswordChanged(newPassword: String) {
        password.value = newPassword // Guardamos dentro de la caja
        validateFields()
    }

    // 3. Validación leyendo de las cajas
    private fun validateFields() {
        // CUIDADO: .value puede ser nulo, por eso usamos el operador elvis ?: ""
        val currentUsername = username.value ?: ""
        val currentPassword = password.value ?: ""

        val isValid = currentUsername.length >= 1 && currentPassword.length >= 4
        _isLoginButtonEnabled.value = isValid
    }

    fun performLogin() {
        val currentUsername = username.value ?: ""
        val currentPassword = password.value ?: ""

        val success = currentUsername == "admin" && currentPassword == "1234"
        _loginResult.value = success
    }
}