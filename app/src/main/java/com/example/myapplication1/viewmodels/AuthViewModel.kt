package com.example.myapplication1.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication1.repository.AuthRepository

// heredamos de viewmodel para que los datos no se borren si giramos el movil
class AuthViewModel(repository: AuthRepository) : ViewModel() {

    // usamos mutablelivedata que son como cajas que la vista puede observar
    // si el dato cambia aqui dentro la pantalla se entera automaticamente
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    // estado del boton
    // usamos el guion bajo para la variable privada que solo modificamos nosotros
    private val _isLoginButtonEnabled = MutableLiveData<Boolean>()
    // la variable publica es livedata normal para que desde fuera solo puedan leerla no cambiarla
    val isLoginButtonEnabled: LiveData<Boolean> = _isLoginButtonEnabled

    // resultado del login
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    init {
        // al arrancar el boton tiene que estar desactivado
        _isLoginButtonEnabled.value = false
    }

    // funciones que se ejecutan cada vez que escribimos una letra
    fun onUsernameChanged(newUsername: String) {
        username.value = newUsername // guardamos el texto en la caja
        validateFields() // comprobamos si ya podemos activar el boton
    }

    fun onPasswordChanged(newPassword: String) {
        password.value = newPassword
        validateFields()
    }

    // logica para saber si los datos son validos
    private fun validateFields() {
        // usamos el simbolo ?: que significa que si el valor es nulo use una cadena vacia
        // asi evitamos que la app se cierre por errores de nulos
        val currentUsername = username.value ?: ""
        val currentPassword = password.value ?: ""

        // la regla es que el usuario tenga algo y la contraseña mas de 4 letras
        val isValid = currentUsername.length >= 1 && currentPassword.length >= 4

        // actualizamos el estado del boton
        _isLoginButtonEnabled.value = isValid
    }

    // funcion que simula el login real
    fun performLogin() {
        val currentUsername = username.value ?: ""
        val currentPassword = password.value ?: ""

        // comprobamos si es el usuario admin con la contraseña 1234
        val success = currentUsername == "admin" && currentPassword == "1234"

        // avisamos del resultado final
        _loginResult.value = success
    }
}