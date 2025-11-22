package com.example.myapplication1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged // Import para listeners de texto modernos
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // ¡DELEGADO CLAVE!
import androidx.navigation.fragment.findNavController
import com.example.myapplication1.R
import com.example.myapplication1.databinding.FragmentLoginBinding
import com.example.myapplication1.viewmodels.AuthViewModel // Tu ViewModel
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // 1. INSTANCIACIÓN DEL VIEWMODEL (by viewModels())
    // Esta instancia sobrevive a los cambios de configuración.
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuración de Listeners y Observadores
        setupInputListeners()
        setupButtonListeners()
        setupViewModelObservers()

        // Inicializar el estado del botón al cargar el Fragmento
        authViewModel.checkCredentialsValidity()
    }

    /**
     * Conecta los campos de texto del XML con las variables del ViewModel.
     * La tarea requiere usar TextInputLayouts (Layout de Material Design),
     * por lo que accedemos al EditText interno (editText).
     */
    private fun setupInputListeners() {
        // Listener para el nombre de usuario
        binding.tilUsername.editText?.doOnTextChanged { text, _, _, _ ->
            // Actualiza la propiedad 'username' del ViewModel
            authViewModel.username = text.toString()
            // No es necesario llamar aquí a checkCredentialsValidity(), ya se llama en el setter del ViewModel.
        }

        // Listener para la contraseña
        binding.tilPassword.editText?.doOnTextChanged { text, _, _, _ ->
            // Actualiza la propiedad 'password' del ViewModel
            authViewModel.password = text.toString()
        }
    }

    /**
     * Configura los listeners de clic de los botones.
     */
    private fun setupButtonListeners() {

        // Botón INICIAR SESIÓN
        binding.btnLogin.setOnClickListener {
            // Llama a la lógica de autenticación del ViewModel
            authViewModel.performLogin()
        }

        // Botón CREAR CUENTA (Navegación)
        binding.tvCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Botón INICIAR SESIÓN EN GOOGLE (Requisito de SnackBar)
        binding.btnGoogleLogin.setOnClickListener {
            // Muestra el SnackBar requerido por la tarea
            Snackbar.make(
                binding.root,
                getString(R.string.snackbar_not_implemented), // Asegúrate de definirlo en strings.xml
                Snackbar.LENGTH_LONG
            ).setAction(getString(R.string.snackbar_action_close)) { /* Acción vacía */ }.show()
        }
    }

    /**
     * Suscribe el Fragmento a los cambios de LiveData del ViewModel.
     */
    private fun setupViewModelObservers() {

        // 2. OBSERVACIÓN DEL ESTADO DEL BOTÓN
        authViewModel.isLoginButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            // Actualiza el estado del botón cuando cambia la validez de los campos.
            binding.btnLogin.isEnabled = isEnabled
        }

        // 3. OBSERVACIÓN DEL RESULTADO DEL LOGIN
        authViewModel.loginResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                // TAREA: Si es exitoso, la tarea requeriría navegar a la pantalla principal.
                // Como no existe esa pantalla todavía, simplemente mostramos un mensaje de éxito.
                Toast.makeText(context, "Login Correcto. ¡Bienvenido, admin!", Toast.LENGTH_SHORT).show()
            } else {
                // Login fallido: Muestra un mensaje de error o marca los campos.
                Toast.makeText(context, "Credenciales incorrectas.", Toast.LENGTH_LONG).show()
                // Opcional: limpiar los campos o marcar error en los TextInputLayouts
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}