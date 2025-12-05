package com.example.myapplication1.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class NewUserViewModel : ViewModel() {

    // variables livedata para que la pantalla sepa cuando cambiar cosas
    // el guion bajo es privado para nosotros y la otra publica para que la pantalla solo lea

    // 1. controla si el boton de registrar se puede pulsar o no
    private val _isRegisterButtonEnabled = MutableLiveData<Boolean>()
    val isRegisterButtonEnabled: LiveData<Boolean> = _isRegisterButtonEnabled

    // 2. controla si hay que enseñar el mensaje rojo de error de contraseña
    private val _passwordMatchError = MutableLiveData<Boolean>()
    val passwordMatchError: LiveData<Boolean> = _passwordMatchError

    // 3. avisa cuando el registro se ha completado para cambiar de pantalla
    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    // variables para guardar lo que escribe el usuario
    // usamos set para que cada vez que cambie el valor se compruebe todo automaticamente

    // campo de usuario
    var username: String = ""
        set(value) {
            field = value
            validateForm() // validamos cada vez que se escribe una letra
        }

    // campo de contraseña
    var password: String = ""
        set(value) {
            field = value
            validateForm()
        }

    // campo para confirmar contraseña
    var confirmPassword: String = ""
        set(value) {
            field = value
            validateForm()
        }

    // campo de la fecha de nacimiento
    var birthDate: String = ""
        set(value) {
            field = value
            validateForm()
        }

    // logica para comprobar si el formulario esta bien relleno
    private fun validateForm() {

        // regla 1 el usuario tiene que tener texto y la contraseña al menos 4 letras
        val isMinLengthValid = username.length >= 1 && password.length >= 4

        // regla 2 las dos contraseñas tienen que ser identicas
        val passwordsCoincide = password == confirmPassword

        // regla 3 tiene que haber seleccionado una fecha
        val isDateSelected = birthDate.isNotEmpty()

        // logica del error
        // solo mostramos el error si ha escrito en los dos campos y son distintos
        if (password.isNotEmpty() && confirmPassword.isNotEmpty() && !passwordsCoincide) {
            _passwordMatchError.value = true
        } else {
            _passwordMatchError.value = false
        }

        // logica del boton
        // solo activamos el boton si la longitud esta bien y las contraseñas coinciden
        // aqui podrias añadir tambien isDateSelected si fuera obligatorio
        _isRegisterButtonEnabled.value = isMinLengthValid && passwordsCoincide
    }

    // funcion publica que se llama al pulsar el boton
    fun createAccount() {
        // comprobacion de seguridad final
        if (_isRegisterButtonEnabled.value == true) {
            // avisamos a la pantalla de que todo ha ido bien para que navegue
            _registrationSuccess.value = true
        } else {
            _registrationSuccess.value = false
        }
    }
}