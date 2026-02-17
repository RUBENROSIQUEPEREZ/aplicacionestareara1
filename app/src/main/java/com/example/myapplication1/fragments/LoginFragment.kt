package com.example.myapplication1.fragments // Asegúrate de que el paquete es correcto

import AuthViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.myapplication1.R
import com.example.myapplication1.databinding.FragmentLoginBinding
import com.example.myapplication1.di.ServiceLocator
import com.example.myapplication1.ui.UserUiState // Tu sealed interface
import com.example.myapplication1.viewmodels.AuthViewModelFactory
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // --- INSTANCIA DEL VIEWMODEL ---
    // ACTUALIZACIÓN: Usamos la Factory para inyectar el repositorio desde el ServiceLocator.
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(ServiceLocator.authRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInputs()
        setupObservers() // Aquí gestionamos los StateFlow
        setupListeners()
    }

    private fun setupInputs() {
        // Escuchamos cambios y actualizamos el ViewModel
        binding.tilUsername.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onUsernameChanged(text.toString())
        }

        binding.tilPassword.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onPasswordChanged(text.toString())
        }
    }

    private fun setupObservers() {
        // ACTUALIZACIÓN: StateFlow requiere corrutinas y repeatOnLifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1. Observar estado del botón (habilitado/deshabilitado)
                launch {
                    viewModel.isLoginButtonEnabled.collect { isEnabled ->
                        binding.btnLogin.isEnabled = isEnabled
                    }
                }

                // 2. Observar el estado de la UI (Carga, Éxito, Error)
                launch {
                    viewModel.userUiState.collect { state ->
                        when (state) {
                            is UserUiState.Idle -> {
                                binding.progressBar.visibility = View.GONE
                            }
                            is UserUiState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                // Opcional: Desactivar botón mientras carga para evitar doble click
                                binding.btnLogin.isEnabled = false
                            }
                            is UserUiState.Authenticated -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(requireContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show()

                                // Navegación al éxito
                                findNavController().navigate(R.id.action_loginFragment_to_tabFragment)

                                // Importante: Resetear estado para evitar re-navegación al volver atrás (opcional)
                                viewModel.resetState()
                            }
                            is UserUiState.Error -> {
                                binding.progressBar.visibility = View.GONE
                                // Reactivar botón si fue desactivado en Loading
                                binding.btnLogin.isEnabled = true
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                                viewModel.resetState() // Volver a Idle
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            // 1. Capturamos el texto de la vista
            val email = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // 2. Pequeña validación antes de molestar a Firebase
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // 3. Pasamos los datos al ViewModel
                // Nota: En la Tarea 4 el método sugerido en el diagrama es signIn(email, password)
                viewModel.signIn(email, password)
            } else {
                // Feedback visual si están vacíos
                Toast.makeText(context, "Por favor, rellena los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvCreateAccount.setOnClickListener {
            // Esto está PERFECTO, asumiendo que el ID coincide con tu nav_graph
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}