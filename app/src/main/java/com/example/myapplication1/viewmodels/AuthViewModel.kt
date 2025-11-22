package com.example.myapplication1.viewmodels // Asegúrate de que este es tu paquete 'viewmodels'

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel que gestiona la lógica de negocio y el estado de la UI del Login.
 * Sobrevive a cambios de configuración como la rotación de pantalla.
 */
class AuthViewModel : ViewModel() {

    // --- Variables de Estado para la UI ---

    // 1. Estado para habilitar/deshabilitar el botón de Login.
    // Usamos MutableLiveData internamente para poder modificar el valor.
    private val _isLoginButtonEnabled = MutableLiveData<Boolean>()

    // LiveData pública para que el Fragmento solo pueda OBSERVAR el valor, no modificarlo.
    val isLoginButtonEnabled: LiveData<Boolean> = _isLoginButtonEnabled

    // 2. Estado para el resultado del intento de Login.
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    // --- Almacenamiento de Datos del Usuario ---

    // Variables simples para almacenar el texto que el usuario introduce.
    // Usaremos un setter personalizado para llamar a la validación cada vez que cambien.
    var username: String = ""
        set(value) {
            field = value
            checkCredentialsValidity() // Llama a la validación cada vez que cambia el nombre de usuario
        }

    var password: String = ""
        set(value) {
            field = value
            checkCredentialsValidity() // Llama a la validación cada vez que cambia la contraseña
        }

    // --- Lógica de Negocio ---

    /**
     * Comprueba si los campos cumplen los requisitos MÍNIMOS para habilitar el botón.
     * Requisito de la Tarea: Usuario >= 1 carácter, Contraseña >= 4 caracteres.
     */


//    Cambiado de private a public
     public fun checkCredentialsValidity() {
        val isValid = username.length >= 1 && password.length >= 4

        // Actualiza el LiveData. Esto notificará automáticamente al Fragmento.
        _isLoginButtonEnabled.value = isValid
    }

    /**
     * Procesa el intento de login con las credenciales almacenadas.
     * Requisito de la Tarea: user="admin" y pass="1234".
     */
    fun performLogin() {
        // Lógica de autenticación "fake" según el requisito de la tarea
        val success = username == "admin" && password == "1234"

        // El Fragmento observará este resultado para navegar o mostrar el error.
        _loginResult.value = success
    }
}