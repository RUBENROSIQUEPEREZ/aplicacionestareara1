package com.example.myapplication1.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewUserViewModel : ViewModel() {

    // 1. LiveData para habilitar/deshabilitar el botón de registro
    private val _isRegisterButtonEnabled = MutableLiveData<Boolean>()
    val isRegisterButtonEnabled: LiveData<Boolean> = _isRegisterButtonEnabled

    // 2. LiveData para mostrar error si las contraseñas no coinciden
    private val _passwordMatchError = MutableLiveData<Boolean>()
    val passwordMatchError: LiveData<Boolean> = _passwordMatchError

    // 3. LiveData para notificar que el registro fue exitoso (navegar)
    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    // Variables para almacenar los datos del formulario
    // Usamos 'setters' personalizados para validar cada vez que el dato cambia
    var username: String = ""
        set(value) {
            field = value
            validateForm()
        }

    var password: String = ""
        set(value) {
            field = value
            validateForm()
        }

    var confirmPassword: String = ""
        set(value) {
            field = value
            validateForm()
        }

    // Lógica de validación privada
    private fun validateForm() {
        // Regla 1: Usuario >= 1 carácter y Contraseña >= 4 caracteres
        val isMinLengthValid = username.length >= 1 && password.length >= 4

        // Regla 2: Las contraseñas deben coincidir (solo validamos si ambas tienen texto)
        val passwordsCoincide = password == confirmPassword

        // Actualizar estado de Error:
        // Mostramos error SOLO si ambos campos tienen texto y NO coinciden.
        if (password.isNotEmpty() && confirmPassword.isNotEmpty() && !passwordsCoincide) {
            _passwordMatchError.value = true
        } else {
            _passwordMatchError.value = false
        }

        // Actualizar estado del Botón:
        // Se activa si cumple longitud mínima Y las contraseñas coinciden
        _isRegisterButtonEnabled.value = isMinLengthValid && passwordsCoincide
    }

    // Función pública para ejecutar el registro (llamada desde el botón)
    fun createAccount() {
        // Verificamos una última vez (programación defensiva)
        if (_isRegisterButtonEnabled.value == true) {
            // Simulamos éxito y notificamos al fragmento
            _registrationSuccess.value = true
        } else {
            _registrationSuccess.value = false
        }
    }
}