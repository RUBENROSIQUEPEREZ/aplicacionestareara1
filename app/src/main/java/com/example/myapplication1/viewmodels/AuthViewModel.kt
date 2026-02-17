import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication1.repository.AuthRepository
import com.example.myapplication1.ui.UserUiState // Asegúrate de importar tu sealed interface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel( val repository: AuthRepository) : ViewModel() {

    // 1. GESTIÓN DE DATOS DEL FORMULARIO (StateFlow)
    // Usamos StateFlow en lugar de LiveData.
    // _variable es privada y mutable (para escribir).
    // variable es pública e inmutable (para leer desde la Vista).

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password



    // 2. ESTADO DEL BOTÓN
    private val _isLoginButtonEnabled = MutableStateFlow(false)
    val isLoginButtonEnabled: StateFlow<Boolean> = _isLoginButtonEnabled

    // 3. ESTADO GENERAL DE LA UI (Idle, Loading, Authenticated, Error)
    // Reemplaza al simple booleano _loginResult
    private val _userUiState = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val userUiState: StateFlow<UserUiState> = _userUiState

    // Funciones para actualizar los campos desde la UI
    fun onUsernameChanged(newUsername: String) {
        _username.value = newUsername
        validateFields()
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
        validateFields()
    }

    // Lógica de validación (similar a la tuya, adaptada a StateFlow)
    private fun validateFields() {
        val currentUsername = _username.value
        val currentPassword = _password.value

        // La regla: usuario no vacío y contraseña >= 6 caracteres (requisito Firebase)
        val isValid = currentUsername.isNotEmpty() && currentPassword.length >= 6
        _isLoginButtonEnabled.value = isValid
    }

    // 4. LÓGICA DE LOGIN CON FIREBASE
    // Ya no es un simple check local, ahora llamamos al repositorio
    fun performLogin() {
        val email = _username.value
        val pass = _password.value

        // Lanzamos una corrutina porque la operación de red bloquearía el hilo principal
        viewModelScope.launch {
            // Emitimos estado de CARGA para que la UI muestre el ProgressBar
            _userUiState.value = UserUiState.Loading

            // Llamamos al repositorio (función suspendida)
            val result = repository.signIn(email, pass)

            // Gestionamos el resultado (Result<FirebaseUser>)
            result.onSuccess { user ->
                // Si sale bien, emitimos estado Autenticado con el usuario
                _userUiState.value = UserUiState.Authenticated(user)
            }.onFailure { error ->
                // Si falla, emitimos estado de Error con el mensaje
                _userUiState.value = UserUiState.Error(error.message ?: "Error desconocido")
            }
        }
    }

    fun signIn(email: String, pass: String) {

        // Validación básica antes de llamar a Firebase
        if (email.isBlank() || pass.isBlank()) {
            // Opcional: emitir error si están vacíos
            return
        }

        viewModelScope.launch {
            // Emitimos estado de CARGA
            _userUiState.value = UserUiState.Loading

            // Llamamos al repositorio (función suspendida que conecta con Firebase)
            val result = repository.signIn(email, pass)

            // Gestionamos el resultado (Result<FirebaseUser>)
            result.onSuccess { user ->
                // Éxito: Emitimos estado Autenticado
                _userUiState.value = UserUiState.Authenticated(user)
            }.onFailure { error ->
                // Fallo: Emitimos estado de Error con el mensaje
                _userUiState.value = UserUiState.Error(error.message ?: "Error desconocido")
            }
        }
    }

    // Función opcional para limpiar el estado al volver a la pantalla
    fun resetState() {
        _userUiState.value = UserUiState.Idle
    }
}

