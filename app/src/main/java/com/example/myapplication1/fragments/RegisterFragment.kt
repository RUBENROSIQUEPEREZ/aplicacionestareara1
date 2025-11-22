package com.example.myapplication1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication1.R
import com.example.myapplication1.databinding.FragmentRegisterBinding
import com.example.myapplication1.viewmodels.NewUserViewModel

class RegisterFragment : Fragment() {

    // Configuración de View Binding
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // Instancia del ViewModel usando el delegado 'by viewModels()'
    private val viewModel: NewUserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInputs()
        setupObservers()
        setupButtons()
    }

    // 1. Capturar lo que escribe el usuario
    private fun setupInputs() {
        // Usuario
        binding.tilRegisterUsername.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.username = text.toString()
        }

        // Contraseña
        binding.tilRegisterPassword.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.password = text.toString()
        }

        // Confirmar Contraseña
        binding.tilRegisterConfirmPassword.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.confirmPassword = text.toString()
        }
    }

    // 2. Observar los cambios del ViewModel (LiveData)
    private fun setupObservers() {
        // A. Habilitar/Deshabilitar botón
        viewModel.isRegisterButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.btnRegister.isEnabled = isEnabled
        }

        // B. Mostrar/Ocultar error de contraseña
        viewModel.passwordMatchError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                // Mostramos el error en el TextInputLayout de confirmar contraseña
                binding.tilRegisterConfirmPassword.error = getString(R.string.error_password_mismatch)
            } else {
                // Quitamos el error (null)
                binding.tilRegisterConfirmPassword.error = null
            }
        }

        // C. Navegar si el registro es exitoso
        viewModel.registrationSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()
                // Navegar de vuelta al Login limpiando la pila (definido en nav_graph)
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    // 3. Configurar clic del botón
    private fun setupButtons() {
        binding.btnRegister.setOnClickListener {
            viewModel.createAccount()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}