package com.example.myapplication1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication1.repository.AuthRepository
import com.example.myapplication1.repository.UserRepository
import com.example.myapplication1.viewmodels.NewUserUiState // Asegúrate de tener esta clase creada
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewUserViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // 1. ESTADO DE LA UI (StateFlow en lugar de LiveData)
    // Gestiona: Idle, Loading, Created (éxito) y Error.
    private val _uiState = MutableStateFlow<NewUserUiState>(NewUserUiState.Idle)
    val uiState: StateFlow<NewUserUiState> = _uiState

    // 2. ESTADO DEL BOTÓN Y VALIDACIONES VISUALES
    private val _isRegisterButtonEnabled = MutableStateFlow(false)
    val isRegisterButtonEnabled: StateFlow<Boolean> = _isRegisterButtonEnabled

    private val _passwordMatchError = MutableStateFlow(false)
    val passwordMatchError: StateFlow<Boolean> = _passwordMatchError

    // Variables para los inputs (usamos variables simples o StateFlows para el binding bidireccional)
    var username = ""
        set(value) { field = value; validateForm() }
    var password = ""
        set(value) { field = value; validateForm() }
    var confirmPassword = ""
        set(value) { field = value; validateForm() }
    var birthDate = ""
        set(value) { field = value; validateForm() }

    // Lógica de validación
    private fun validateForm() {
        // Tarea 4: La contraseña debe tener al menos 6 caracteres [1]
        val isMinLengthValid = username.isNotEmpty() && password.length >= 6
        val passwordsCoincide = password == confirmPassword
        val isDateSelected = birthDate.isNotEmpty()

        // Mostrar error solo si hay texto en ambos y no coinciden
        _passwordMatchError.value = password.isNotEmpty() && confirmPassword.isNotEmpty() && !passwordsCoincide

        // Activar botón solo si todo es válido
        _isRegisterButtonEnabled.value = isMinLengthValid && passwordsCoincide && isDateSelected
    }

    // 3. LÓGICA DE REGISTRO CON FIREBASE (Corrutinas)
    fun createAccount() {
        if (password.length < 6) {
            _uiState.value = NewUserUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        viewModelScope.launch {
            _uiState.value = NewUserUiState.Loading

            // 1. Intentamos crear el usuario en Authentication
            val result = authRepository.signUp(username, password)

            result.onSuccess { authUser ->
                // EL USUARIO SE CREÓ EN AUTH. AHORA GUARDAMOS EN FIRESTORE.
                try {
                    // Usamos el UID que nos devuelve Firebase Auth
                    userRepository.saveUser(
                        id = authUser.uid,
                        email = username,
                        birthDate = birthDate
                    )

                    // Si todo va bien, emitimos éxito
                    _uiState.value = NewUserUiState.Created

                } catch (e: Exception) {
                    // Si falla la base de datos (aunque el usuario se creó en Auth)
                    _uiState.value = NewUserUiState.Error("Usuario creado, pero error al guardar datos: ${e.message}")
                }
            }.onFailure { error ->
                _uiState.value = NewUserUiState.Error(error.message ?: "Error al registrar")
            }
        }
    }
}