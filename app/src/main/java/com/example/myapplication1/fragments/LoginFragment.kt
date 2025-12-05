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
import com.example.myapplication1.databinding.FragmentLoginBinding
import com.example.myapplication1.viewmodels.AuthViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Instancia del ViewModel
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInputs()     // 1. Escuchar lo que escribe el usuario
        setupObservers()  // 2. Reaccionar a los cambios del ViewModel
        setupListeners()  // 3. Clics de botones
    }

    private fun setupInputs() {
        // Enlazamos el evento de escribir con la función del ViewModel
        binding.tilUsername.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onUsernameChanged(text.toString())
        }

        binding.tilPassword.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onPasswordChanged(text.toString())
        }
    }

    private fun setupObservers() {
        // Observamos si el botón debe estar habilitado
        viewModel.isLoginButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.btnLogin.isEnabled = isEnabled
        }

        // Observamos el resultado del login para navegar
        viewModel.loginResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "¡Login correcto! Bienvenido.", Toast.LENGTH_SHORT).show()
                // Asegúrate de tener esta acción creada en tu nav_graph.xml
               findNavController().navigate(R.id.action_loginFragment_to_tabFragment)
            } else {
                Toast.makeText(context, "Credenciales incorrectas (admin/1234)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        // Botón Login
        binding.btnLogin.setOnClickListener {
            viewModel.performLogin()
        }

        // Botón "Crear cuenta" (Navegar al registro)
        binding.tvCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}